package dev.crossemmet.engine

enum class NumericKind { LENGTH, NUMBER, INTEGER, TIME, ANGLE, NONE }

data class PropertyDefinition(val name: String, val kind: NumericKind) {
    val abbreviation: String = name.split('-').joinToString("") { it.first().toString() }
    val defaultUnit: String get() = when (kind) {
        NumericKind.LENGTH -> "px"
        NumericKind.TIME -> "ms"
        NumericKind.ANGLE -> "deg"
        else -> ""
    }
}

data class ExpansionCandidate(val property: PropertyDefinition, val declaration: String, val caretOffset: Int)

object ExpansionEngine {
    private val syntax = Regex("^([a-z]+)([+-]?(?:[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+))?([a-z%]*)$")
    private val lengths = setOf("px", "rem", "em", "ex", "ch", "cap", "ic", "lh", "rlh", "vw", "vh", "vi", "vb", "vmin", "vmax", "svw", "svh", "svi", "svb", "svmin", "svmax", "lvw", "lvh", "lvi", "lvb", "lvmin", "lvmax", "dvw", "dvh", "dvi", "dvb", "dvmin", "dvmax", "cqw", "cqh", "cqi", "cqb", "cqmin", "cqmax", "cm", "mm", "q", "in", "pt", "pc", "%")

    fun expand(input: String): List<ExpansionCandidate> {
        val match = syntax.matchEntire(input) ?: return emptyList()
        val (alias, number, unit) = match.destructured
        if (number.isEmpty() && unit.isNotEmpty()) return emptyList()
        return PropertyCatalog.properties.filter { it.abbreviation == alias }.mapNotNull { property ->
            if (number.isNotEmpty() && !accepts(property.kind, number, unit)) return@mapNotNull null
            val value = if (number.isEmpty()) "" else number + when {
                unit.isNotEmpty() -> unit
                number.toBigDecimal().signum() == 0 && property.kind == NumericKind.LENGTH -> ""
                else -> property.defaultUnit
            }
            val prefix = "${property.name}: "
            ExpansionCandidate(property, "$prefix$value;", prefix.length + value.length)
        }
    }

    private fun accepts(kind: NumericKind, number: String, unit: String): Boolean = when (kind) {
        NumericKind.LENGTH -> unit.isEmpty() || unit in lengths
        NumericKind.NUMBER -> unit.isEmpty()
        NumericKind.INTEGER -> unit.isEmpty() && number.toBigDecimal().stripTrailingZeros().scale() <= 0
        NumericKind.TIME -> unit.isEmpty() || unit in setOf("s", "ms")
        NumericKind.ANGLE -> unit.isEmpty() || unit in setOf("deg", "rad", "grad", "turn")
        NumericKind.NONE -> false
    }
}
