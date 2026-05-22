package com.github.stackus.gohtjetbrains.injection

import com.github.stackus.gohtjetbrains.GohtFileType
import com.github.stackus.gohtjetbrains.psi.GohtInjectionHost
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class GohtHighlightInfoFilterTest : BasePlatformTestCase() {

    fun testGohtFileIsNotMarkedAsInjected() {
        val source = """
            package hello

            @haml HamlWorld(term string) {
            	%p= term
            	:css
            		body { color: white; }
            }
            """.trimIndent()
        val file = myFixture.configureByText(GohtFileType, source)
        val filter = GohtHighlightInfoFilter()

        assertFalse("GoHT host file should not be treated as an injected GoHT file", filter.isGohtInjectedFile(file))
    }

    fun testGohtInjectionHostsProduceInjectedFiles() {
        val source = """
            package hello

            var terms = []string{"foo"}

            @haml HamlWorld(term string) {
            	%p= term
            	- for _, term := range terms
            	:css
            		body { color: white; }
            }
            """.trimIndent()
        val file = myFixture.configureByText(GohtFileType, source)
        val manager = InjectedLanguageManager.getInstance(project)

        val hosts = file.collectGohtInjectionHosts()
        assertTrue("Expected GoHT injection hosts", hosts.isNotEmpty())

        hosts.forEach { host ->
            val injected = manager.getInjectedPsiFiles(host)
            injected?.forEach { pair ->
                val injectedFile = pair.first as? com.intellij.psi.PsiFile ?: return@forEach
                assertTrue(
                    "Injected file hosted by GohtInjectionHost should be detected: ${injectedFile.language.id}",
                    GohtHighlightInfoFilter().isGohtInjectedFile(injectedFile)
                )
            }
        }
    }

    private fun com.intellij.psi.PsiFile.collectGohtInjectionHosts(): List<GohtInjectionHost> {
        val hosts = mutableListOf<GohtInjectionHost>()
        accept(object : com.intellij.psi.PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: com.intellij.psi.PsiElement) {
                if (element is GohtInjectionHost) hosts += element
                super.visitElement(element)
            }
        })
        return hosts
    }
}
