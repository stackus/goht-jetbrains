package com.github.stackus.gohtjetbrains.lexer

import com.github.stackus.gohtjetbrains.highlighter.GohtHighlighter
import com.intellij.psi.tree.IElementType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GohtLexerTest {

    @Test
    fun `template body closing brace is only recognized on standalone closing line`() {
        val tokens = lex(
            """
            package hello

            @goht World() {
                %p Text with } inside
                :css
                    body {
                        color: white;
                    }
            }
            """.trimIndent()
        )

        val blockCloses = tokens.filter { it.type == GohtTokenTypes.BLOCK_CLOSE }

        assertEquals(listOf("}"), blockCloses.map { it.text })
        assertTrue(tokens.any { it.type == GohtTokenTypes.TEXT && it.text.contains("}") })
        assertTrue(tokens.any { it.type == GohtTokenTypes.FILTER_CONTENT && it.text.contains("body {") })
    }

    @Test
    fun `haml code and output markers are separate from go code`() {
        val tokens = lex(
            """
            @haml HamlWorld() {
                %p.term= term
                - for _, term := range terms
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.HAML_TAG, "%p")
        assertHasToken(tokens, GohtTokenTypes.CLASS_SHORTHAND, ".term")
        assertHasToken(tokens, GohtTokenTypes.OUTPUT_FLAG, "=")
        assertHasToken(tokens, GohtTokenTypes.GO_EXPR, " term")
        assertHasToken(tokens, GohtTokenTypes.SILENT_FLAG, "-")
        assertHasToken(tokens, GohtTokenTypes.GO_CODE, " for _, term := range terms")
    }

    @Test
    fun `slim code output and no parens html attributes are tokenized`() {
        val tokens = lex(
            """
            @slim SlimWorld() {
                p.term= term
                - for _, term := range terms
                div class="foo" id=#{id} Content
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "p")
        assertHasToken(tokens, GohtTokenTypes.CLASS_SHORTHAND, ".term")
        assertHasToken(tokens, GohtTokenTypes.OUTPUT_FLAG, "=")
        assertHasToken(tokens, GohtTokenTypes.GO_EXPR, " term")
        assertHasToken(tokens, GohtTokenTypes.SILENT_FLAG, "-")
        assertHasToken(tokens, GohtTokenTypes.GO_CODE, " for _, term := range terms")
        assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, "class")
        assertHasToken(tokens, GohtTokenTypes.ATTR_VALUE, "\"foo\"")
        assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, "id")
        assertHasToken(tokens, GohtTokenTypes.INTERP_START, "#{")
        assertHasToken(tokens, GohtTokenTypes.INTERP_CONTENT, "id")
        assertHasToken(tokens, GohtTokenTypes.INTERP_END, "}")
        assertHasToken(tokens, GohtTokenTypes.TEXT, " Content")
    }

    @Test
    fun `html style attributes and dynamic values are tokenized`() {
        val tokens = lex(
            """
            @haml HamlHtmlAttributes() {
                %div(class="foo" id=#{id}) Content
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.ATTR_OPEN, "(")
        assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, "class")
        assertHasToken(tokens, GohtTokenTypes.ATTR_SEP, "=")
        assertHasToken(tokens, GohtTokenTypes.ATTR_VALUE, "\"foo\"")
        assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, "id")
        assertHasToken(tokens, GohtTokenTypes.INTERP_START, "#{")
        assertHasToken(tokens, GohtTokenTypes.INTERP_CONTENT, "id")
        assertHasToken(tokens, GohtTokenTypes.INTERP_END, "}")
        assertHasToken(tokens, GohtTokenTypes.ATTR_CLOSE, ")")
        assertHasToken(tokens, GohtTokenTypes.TEXT, " Content")
    }

    @Test
    fun `ego html and go tags are separate tokens`() {
        val tokens = lex(
            """
            @ego EgoWorld() {
                <html>
                    <% for _, term := range terms { -%>
                        <p><%= term %></p>
                    <%- } -%>
                </html>
            }
            """.trimIndent()
        )

        assertTrue(tokens.any { it.type == GohtTokenTypes.EGO_HTML && it.text.contains("<html>") })
        assertHasToken(tokens, GohtTokenTypes.EGO_OPEN_CODE, "<%")
        assertHasToken(tokens, GohtTokenTypes.GO_CODE, " for _, term := range terms { ")
        assertHasToken(tokens, GohtTokenTypes.EGO_TRIM_CLOSE, "-%>")
        assertHasToken(tokens, GohtTokenTypes.EGO_OPEN_OUTPUT, "<%=")
        assertHasToken(tokens, GohtTokenTypes.GO_CODE, " term ")
        assertHasToken(tokens, GohtTokenTypes.EGO_CLOSE, "%>")
        assertHasToken(tokens, GohtTokenTypes.EGO_TRIM_OPEN, "<%-")
    }

    @Test
    fun `embedded language host tokens do not override injected highlighters`() {
        val highlighter = GohtHighlighter()

        val injectedHosts = listOf(
            GohtTokenTypes.GO_FRAGMENT,
            GohtTokenTypes.GO_CODE,
            GohtTokenTypes.GO_EXPR,
            GohtTokenTypes.FUNC_PARAMS,
            GohtTokenTypes.INTERP_CONTENT,
            GohtTokenTypes.ATTR_VALUE,
            GohtTokenTypes.FILTER_CONTENT,
            GohtTokenTypes.EGO_HTML,
        )

        injectedHosts.forEach { tokenType ->
            assertEquals(emptyList(), highlighter.getTokenHighlights(tokenType).toList(), tokenType.toString())
        }
        assertTrue(highlighter.getTokenHighlights(GohtTokenTypes.HAML_TAG).isNotEmpty())
        assertTrue(highlighter.getTokenHighlights(GohtTokenTypes.OUTPUT_FLAG).isNotEmpty())
        assertTrue(highlighter.getTokenHighlights(GohtTokenTypes.SILENT_FLAG).isNotEmpty())
    }

    private fun lex(source: String): List<Token> {
        val lexer = GohtLexer()
        lexer.start(source, 0, source.length, 0)

        val tokens = mutableListOf<Token>()
        while (lexer.tokenType != null) {
            val type = lexer.tokenType ?: break
            tokens += Token(type, source.substring(lexer.tokenStart, lexer.tokenEnd))
            lexer.advance()
        }
        return tokens
    }

    private fun assertHasToken(tokens: List<Token>, type: IElementType, text: String) {
        assertTrue(
            tokens.any { it.type == type && it.text == text },
            "Expected token $type '$text', got:\n${tokens.joinToString("\n") { "${it.type}: '${it.text}'" }}"
        )
    }

    private data class Token(val type: IElementType, val text: String)
}
