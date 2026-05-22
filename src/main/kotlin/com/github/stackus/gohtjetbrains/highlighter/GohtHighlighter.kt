package com.github.stackus.gohtjetbrains.highlighter

import com.github.stackus.gohtjetbrains.lexer.GohtLexer
import com.github.stackus.gohtjetbrains.lexer.GohtTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class GohtHighlighter : SyntaxHighlighterBase() {

    companion object {
        val DIRECTIVE_KEY      = createTextAttributesKey("GOHT_DIRECTIVE",      DefaultLanguageHighlighterColors.KEYWORD)
        val FUNC_NAME_KEY      = createTextAttributesKey("GOHT_FUNC_NAME",      DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
        val FUNC_PARAMS_KEY    = createTextAttributesKey("GOHT_FUNC_PARAMS",    DefaultLanguageHighlighterColors.PARAMETER)
        val BLOCK_BRACE_KEY    = createTextAttributesKey("GOHT_BLOCK_BRACE",    DefaultLanguageHighlighterColors.BRACES)
        val HAML_TAG_KEY       = createTextAttributesKey("GOHT_HAML_TAG",       DefaultLanguageHighlighterColors.MARKUP_TAG)
        val SLIM_TAG_KEY       = createTextAttributesKey("GOHT_SLIM_TAG",       DefaultLanguageHighlighterColors.MARKUP_TAG)
        val DOCTYPE_KEY        = createTextAttributesKey("GOHT_DOCTYPE",        DefaultLanguageHighlighterColors.KEYWORD)
        val CLASS_KEY          = createTextAttributesKey("GOHT_CLASS",          DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE)
        val ID_KEY             = createTextAttributesKey("GOHT_ID",             DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE)
        val ATTR_NAME_KEY      = createTextAttributesKey("GOHT_ATTR_NAME",      DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE)
        val ATTR_VALUE_KEY     = createTextAttributesKey("GOHT_ATTR_VALUE",     DefaultLanguageHighlighterColors.STRING)
        val ATTR_OPEN_KEY      = createTextAttributesKey("GOHT_ATTR_OPEN",      DefaultLanguageHighlighterColors.BRACES)
        val ATTR_CLOSE_KEY     = createTextAttributesKey("GOHT_ATTR_CLOSE",     DefaultLanguageHighlighterColors.BRACES)
        val ATTR_SEP_KEY       = createTextAttributesKey("GOHT_ATTR_SEP",       DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val ATTR_COMMA_KEY     = createTextAttributesKey("GOHT_ATTR_COMMA",     DefaultLanguageHighlighterColors.COMMA)
        val OUTPUT_FLAG_KEY    = createTextAttributesKey("GOHT_OUTPUT_FLAG",    DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val SILENT_FLAG_KEY    = createTextAttributesKey("GOHT_SILENT_FLAG",    DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val INTERP_BRACE_KEY   = createTextAttributesKey("GOHT_INTERP_BRACE",  DefaultLanguageHighlighterColors.BRACES)
        val INTERP_FORMAT_KEY  = createTextAttributesKey("GOHT_INTERP_FORMAT", DefaultLanguageHighlighterColors.KEYWORD)
        val AT_RENDER_KEY      = createTextAttributesKey("GOHT_AT_RENDER",      DefaultLanguageHighlighterColors.KEYWORD)
        val FILTER_NAME_KEY    = createTextAttributesKey("GOHT_FILTER_NAME",    DefaultLanguageHighlighterColors.LABEL)
        val EGO_TAG_KEY        = createTextAttributesKey("GOHT_EGO_TAG",        DefaultLanguageHighlighterColors.MARKUP_TAG)
        val COMMENT_KEY        = createTextAttributesKey("GOHT_COMMENT",        DefaultLanguageHighlighterColors.LINE_COMMENT)
        val TEXT_KEY           = createTextAttributesKey("GOHT_TEXT",           DefaultLanguageHighlighterColors.STRING)

        private val TOKEN_MAP = mapOf(
            GohtTokenTypes.DIRECTIVE        to arrayOf(DIRECTIVE_KEY),
            GohtTokenTypes.FUNC_NAME        to arrayOf(FUNC_NAME_KEY),
            GohtTokenTypes.BLOCK_OPEN       to arrayOf(BLOCK_BRACE_KEY),
            GohtTokenTypes.BLOCK_CLOSE      to arrayOf(BLOCK_BRACE_KEY),
            GohtTokenTypes.HAML_TAG         to arrayOf(HAML_TAG_KEY),
            GohtTokenTypes.SLIM_TAG         to arrayOf(SLIM_TAG_KEY),
            GohtTokenTypes.HAML_DOCTYPE     to arrayOf(DOCTYPE_KEY),
            GohtTokenTypes.SLIM_DOCTYPE     to arrayOf(DOCTYPE_KEY),
            GohtTokenTypes.CLASS_SHORTHAND  to arrayOf(CLASS_KEY),
            GohtTokenTypes.ID_SHORTHAND     to arrayOf(ID_KEY),
            GohtTokenTypes.ATTR_NAME        to arrayOf(ATTR_NAME_KEY),
            GohtTokenTypes.ATTR_OPEN        to arrayOf(ATTR_OPEN_KEY),
            GohtTokenTypes.ATTR_CLOSE       to arrayOf(ATTR_CLOSE_KEY),
            GohtTokenTypes.ATTR_SEP         to arrayOf(ATTR_SEP_KEY),
            GohtTokenTypes.ATTR_COMMA       to arrayOf(ATTR_COMMA_KEY),
            GohtTokenTypes.INTERP_START     to arrayOf(INTERP_BRACE_KEY),
            GohtTokenTypes.INTERP_FORMAT    to arrayOf(INTERP_FORMAT_KEY),
            GohtTokenTypes.INTERP_END       to arrayOf(INTERP_BRACE_KEY),
            GohtTokenTypes.OUTPUT_FLAG           to arrayOf(OUTPUT_FLAG_KEY),
            GohtTokenTypes.SILENT_FLAG           to arrayOf(SILENT_FLAG_KEY),
            GohtTokenTypes.UNESCAPE_FLAG         to arrayOf(OUTPUT_FLAG_KEY),
            GohtTokenTypes.UNESCAPE_OUTPUT_FLAG  to arrayOf(OUTPUT_FLAG_KEY),
            GohtTokenTypes.OBJ_REF_OPEN          to arrayOf(ATTR_OPEN_KEY),
            GohtTokenTypes.OBJ_REF_CLOSE         to arrayOf(ATTR_CLOSE_KEY),
            GohtTokenTypes.AT_RENDER        to arrayOf(AT_RENDER_KEY),
            GohtTokenTypes.AT_CHILDREN      to arrayOf(AT_RENDER_KEY),
            GohtTokenTypes.AT_SLOT          to arrayOf(AT_RENDER_KEY),
            GohtTokenTypes.FILTER_NAME      to arrayOf(FILTER_NAME_KEY),
            GohtTokenTypes.EGO_OPEN_CODE    to arrayOf(EGO_TAG_KEY),
            GohtTokenTypes.EGO_OPEN_OUTPUT  to arrayOf(EGO_TAG_KEY),
            GohtTokenTypes.EGO_OPEN_RENDER  to arrayOf(EGO_TAG_KEY),
            GohtTokenTypes.EGO_TRIM_OPEN    to arrayOf(EGO_TAG_KEY),
            GohtTokenTypes.EGO_TRIM_CLOSE   to arrayOf(EGO_TAG_KEY),
            GohtTokenTypes.EGO_CLOSE        to arrayOf(EGO_TAG_KEY),
            GohtTokenTypes.COMMENT          to arrayOf(COMMENT_KEY),
            GohtTokenTypes.TEXT             to arrayOf(TEXT_KEY),
        )
    }

    override fun getHighlightingLexer(): Lexer = GohtLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        TOKEN_MAP[tokenType] ?: emptyArray()
}
