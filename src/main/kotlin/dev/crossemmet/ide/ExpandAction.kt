package dev.crossemmet.ide

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiDocumentManager
import dev.crossemmet.engine.ExpansionCandidate
import dev.crossemmet.engine.ExpansionEngine

class ExpandAction : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val file = e.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isEnabled = editor != null && file != null && editor.caretModel.caretCount == 1 &&
            AbbreviationContext.find(file, editor.caretModel.offset)?.let { ExpansionEngine.expand(it.text).isNotEmpty() } == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        if (editor.caretModel.caretCount != 1) return
        val manager = PsiDocumentManager.getInstance(project)
        manager.commitDocument(editor.document)
        val file = manager.getPsiFile(editor.document) ?: return
        val slot = AbbreviationContext.find(file, editor.caretModel.offset) ?: return
        val candidates = ExpansionEngine.expand(slot.text)
        val stamp = editor.document.modificationStamp
        fun apply(candidate: ExpansionCandidate) {
            if (editor.isDisposed || editor.document.modificationStamp != stamp || editor.caretModel.caretCount != 1 || editor.caretModel.offset != slot.end) return
            WriteCommandAction.runWriteCommandAction(project, "Expand CrossEmmet Abbreviation", null, Runnable {
                ExpansionInsertion.insert(editor, slot.start, slot.end, candidate)
            })
        }
        if (candidates.size == 1) apply(candidates.single())
        else if (candidates.isNotEmpty()) JBPopupFactory.getInstance().createPopupChooserBuilder(candidates)
            .setTitle("CrossEmmet Expansion")
            .setRenderer(object : javax.swing.DefaultListCellRenderer() {
                override fun getListCellRendererComponent(
                    list: javax.swing.JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean
                ): java.awt.Component = super.getListCellRendererComponent(
                    list, (value as? ExpansionCandidate)?.declaration, index, isSelected, cellHasFocus
                )
            })
            .setItemChosenCallback { apply(it) }
            .createPopup().showInBestPositionFor(editor)
    }
}
