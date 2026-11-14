package dev.crossemmet.ide

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class CrossEmmetTypedHandler : TypedHandlerDelegate() {
    override fun checkAutoPopup(charTyped: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file.language.id !in setOf("CSS", "SCSS") || editor.caretModel.caretCount != 1 ||
            !(charTyped.isLetterOrDigit() || charTyped in ".%+-")) return Result.CONTINUE
        AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
        return Result.CONTINUE
    }
}
