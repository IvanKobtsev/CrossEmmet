package dev.crossemmet.ide

import com.intellij.openapi.editor.Editor
import dev.crossemmet.engine.ExpansionCandidate

object ExpansionInsertion {
    fun insert(editor: Editor, start: Int, end: Int, candidate: ExpansionCandidate) {
        val document = editor.document
        var next = end
        while (next < document.textLength && document.charsSequence[next].isWhitespace()) next++
        val existingSemicolon = document.charsSequence.getOrNull(next) == ';'
        val replacement = if (existingSemicolon) candidate.declaration.dropLast(1) else candidate.declaration
        document.replaceString(start, end, replacement)
        editor.caretModel.moveToOffset(start + candidate.caretOffset)
    }
}
