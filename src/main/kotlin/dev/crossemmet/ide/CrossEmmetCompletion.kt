package dev.crossemmet.ide

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.AutoCompletionPolicy
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
                target.restartCompletionOnAnyPrefixChange()
                candidates.forEach { candidate ->
                    target.addElement(PrioritizedLookupElement.withPriority(AutoCompletionPolicy.NEVER_AUTOCOMPLETE.applyPolicy(LookupElementBuilder.create(candidate, candidate.declaration)
                        .withLookupString(slot.text)
                        .withPresentableText(candidate.declaration)
                        .withTypeText("CrossEmmet")
                        .withInsertHandler { insertion, _ ->
                            insertion.setAddCompletionChar(false)
                            ExpansionInsertion.insert(insertion.editor, insertion.startOffset, insertion.tailOffset, candidate)
                        }), if (candidate.property.name == "width" || candidate.property.name == "height") 110.0 else 100.0))
                }
                if (candidates.isNotEmpty()) result.stopHere()
            }
        })
    }
}
