package dev.crossemmet.ide

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import dev.crossemmet.engine.ExpansionEngine

class CrossEmmetCompletion : CompletionContributor() {
    init {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
                if (parameters.editor.caretModel.caretCount != 1) return
                val slot = AbbreviationContext.find(parameters.originalFile, parameters.offset) ?: return
                val candidates = ExpansionEngine.expand(slot.text)
                val target = result.withPrefixMatcher(slot.text)
                candidates.forEach { candidate ->
                    target.addElement(LookupElementBuilder.create(candidate, slot.text)
                        .withPresentableText(candidate.declaration)
                        .withTypeText("CrossEmmet")
                        .withInsertHandler { insertion, _ ->
                            insertion.setAddCompletionChar(false)
                            ExpansionInsertion.insert(insertion.editor, insertion.startOffset, insertion.tailOffset, candidate)
                        })
                }
            }
        })
    }
}
