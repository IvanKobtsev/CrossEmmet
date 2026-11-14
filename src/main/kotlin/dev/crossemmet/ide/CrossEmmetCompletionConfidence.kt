package dev.crossemmet.ide

import com.intellij.codeInsight.completion.CompletionConfidence
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.ThreeState
import dev.crossemmet.engine.ExpansionEngine

class CrossEmmetCompletionConfidence : CompletionConfidence() {
    override fun shouldSkipAutopopup(editor: com.intellij.openapi.editor.Editor, contextElement: PsiElement, psiFile: PsiFile, offset: Int): ThreeState {
        val slot = AbbreviationContext.find(psiFile.originalFile, offset) ?: return ThreeState.UNSURE
        return if (ExpansionEngine.expand(slot.text).isNotEmpty()) ThreeState.NO else ThreeState.UNSURE
    }
}
