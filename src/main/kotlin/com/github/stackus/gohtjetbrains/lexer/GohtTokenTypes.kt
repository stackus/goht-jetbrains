package com.github.stackus.gohtjetbrains.lexer

import com.github.stackus.gohtjetbrains.GohtLanguage
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

object GohtTokenTypes {
    // Top-level Go code between template blocks
    val GO_FRAGMENT = IElementType("GOHT_GO_FRAGMENT", GohtLanguage)

    // Directive header tokens (@goht FuncName(params) {)
    val DIRECTIVE    = IElementType("GOHT_DIRECTIVE",   GohtLanguage)
    val FUNC_NAME    = IElementType("GOHT_FUNC_NAME",   GohtLanguage)
    val FUNC_PARAMS  = IElementType("GOHT_FUNC_PARAMS", GohtLanguage)
    val BLOCK_OPEN   = IElementType("GOHT_BLOCK_OPEN",  GohtLanguage)
    val BLOCK_CLOSE  = IElementType("GOHT_BLOCK_CLOSE", GohtLanguage)

    // Shared template tokens
    val INDENT  = IElementType("GOHT_INDENT",  GohtLanguage)
    val NEWLINE = IElementType("GOHT_NEWLINE", GohtLanguage)
    val TEXT    = IElementType("GOHT_TEXT",    GohtLanguage)

    // Go code embedded in templates
    val GO_CODE = IElementType("GOHT_GO_CODE", GohtLanguage)
    val GO_EXPR = IElementType("GOHT_GO_EXPR", GohtLanguage)

    // Interpolation #{...}
    val INTERP_START   = IElementType("GOHT_INTERP_START",   GohtLanguage)
    val INTERP_FORMAT  = IElementType("GOHT_INTERP_FORMAT",  GohtLanguage)
    val INTERP_CONTENT = IElementType("GOHT_INTERP_CONTENT", GohtLanguage)
    val INTERP_END     = IElementType("GOHT_INTERP_END",     GohtLanguage)

    // @render, @children, @slot directives inside templates
    val AT_RENDER   = IElementType("GOHT_AT_RENDER",   GohtLanguage)
    val AT_CHILDREN = IElementType("GOHT_AT_CHILDREN", GohtLanguage)
    val AT_SLOT     = IElementType("GOHT_AT_SLOT",     GohtLanguage)

    // Haml/GoHT-dialect tokens
    val HAML_TAG        = IElementType("GOHT_HAML_TAG",    GohtLanguage)
    val HAML_DOCTYPE    = IElementType("GOHT_HAML_DOCTYPE", GohtLanguage)
    val CLASS_SHORTHAND = IElementType("GOHT_CLASS",       GohtLanguage)
    val ID_SHORTHAND    = IElementType("GOHT_ID",          GohtLanguage)
    val ATTR_OPEN       = IElementType("GOHT_ATTR_OPEN",   GohtLanguage)
    val ATTR_CLOSE      = IElementType("GOHT_ATTR_CLOSE",  GohtLanguage)
    val ATTR_NAME       = IElementType("GOHT_ATTR_NAME",   GohtLanguage)
    val ATTR_SEP        = IElementType("GOHT_ATTR_SEP",    GohtLanguage)
    val ATTR_VALUE      = IElementType("GOHT_ATTR_VALUE",  GohtLanguage)
    val ATTR_COMMA      = IElementType("GOHT_ATTR_COMMA",  GohtLanguage)
    val OUTPUT_FLAG          = IElementType("GOHT_OUTPUT_FLAG",          GohtLanguage)
    val SILENT_FLAG          = IElementType("GOHT_SILENT_FLAG",          GohtLanguage)
    val UNESCAPE_FLAG        = IElementType("GOHT_UNESCAPE_FLAG",        GohtLanguage)
    val UNESCAPE_OUTPUT_FLAG = IElementType("GOHT_UNESCAPE_OUTPUT_FLAG", GohtLanguage)
    val OBJ_REF_OPEN         = IElementType("GOHT_OBJ_REF_OPEN",         GohtLanguage)
    val OBJ_REF_CLOSE        = IElementType("GOHT_OBJ_REF_CLOSE",        GohtLanguage)

    // Slim-dialect tokens
    val SLIM_TAG     = IElementType("GOHT_SLIM_TAG",     GohtLanguage)
    val SLIM_DOCTYPE = IElementType("GOHT_SLIM_DOCTYPE", GohtLanguage)

    // Ego-dialect tokens (ERB-style)
    val EGO_OPEN_CODE   = IElementType("GOHT_EGO_OPEN_CODE",   GohtLanguage)
    val EGO_OPEN_OUTPUT = IElementType("GOHT_EGO_OPEN_OUTPUT", GohtLanguage)
    val EGO_OPEN_RENDER = IElementType("GOHT_EGO_OPEN_RENDER", GohtLanguage)
    val EGO_TRIM_OPEN   = IElementType("GOHT_EGO_TRIM_OPEN",   GohtLanguage)
    val EGO_TRIM_CLOSE  = IElementType("GOHT_EGO_TRIM_CLOSE",  GohtLanguage)
    val EGO_CLOSE       = IElementType("GOHT_EGO_CLOSE",       GohtLanguage)
    val EGO_HTML        = IElementType("GOHT_EGO_HTML",        GohtLanguage)

    // Filter blocks
    val FILTER_NAME    = IElementType("GOHT_FILTER_NAME",    GohtLanguage)
    val FILTER_CONTENT = IElementType("GOHT_FILTER_CONTENT", GohtLanguage)

    // Comment
    val COMMENT = IElementType("GOHT_COMMENT", GohtLanguage)

    val BAD_CHARACTER: IElementType = TokenType.BAD_CHARACTER
}
