package dev.crossemmet.ide

import com.intellij.psi.PsiFile
import com.intellij.psi.css.CssDeclaration
import com.intellij.psi.css.CssBlock
import com.intellij.psi.util.PsiTreeUtil

data class AbbreviationContext(val start: Int, val end: Int, val text: String) {
    companion object {
        fun find(file: PsiFile, offset: Int): AbbreviationContext? {
            if (file.language.id !in setOf("CSS", "SCSS")) return null
            val text = file.viewProvider.document?.charsSequence ?: return null
            if (offset <= 0 || offset > text.length) return null
            var start = offset
            while (start > 0 && (text[start - 1].isLetterOrDigit() || text[start - 1] in ".%+-")) start--
            if (start == offset) return null
            // A shorthand must occupy an entire declaration slot, never a selector or value.
            var previous = start - 1
            while (previous >= 0 && text[previous].isWhitespace()) previous--
            if (previous < 0 || text[previous] !in "{;}") return null
            val leaf = file.findElementAt(start) ?: return null
            if (PsiTreeUtil.getParentOfType(leaf, CssBlock::class.java, false) == null) return null
            val declaration = PsiTreeUtil.getParentOfType(leaf, CssDeclaration::class.java, false)
            if (declaration != null && ':' in declaration.text.take(start - declaration.textRange.startOffset)) return null
            // CSS/SCSS lexical scan guards comments, strings, interpolation and nested selectors.
            if (!isDeclarationSlot(text, start, offset)) return null
            return AbbreviationContext(start, offset, text.subSequence(start, offset).toString())
        }

        private fun isDeclarationSlot(text: CharSequence, start: Int, end: Int): Boolean {
            var quote: Char? = null
            var comment = false
            var lineComment = false
            var interpolation = 0
            var i = 0
            while (i < start) {
                val c = text[i]
                val next = text.getOrNull(i + 1)
                when {
                    lineComment -> if (c == '\n') lineComment = false
                    comment -> if (c == '*' && next == '/') { comment = false; i++ }
                    quote != null -> if (c == '\\') i++ else if (c == quote) quote = null
                    c == '/' && next == '*' -> { comment = true; i++ }
                    c == '/' && next == '/' -> { lineComment = true; i++ }
                    c == '\'' || c == '"' -> quote = c
                    c == '#' && next == '{' -> { interpolation++; i++ }
                    c == '}' && interpolation > 0 -> interpolation--
                }
                i++
            }
            if (comment || lineComment || quote != null || interpolation > 0) return false
            // Reject partial tokens and selectors that continue after the caret.
            var after = end
            while (after < text.length && text[after].isWhitespace()) after++
            return after == text.length || text[after] in ";}"
        }
    }
}
