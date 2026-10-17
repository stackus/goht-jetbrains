package com.github.stackus.gohtjetbrains.lexer

import com.github.stackus.gohtjetbrains.highlighter.GohtHighlighter
import com.intellij.psi.tree.IElementType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GohtLexerTest {

    @Test
    fun `top level go is emitted as one fragment per contiguous section`() {
        val tokens = lex(
            """
            package hello

            import "fmt"

            var terms = []string{"foo", "bar"}

            @goht World() {
            	%p Hello
            }

            func helper() {
            	fmt.Println(terms)
            }
            """.trimIndent()
        )

        val fragments = tokens.filter { it.type == GohtTokenTypes.GO_FRAGMENT }

        assertEquals(2, fragments.size)
        assertTrue(fragments[0].text.contains("package hello"))
        assertTrue(fragments[0].text.contains("var terms"))
        assertTrue(fragments[1].text.contains("func helper"))
    }

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
    fun `AT_SLOT token has keyword highlighting`() {
        val keys = GohtHighlighter().getTokenHighlights(GohtTokenTypes.AT_SLOT)
        assertTrue(keys.isNotEmpty(), "AT_SLOT must have at least one highlight key")
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

    @Test
    fun `interp format spec without comma is tokenized`() {
        val tokens = lex(
            """
            @goht World() {
            	%p #{%02d value}
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.INTERP_START, "#{")
        assertHasToken(tokens, GohtTokenTypes.INTERP_FORMAT, "%02d")
        assertHasToken(tokens, GohtTokenTypes.INTERP_CONTENT, " value")
        assertHasToken(tokens, GohtTokenTypes.INTERP_END, "}")
    }

    @Test
    fun `interp format spec with comma is tokenized`() {
        val tokens = lex(
            """
            @goht World() {
            	%p #{%02d, value}
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.INTERP_START, "#{")
        assertHasToken(tokens, GohtTokenTypes.INTERP_FORMAT, "%02d")
        assertHasToken(tokens, GohtTokenTypes.ATTR_COMMA, ",")
        assertHasToken(tokens, GohtTokenTypes.INTERP_CONTENT, " value")
        assertHasToken(tokens, GohtTokenTypes.INTERP_END, "}")
    }

    @Test
    fun `plain interp without format spec is still tokenized correctly`() {
        val tokens = lex(
            """
            @goht World() {
            	%p #{value}
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.INTERP_START, "#{")
        assertHasToken(tokens, GohtTokenTypes.INTERP_CONTENT, "value")
        assertHasToken(tokens, GohtTokenTypes.INTERP_END, "}")
        assertTrue(tokens.none { it.type == GohtTokenTypes.INTERP_FORMAT })
    }

    @Test
    fun `quoted attribute names are parsed as ATTR_NAME`() {
        val tokens = lex(
            """
            @haml World() {
            	%div{"data-foo": "bar"}
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, "\"data-foo\"")
        assertHasToken(tokens, GohtTokenTypes.ATTR_SEP, ":")
        assertHasToken(tokens, GohtTokenTypes.ATTR_VALUE, "\"bar\"")
    }

    @Test
    fun `backtick attribute names are parsed as ATTR_NAME`() {
        val tokens = lex(
            """
            @haml World() {
            	%div{`data-foo`: "bar"}
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, "`data-foo`")
        assertHasToken(tokens, GohtTokenTypes.ATTR_SEP, ":")
        assertHasToken(tokens, GohtTokenTypes.ATTR_VALUE, "\"bar\"")
    }

    @Test
    fun `ruby style attrs reject equals separator`() {
        val tokens = lex(
            """
            @haml World() {
            	%div{foo= "bar"}
            }
            """.trimIndent()
        )

        val seps = tokens.filter { it.type == GohtTokenTypes.ATTR_SEP }
        assertTrue(seps.none { it.text == "=" }, "Expected no '=' ATTR_SEP in Ruby-style attrs, got: $seps")
    }

    @Test
    fun `html style attrs reject colon separator`() {
        val tokens = lex(
            """
            @haml World() {
            	%div(foo: "bar")
            }
            """.trimIndent()
        )

        val seps = tokens.filter { it.type == GohtTokenTypes.ATTR_SEP }
        assertTrue(seps.none { it.text == ":" }, "Expected no ':' ATTR_SEP in HTML-style attrs, got: $seps")
    }

    @Test
    fun `ego css braces do not break html scan`() {
        val tokens = lex(
            """
            @ego EgoBodyCss() {
            	<html>
            		<style>
            			body {
            				color: white;
            			}
            		</style>
            	</html>
            }
            """.trimIndent()
        )

        val htmlTokens = tokens.filter { it.type == GohtTokenTypes.EGO_HTML }
        val combined = htmlTokens.joinToString("") { it.text }
        assertTrue(combined.contains("body {"), "CSS body selector should be inside EGO_HTML")
        assertTrue(combined.contains("color: white;"), "CSS property should be inside EGO_HTML")
        assertTrue(combined.contains("}"), "CSS closing brace should be inside EGO_HTML")
        val blockCloses = tokens.filter { it.type == GohtTokenTypes.BLOCK_CLOSE }
        assertEquals(1, blockCloses.size, "Should have exactly one BLOCK_CLOSE")
    }

    @Test
    fun `output line with @render directive emits three tokens`() {
        val tokens = lex(
            """
            @haml H() {
            	= @render someTemplate
            }
            """.trimIndent()
        )
        assertHasToken(tokens, GohtTokenTypes.OUTPUT_FLAG, "=")
        assertHasToken(tokens, GohtTokenTypes.AT_RENDER, " @render")
        assertHasToken(tokens, GohtTokenTypes.GO_EXPR, " someTemplate")
    }

    @Test
    fun `output line with @render no space emits three tokens`() {
        val tokens = lex(
            """
            @haml H() {
            	=@render someTemplate
            }
            """.trimIndent()
        )
        assertHasToken(tokens, GohtTokenTypes.OUTPUT_FLAG, "=")
        assertHasToken(tokens, GohtTokenTypes.AT_RENDER, "@render")
        assertHasToken(tokens, GohtTokenTypes.GO_EXPR, " someTemplate")
    }

    @Test
    fun `output line with @children emits two tokens`() {
        val tokens = lex(
            """
            @haml H() {
            	= @children
            }
            """.trimIndent()
        )
        assertHasToken(tokens, GohtTokenTypes.OUTPUT_FLAG, "=")
        assertHasToken(tokens, GohtTokenTypes.AT_CHILDREN, " @children")
    }

    @Test
    fun `output line with @slot emits three tokens`() {
        val tokens = lex(
            """
            @haml H() {
            	= @slot header
            }
            """.trimIndent()
        )
        assertHasToken(tokens, GohtTokenTypes.OUTPUT_FLAG, "=")
        assertHasToken(tokens, GohtTokenTypes.AT_SLOT, " @slot")
        assertHasToken(tokens, GohtTokenTypes.TEXT, " header")
    }

    @Test
    fun `slim output line with @render directive emits three tokens`() {
        val tokens = lex(
            """
            @slim H() {
            	= @render someTemplate
            }
            """.trimIndent()
        )
        assertHasToken(tokens, GohtTokenTypes.OUTPUT_FLAG, "=")
        assertHasToken(tokens, GohtTokenTypes.AT_RENDER, " @render")
        assertHasToken(tokens, GohtTokenTypes.GO_EXPR, " someTemplate")
    }

    @Test
    fun `slim output line with @slot emits three tokens`() {
        val tokens = lex(
            """
            @slim H() {
            	= @slot footer
            }
            """.trimIndent()
        )
        assertHasToken(tokens, GohtTokenTypes.OUTPUT_FLAG, "=")
        assertHasToken(tokens, GohtTokenTypes.AT_SLOT, " @slot")
        assertHasToken(tokens, GohtTokenTypes.TEXT, " footer")
    }

    @Test
    fun `silent flag line with @render content emits go code not directive tokens`() {
        val tokens = lex(
            """
            @haml H() {
            	- @render someTemplate
            }
            """.trimIndent()
        )
        assertHasToken(tokens, GohtTokenTypes.SILENT_FLAG, "-")
        assertHasToken(tokens, GohtTokenTypes.GO_CODE, " @render someTemplate")
        assertTrue(tokens.none { it.type == GohtTokenTypes.AT_RENDER }, "Silent flag path must not emit AT_RENDER")
    }

    @Test
    fun `ego render command splits into markup open and keyword tokens`() {
        val tokens = lex(
            """
            @ego EgoWorld() {
            	<%@render someTemplate() %>
            	<%@children %>
            	<%@slot header %>
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.EGO_OPEN_RENDER, "<%@")
        assertHasToken(tokens, GohtTokenTypes.AT_RENDER, "render")
        assertHasToken(tokens, GohtTokenTypes.AT_CHILDREN, "children")
        assertHasToken(tokens, GohtTokenTypes.AT_SLOT, "slot")
        assertTrue(tokens.none { it.type == GohtTokenTypes.EGO_OPEN_RENDER && it.text == "<%@render" },
            "EGO_OPEN_RENDER must not include the command word")
    }

    @Test
    fun `slim filter body returns to slim body state after filter block`() {
        val tokens = lex(
            """
            @slim SlimWorld() {
            	head
            		:css
            			body {
            				color: white;
            			}
            	body
            		p Hello
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.FILTER_NAME, ":css")
        assertTrue(tokens.any { it.type == GohtTokenTypes.FILTER_CONTENT && it.text.contains("color: white;") })
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "body")
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "p")
    }

    @Test
    fun `slim tags with inline text are tokenized as slim tag plus text`() {
        val tokens = lex(
            """
            @slim SlimWorld() {
            	title Hello World
            	h1 Heading Text
            	p Some paragraph content
            }
            """.trimIndent()
        )

        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "title")
        assertHasToken(tokens, GohtTokenTypes.TEXT, " Hello World")
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "h1")
        assertHasToken(tokens, GohtTokenTypes.TEXT, " Heading Text")
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "p")
        assertHasToken(tokens, GohtTokenTypes.TEXT, " Some paragraph content")
    }

    @Test
    fun `haml unescaped output flag is tokenized`() {
        val tokens = lex("@haml F() {\n\t%foo!= htmlStr\n}")
        assertHasToken(tokens, GohtTokenTypes.HAML_TAG, "%foo")
        assertHasToken(tokens, GohtTokenTypes.UNESCAPE_OUTPUT_FLAG, "!=")
        assertHasToken(tokens, GohtTokenTypes.GO_EXPR, " htmlStr")
    }

    @Test
    fun `haml unescaped text flag is tokenized`() {
        val tokens = lex("@haml F() {\n\t%foo! text\n}")
        assertHasToken(tokens, GohtTokenTypes.HAML_TAG, "%foo")
        assertHasToken(tokens, GohtTokenTypes.UNESCAPE_FLAG, "!")
        assertHasToken(tokens, GohtTokenTypes.TEXT, " text")
    }

    @Test
    fun `haml object reference is tokenized`() {
        val tokens = lex("@haml F() {\n\t%article[obj]\n}")
        assertHasToken(tokens, GohtTokenTypes.HAML_TAG, "%article")
        assertHasToken(tokens, GohtTokenTypes.OBJ_REF_OPEN, "[")
        assertHasToken(tokens, GohtTokenTypes.ATTR_VALUE, "obj")
        assertHasToken(tokens, GohtTokenTypes.OBJ_REF_CLOSE, "]")
    }

    @Test
    fun `haml object reference with prefix is tokenized`() {
        val tokens = lex("@haml F() {\n\t%article[obj, \"pre\"]\n}")
        assertHasToken(tokens, GohtTokenTypes.OBJ_REF_OPEN, "[")
        assertHasToken(tokens, GohtTokenTypes.ATTR_VALUE, "obj")
        assertHasToken(tokens, GohtTokenTypes.ATTR_COMMA, ",")
        assertHasToken(tokens, GohtTokenTypes.OBJ_REF_CLOSE, "]")
    }

    @Test
    fun `slim tag with output flag is tokenized`() {
        val tokens = lex("@slim F() {\n\tp= term\n}")
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "p")
        assertHasToken(tokens, GohtTokenTypes.OUTPUT_FLAG, "=")
        assertHasToken(tokens, GohtTokenTypes.GO_EXPR, " term")
        assertTrue(tokens.none { it.type == GohtTokenTypes.ATTR_NAME && it.text == "p" },
            "tag 'p' must not be ATTR_NAME")
    }

    @Test
    fun `slim tag with paren attributes is tokenized`() {
        val tokens = lex("@slim F() {\n\tinput(type=\"text\")\n}")
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "input")
        assertHasToken(tokens, GohtTokenTypes.ATTR_OPEN, "(")
        assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, "type")
        assertHasToken(tokens, GohtTokenTypes.ATTR_SEP, "=")
        assertHasToken(tokens, GohtTokenTypes.ATTR_CLOSE, ")")
    }

    @Test
    fun `haml and slim slot directives are tokenized`() {
        for (directive in listOf("@haml", "@slim")) {
            val tokens = lex(
                """
                $directive H() {
                	= @ifslot content
                		= @eachslot item in content
                			= @render item
                	= @noslot content
                		p Empty
                }
                """.trimIndent()
            )
            assertHasToken(tokens, GohtTokenTypes.AT_IFSLOT, " @ifslot")
            assertHasToken(tokens, GohtTokenTypes.AT_EACHSLOT, " @eachslot")
            assertHasToken(tokens, GohtTokenTypes.TEXT, " item in content")
            assertHasToken(tokens, GohtTokenTypes.AT_NOSLOT, " @noslot")
            assertHasToken(tokens, GohtTokenTypes.TEXT, " content")
        }
    }

    @Test
    fun `ego slot directives are tokenized`() {
        val tokens = lex(
            """
            @ego E() {
            	<%@ifslot content { %>
            		<%@eachslot item in content { %>
            			<%@render item %>
            		<% } %>
            	<% } %>
            	<%@noslot content %>
            }
            """.trimIndent()
        )
        assertHasToken(tokens, GohtTokenTypes.AT_IFSLOT, "ifslot")
        assertHasToken(tokens, GohtTokenTypes.AT_EACHSLOT, "eachslot")
        assertHasToken(tokens, GohtTokenTypes.AT_NOSLOT, "noslot")
        assertHasToken(tokens, GohtTokenTypes.GO_CODE, " item in content { ")
        assertTrue(tokens.none { it.type == GohtTokenTypes.BAD_CHARACTER })
    }

    @Test
    fun `tailwind class shorthand is tokenized`() {
        val classes = listOf(
            ".hover:bg-red-500", ".w-[12px]", ".bg-[url('/images/hero.jpg')]",
            ".[&>p]:underline", ".text-red/80", ".bg-red/[0.5]",
        )
        for ((source, tag) in listOf(
            "@haml H() {\n\t%div${classes.joinToString("")} Tailwind\n}" to GohtTokenTypes.HAML_TAG,
            "@slim S() {\n\tdiv${classes.joinToString("")} Tailwind\n}" to GohtTokenTypes.SLIM_TAG,
        )) {
            val tokens = lex(source)
            assertTrue(tokens.any { it.type == tag })
            for (cls in classes) assertHasToken(tokens, GohtTokenTypes.CLASS_SHORTHAND, cls)
            assertHasToken(tokens, GohtTokenTypes.TEXT, " Tailwind")
        }
    }

    @Test
    fun `slim inline tag after class is not a filter`() {
        val tokens = lex("@slim S() {\n\tdiv.foo: span Inline tag\n\tp After\n}")
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "div")
        assertHasToken(tokens, GohtTokenTypes.CLASS_SHORTHAND, ".foo")
        assertHasToken(tokens, GohtTokenTypes.SLIM_INLINE_SEP, ": ")
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "span")
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "p")
        assertTrue(tokens.none { it.type == GohtTokenTypes.FILTER_NAME })
    }

    @Test
    fun `ruby style framework attribute names are tokenized`() {
        val tokens = lex(
            "@haml H() {\n\t%button{@click: \"act\", :class: \"active\", x-on:click: \"toggle\", data-[state]: \"open\"} Ruby\n}"
        )
        for (name in listOf("@click", ":class", "x-on:click", "data-[state]")) {
            assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, name)
        }
        assertEquals(4, tokens.count { it.type == GohtTokenTypes.ATTR_SEP && it.text == ":" })
        assertHasToken(tokens, GohtTokenTypes.ATTR_VALUE, "\"open\"")
    }

    @Test
    fun `html style framework attribute names are tokenized`() {
        val tokens = lex(
            "@slim S() {\n\tbutton(@click=\"act\" :class=\"active\" x-on:click=\"toggle\" data-[state]=\"open\" @keyup.enter=\"go\") HTML\n}"
        )
        for (name in listOf("@click", ":class", "x-on:click", "data-[state]", "@keyup.enter")) {
            assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, name)
        }
        assertHasToken(tokens, GohtTokenTypes.ATTR_VALUE, "\"open\"")
        assertHasToken(tokens, GohtTokenTypes.ATTR_CLOSE, ")")
    }

    @Test
    fun `html style boolean and conditional attributes are tokenized`() {
        val tokens = lex("@haml H() {\n\t%button(disabled class=\"x\")\n\t%button(disabled? #{true} class=\"on\")\n}")
        assertEquals(2, tokens.count { it.type == GohtTokenTypes.ATTR_NAME && it.text == "disabled" })
        assertEquals(2, tokens.count { it.type == GohtTokenTypes.ATTR_NAME && it.text == "class" })
        assertHasToken(tokens, GohtTokenTypes.ATTR_SEP, "?")
        assertHasToken(tokens, GohtTokenTypes.INTERP_CONTENT, "true")
        assertHasToken(tokens, GohtTokenTypes.ATTR_VALUE, "\"x\"")
    }

    @Test
    fun `ruby style conditional attribute is tokenized`() {
        val tokens = lex("@haml H() {\n\t.boolean{disabled? #{condition}} Content\n}")
        assertHasToken(tokens, GohtTokenTypes.ATTR_NAME, "disabled")
        assertHasToken(tokens, GohtTokenTypes.ATTR_SEP, "?")
        assertHasToken(tokens, GohtTokenTypes.INTERP_CONTENT, "condition")
    }

    @Test
    fun `escaped interpolation stays text`() {
        val tokens = lex("@haml H() {\n\t%p Price \\#{x} now\n}")
        assertHasToken(tokens, GohtTokenTypes.TEXT, " Price \\#{x} now")
        assertTrue(tokens.none { it.type == GohtTokenTypes.INTERP_START })
    }

    @Test
    fun `comments cover their nested lines`() {
        val slim = lex("@slim S() {\n\t/! Comment\n\t\t%p nested\n\tp After\n}")
        assertHasToken(slim, GohtTokenTypes.COMMENT, "/! Comment\n\t\t%p nested")
        assertHasToken(slim, GohtTokenTypes.SLIM_TAG, "p")

        val haml = lex("@haml H() {\n\t-# Comment\n\t\t%p nested\n\n\t%p After\n}")
        assertHasToken(haml, GohtTokenTypes.COMMENT, "-# Comment\n\t\t%p nested\n")
        assertEquals(listOf("%p"), haml.filter { it.type == GohtTokenTypes.HAML_TAG }.map { it.text })
        assertEquals(1, haml.count { it.type == GohtTokenTypes.BLOCK_CLOSE })
    }

    @Test
    fun `go filter body is filter content`() {
        val tokens = lex("@slim S() {\n\t:go\n\t\tname := \"x\"\n\tp= name\n}")
        assertHasToken(tokens, GohtTokenTypes.FILTER_NAME, ":go")
        assertTrue(tokens.any { it.type == GohtTokenTypes.FILTER_CONTENT && it.text.contains("name := \"x\"") })
        assertHasToken(tokens, GohtTokenTypes.SLIM_TAG, "p")
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
