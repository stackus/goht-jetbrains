package com.github.stackus.gohtjetbrains.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class GohtHighlighter(lexer: GohtHighlightingLexer) : SyntaxHighlighterBase() {
    private val myLexer: GohtHighlightingLexer = lexer

    override fun getHighlightingLexer(): Lexer {
        return myLexer
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<out TextAttributesKey> {
        if (tokenType !is GohtElementType) return pack(null)

        val scope = tokenType.getScope().scopeName ?: ""

        if (scope.isEmpty()) return pack(null)

        // comments
        if (scope.startsWith("comment")) {
            if (scope.startsWith("comment.block")) return arrayOf(DefaultLanguageHighlighterColors.BLOCK_COMMENT)
            return arrayOf(DefaultLanguageHighlighterColors.LINE_COMMENT)
        }

        // strings
        if (scope.startsWith("string")) {
            if (scope.startsWith("string.quoted")) return arrayOf(DefaultLanguageHighlighterColors.STRING)
            return arrayOf(DefaultLanguageHighlighterColors.STRING)
        }

        // punctuation
        if (scope.startsWith("punctuation")) {
            if (scope.startsWith("punctuation.definition.comment")) return arrayOf(DefaultLanguageHighlighterColors.LINE_COMMENT)
            if (scope.startsWith("punctuation.definition.string")) return arrayOf(DefaultLanguageHighlighterColors.STRING)

            if (scope.startsWith("punctuation.definition.doctype")) return arrayOf(DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL)
            if (scope.startsWith("punctuation.definition.tag.html")) return arrayOf(DefaultLanguageHighlighterColors.OPERATION_SIGN)
            if (scope.startsWith("punctuation.definition.tag")) return arrayOf(DefaultLanguageHighlighterColors.METADATA)

            return arrayOf(DefaultLanguageHighlighterColors.OPERATION_SIGN)
        }

        // keywords
        if (scope.startsWith("keyword")) {
            if (scope.startsWith("keyword.package")) return arrayOf(DefaultLanguageHighlighterColors.IDENTIFIER)
            if (scope.startsWith("keyword.operator")) return arrayOf(DefaultLanguageHighlighterColors.OPERATION_SIGN)
            return arrayOf(DefaultLanguageHighlighterColors.KEYWORD)
        }

        // constants
        if (scope.startsWith("constant")) {
            if (scope.startsWith("constant.numeric")) return arrayOf(DefaultLanguageHighlighterColors.NUMBER)
            return arrayOf(DefaultLanguageHighlighterColors.CONSTANT)
        }

        // entities
        if (scope.startsWith("entity")) {
            if (scope.startsWith("entity.name.function.embedded")) return arrayOf(DefaultLanguageHighlighterColors.FUNCTION_CALL)
            if (scope.startsWith("entity.name.function")) return arrayOf(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
            if (scope.startsWith("entity.name.tag")) return arrayOf(DefaultLanguageHighlighterColors.MARKUP_TAG)
            if (scope.startsWith("entity.name.import")) return arrayOf(DefaultLanguageHighlighterColors.STRING)
            if (scope.startsWith("entity.name")) return arrayOf(DefaultLanguageHighlighterColors.KEYWORD)
            if (scope.startsWith("entity.other.attribute-name")) return arrayOf(DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE)
            return arrayOf(DefaultLanguageHighlighterColors.IDENTIFIER)
        }

        // variables
        if (scope.startsWith("variable")) {
            if (scope.startsWith("variable.parameter")) return arrayOf(DefaultLanguageHighlighterColors.PARAMETER)
            if (scope.startsWith("variable.other")) return arrayOf(DefaultLanguageHighlighterColors.IDENTIFIER)
            return arrayOf(DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
        }

        // storage
        if (scope.startsWith("storage.type")) return arrayOf(DefaultLanguageHighlighterColors.KEYWORD)

        // support
        if (scope.startsWith("support")) {
            if (scope.startsWith("support.function")) return arrayOf(DefaultLanguageHighlighterColors.LABEL)
            return arrayOf(DefaultLanguageHighlighterColors.KEYWORD)
        }

        return pack(null)
    }
}