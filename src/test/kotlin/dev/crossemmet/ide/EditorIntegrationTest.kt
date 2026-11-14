package dev.crossemmet.ide

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.openapi.command.WriteCommandAction
import dev.crossemmet.engine.ExpansionEngine
import dev.crossemmet.engine.ExpansionCandidate
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.application.ApplicationManager

class EditorIntegrationTest : BasePlatformTestCase() {
    fun testAutomaticWidthCompletion() = checkAutomaticCompletion("w12", "width: 12px")
    fun testAutomaticKeywordCompletion() = checkAutomaticCompletion("tac", "text-align: center")
    fun testAutomaticFlexCompletion() = checkAutomaticCompletion("df", "display: flex")
    fun testAutomaticJustifyCompletion() = checkAutomaticCompletion("jcc", "justify-content: center")
    private fun checkAutomaticCompletion(abbreviation: String, declaration: String) {
        myFixture.configureByText("test.css", "a { $abbreviation<caret>; }")
        val items = myFixture.complete(com.intellij.codeInsight.completion.CompletionType.BASIC, 0)
        val item = items?.firstOrNull { (it.`object` as? ExpansionCandidate)?.declaration == "$declaration;" }
        assertNotNull("Automatic completion should offer $abbreviation", item)
        assertEquals("Enter should accept the expected expansion", item, myFixture.lookup.currentItem)
        myFixture.finishLookup('\n')
        myFixture.checkResult("a { $declaration<caret>; }")
    }
    fun testCssInsertion() = checkExpansion("test.css", "a { mi16<caret> }", "a { margin-inline: 16px<caret>; }")
    fun testScssNestedInsertion() = checkExpansion("test.scss", ".a { .b { mis-8<caret>; } }", ".a { .b { margin-inline-start: -8px<caret>; } }")
    fun testXcssInsertionWithExternalCssAssociation() {
        val manager = FileTypeManager.getInstance()
        val original = manager.getFileTypeByExtension("xcss")
        val css = manager.getFileTypeByExtension("css")
        ApplicationManager.getApplication().runWriteAction { manager.associateExtension(css, "xcss") }
        try {
            checkExpansion("test.xcss", "a { mi1.5rem<caret> }", "a { margin-inline: 1.5rem<caret>; }")
        } finally {
            ApplicationManager.getApplication().runWriteAction {
                manager.removeAssociatedExtension(css, "xcss")
                if (original != com.intellij.openapi.fileTypes.UnknownFileType.INSTANCE) manager.associateExtension(original, "xcss")
            }
        }
    }
    fun testEmptyValueCaret() = checkExpansion("test.css", "a { mis<caret> }", "a { margin-inline-start: <caret>; }")
    fun testSpacedSemicolon() = checkExpansion("test.css", "a { mis8<caret> ; }", "a { margin-inline-start: 8px<caret> ; }")
    fun testActionAndUndo() {
        myFixture.configureByText("test.css", "a { pbs8<caret>; }")
        myFixture.testAction(ExpandAction())
        myFixture.checkResult("a { padding-block-start: 8px<caret>; }")
        myFixture.performEditorAction(IdeActions.ACTION_UNDO)
        myFixture.checkResult("a { pbs8<caret>; }")
    }
    fun testXcssHasNoPluginAssociation() {
        val manager = FileTypeManager.getInstance()
        assertFalse(manager.getFileTypeByExtension("xcss").name == "CSS")
        myFixture.configureByText("test.xcss", "a { mis8<caret>; }")
        assertNull(AbbreviationContext.find(myFixture.file, myFixture.caretOffset))
    }
    fun testExcludedContexts() {
        listOf("/* { mi16<caret> */", "a { color: mi16<caret>; }", "mi16<caret> {}", "a { content: '{ mi16<caret>'; }", "a { .mi16<caret> {} }", "a { #{mi16<caret>} }").forEach {
            myFixture.configureByText("test.scss", it)
            assertNull(it, AbbreviationContext.find(myFixture.file, myFixture.caretOffset))
        }
    }
    fun testCompletion() {
        myFixture.configureByText("test.css", "a { mis8<caret>; }")
        val items = myFixture.completeBasic()
        val item = items?.firstOrNull { it.`object` is ExpansionCandidate }
        if (item != null) {
            myFixture.lookup.currentItem = item
            myFixture.finishLookup('\n')
        }
        myFixture.checkResult("a { margin-inline-start: 8px<caret>; }")
    }
    private fun checkExpansion(name: String, before: String, after: String) {
        myFixture.configureByText(name, before)
        val slot = checkNotNull(AbbreviationContext.find(myFixture.file, myFixture.caretOffset))
        val candidate = ExpansionEngine.expand(slot.text).first { it.property.name.startsWith("margin-inline") }
        WriteCommandAction.runWriteCommandAction(project) {
            ExpansionInsertion.insert(myFixture.editor, slot.start, slot.end, candidate)
        }
        myFixture.checkResult(after)
    }
}
