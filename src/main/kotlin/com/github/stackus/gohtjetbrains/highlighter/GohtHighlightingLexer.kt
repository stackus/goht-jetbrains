package com.github.stackus.gohtjetbrains.highlighter

import com.intellij.psi.tree.IElementType
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateElementType
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateHighlightingLexer

class GohtHighlightingLexer : TextMateHighlightingLexer(getTextMateLanguageDescriptor(), 20000) {
    override fun getTokenType(): IElementType? {
        val tt = super.getTokenType() ?: return null
        return GohtElementType((tt as TextMateElementType).scope)
    }
}