package com.github.stackus.gohtjetbrains.injection

import com.github.stackus.gohtjetbrains.psi.*
import com.intellij.lang.Language
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class GohtLanguageInjector : MultiHostInjector {

    private val goLanguage: Language? by lazy {
        Language.findLanguageByID("go")
    }
    private val htmlLanguage: Language? by lazy { Language.findLanguageByID("HTML") }
    private val cssLanguage: Language? by lazy { Language.findLanguageByID("CSS") }
    private val jsLanguage: Language? by lazy { Language.findLanguageByID("JavaScript") }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> = listOf(
        GohtGoFragment::class.java,
        GohtGoCode::class.java,
        GohtGoExpr::class.java,
        GohtInterpContent::class.java,
        GohtAttrValue::class.java,
        GohtEgoHtml::class.java,
        GohtFilterContent::class.java,
        GohtFuncParams::class.java,
    )

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        when (context) {
            is GohtEgoHtml -> {
                val lang = htmlLanguage ?: return
                registrar
                    .startInjecting(lang)
                    .addPlace(null, null, context, TextRange(0, context.textLength))
                    .doneInjecting()
                return
            }

            is GohtFilterContent -> {
                val filterNameElement = previousMeaningfulSibling(context) ?: return
                val filterName = filterNameElement.text.trimStart(':').lowercase()
                if (filterName == "go") {
                    val lang = goLanguage ?: return
                    val params = findEnclosingFuncParams(context)
                    registrar
                        .startInjecting(lang)
                        .addPlace(
                            "package __goht_injection\nfunc __goht_fn${params?.text ?: "()"} {\n",
                            "\n}",
                            context,
                            TextRange(0, context.textLength)
                        )
                        .doneInjecting()
                    return
                }
                val lang = when (filterName) {
                    "css"              -> cssLanguage
                    "javascript", "js" -> jsLanguage
                    else               -> null
                } ?: return
                registrar
                    .startInjecting(lang)
                    .addPlace(null, null, context, TextRange(0, context.textLength))
                    .doneInjecting()
                return
            }
        }

        val lang = goLanguage ?: return

        when (context) {
            is GohtGoFragment -> {
                registrar
                    .startInjecting(lang)
                    .addPlace(null, null, context, TextRange(0, context.textLength))
                    .doneInjecting()
            }

            is GohtGoCode -> {
                val params = findEnclosingFuncParams(context)
                registrar
                    .startInjecting(lang)
                    .addPlace(
                        "package __goht_injection\nfunc __goht_fn${params?.text ?: "()"} {\n",
                        "\n}",
                        context,
                        innerRange(context, 1)
                    )
                    .doneInjecting()
            }

            is GohtGoExpr -> {
                val params = findEnclosingFuncParams(context)
                registrar
                    .startInjecting(lang)
                    .addPlace(
                        "package __goht_injection\nfunc __goht_fn${params?.text ?: "()"} { _ = ",
                        "}",
                        context,
                        innerRange(context, 1)
                    )
                    .doneInjecting()
            }

            is GohtInterpContent -> {
                val params = findEnclosingFuncParams(context)
                registrar
                    .startInjecting(lang)
                    .addPlace(
                        "package __goht_injection\nfunc __goht_fn${params?.text ?: "()"} { _ = ",
                        "}",
                        context,
                        TextRange(0, context.textLength)
                    )
                    .doneInjecting()
            }

            is GohtAttrValue -> {
                val params = findEnclosingFuncParams(context)
                registrar
                    .startInjecting(lang)
                    .addPlace(
                        "package __goht_injection\nfunc __goht_fn${params?.text ?: "()"} { _ = ",
                        "}",
                        context,
                        TextRange(0, context.textLength)
                    )
                    .doneInjecting()
            }

            is GohtFuncParams -> {
                registrar
                    .startInjecting(lang)
                    .addPlace(
                        "package __goht_injection\nfunc",
                        " {}",
                        context,
                        TextRange(0, context.textLength)
                    )
                    .doneInjecting()
            }
        }
    }

    private fun innerRange(element: PsiElement, skipChars: Int): TextRange {
        return TextRange(skipChars, element.textLength)
    }

    // Walk backwards through flat file siblings to find the nearest preceding FUNC_PARAMS.
    // The PSI tree is completely flat (all tokens are direct children of GohtFile), so
    // GohtFuncParams is always a prev-sibling of any expression inside its template block.
    private fun findEnclosingFuncParams(element: PsiElement): GohtFuncParams? {
        var node = element.prevSibling
        while (node != null) {
            if (node is GohtFuncParams) return node
            node = node.prevSibling
        }
        return null
    }

    private fun previousMeaningfulSibling(element: PsiElement): PsiElement? {
        var sibling = element.prevSibling
        while (sibling != null && sibling.text.isBlank()) {
            sibling = sibling.prevSibling
        }
        return sibling
    }
}
