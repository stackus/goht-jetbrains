package com.github.stackus.gohtjetbrains.injection

import com.github.stackus.gohtjetbrains.GohtFileType
import com.github.stackus.gohtjetbrains.psi.GohtEgoHtml
import com.github.stackus.gohtjetbrains.psi.GohtFilterContent
import com.github.stackus.gohtjetbrains.psi.GohtGoFragment
import com.github.stackus.gohtjetbrains.psi.GohtInterpContent
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class GohtInjectionHostPsiTest : BasePlatformTestCase() {

    fun testLexerHostTokensArePsiLanguageInjectionHosts() {
        val source = """
            package hello

            @goht World() {
                %p Hello #{name}
                :css
                    body { color: white; }
            }

            @ego EgoWorld() {
                <html></html>
            }
            """.trimIndent()
        val file = myFixture.configureByText(GohtFileType, source)

        assertHostAt(source, file.text.indexOf("package"), GohtGoFragment::class.java)
        assertHostAt(source, file.text.indexOf("name"), GohtInterpContent::class.java)
        assertHostAt(source, file.text.indexOf("body"), GohtFilterContent::class.java)
        assertHostAt(source, file.text.indexOf("<html>"), GohtEgoHtml::class.java)
    }

    private fun assertHostAt(source: String, offset: Int, expectedClass: Class<*>) {
        assertTrue("Offset must exist in source:\n$source", offset >= 0)
        val element = myFixture.file.findElementAt(offset)
        assertNotNull("No PSI element at offset $offset", element)
        assertInstanceOf(element, expectedClass)
        assertInstanceOf(element, PsiLanguageInjectionHost::class.java)
    }
}
