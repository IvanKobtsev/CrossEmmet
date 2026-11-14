package dev.crossemmet.engine

import org.junit.Test
import kotlin.test.*

class ExpansionEngineTest {
    @Test fun keywordShortcuts() {
        mapOf("df" to "display: flex;", "jcc" to "justify-content: center;", "aic" to "align-items: center;", "fdc" to "flex-direction: column;", "posst" to "position: sticky;").forEach { (alias, expected) ->
            assertEquals(expected, ExpansionEngine.expand(alias).single().declaration)
        }
        KeywordCatalog.shortcuts.forEach {
            val result = ExpansionEngine.expand(it.abbreviation).single()
            assertEquals("${it.property}: ${it.value};", result.declaration)
            assertEquals(result.declaration.lastIndex, result.caretOffset)
            assertTrue(ExpansionEngine.expand(it.abbreviation + ":").isEmpty())
        }
        assertEquals("animation-iteration-count: 2;", ExpansionEngine.expand("aic2").single().declaration)
    }
    @Test fun textAlignCenter() {
        assertEquals("text-align: center;", ExpansionEngine.expand("tac").single().declaration)
        assertEquals("width: 12px;", ExpansionEngine.expand("w12").single { it.property.name == "width" }.declaration)
    }
    private fun declaration(input: String, property: String) = ExpansionEngine.expand(input).single { it.property.name == property }.declaration
    @Test fun logicalLengths() {
        assertEquals("margin-inline: 16px;", declaration("mi16", "margin-inline"))
        assertEquals("margin-inline-start: -8px;", declaration("mis-8", "margin-inline-start"))
        assertEquals("margin-inline: 1.5rem;", declaration("mi1.5rem", "margin-inline"))
        assertEquals("margin-inline: .5%;", declaration("mi.5%", "margin-inline"))
        assertEquals("margin-inline: 0;", declaration("mi0", "margin-inline"))
    }
    @Test fun numericCategories() {
        assertEquals("opacity: .5;", declaration("o.5", "opacity"))
        assertEquals("animation-duration: 100ms;", declaration("ad100", "animation-duration"))
        assertEquals("rotate: 90deg;", declaration("r90", "rotate"))
        assertEquals("animation-duration: 0ms;", declaration("ad0", "animation-duration"))
        assertEquals("rotate: 0deg;", declaration("r0", "rotate"))
        assertTrue(ExpansionEngine.expand("zi1.5").isEmpty())
        assertTrue(ExpansionEngine.expand("o1px").isEmpty())
        assertTrue(ExpansionEngine.expand("d1").isEmpty())
    }
    @Test fun collisionsAndCaret() {
        assertTrue(ExpansionEngine.expand("mi").size > 1)
        val candidate = ExpansionEngine.expand("mis").single { it.property.name == "margin-inline-start" }
        assertEquals("margin-inline-start: ;", candidate.declaration)
        assertEquals(candidate.declaration.indexOf(';'), candidate.caretOffset)
        assertEquals(ExpansionEngine.expand("mi"), ExpansionEngine.expand("mi"))
    }
    @Test fun invalidInputs() {
        listOf("", "MI16", "mi1..2", "mi16wat", "mi16+p8", "c#fff", "mi(1)", "unknown", "mi--2").forEach {
            assertTrue(ExpansionEngine.expand(it).isEmpty(), it)
        }
    }
    @Test fun catalogIntegrity() {
        val definitions = PropertyCatalog.properties
        assertEquals(definitions.size, definitions.map { it.name }.toSet().size)
        assertEquals(definitions.sortedBy { it.name }, definitions)
        assertTrue(definitions.size > 200)
    }
}
