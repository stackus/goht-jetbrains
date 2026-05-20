package com.github.stackus.gohtjetbrains.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType

abstract class GohtInjectionHost(type: IElementType, text: CharSequence) :
    LeafPsiElement(type, text), PsiLanguageInjectionHost {

    override fun isValidHost() = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val updated = ElementManipulators.handleContentChange(this, text)
        return updated as? PsiLanguageInjectionHost ?: this
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> =
        LiteralTextEscaper.createSimple(this)
}

class GohtGoFragment(type: IElementType, text: CharSequence) : GohtInjectionHost(type, text)
class GohtGoCode(type: IElementType, text: CharSequence) : GohtInjectionHost(type, text)
class GohtGoExpr(type: IElementType, text: CharSequence) : GohtInjectionHost(type, text)
class GohtInterpContent(type: IElementType, text: CharSequence) : GohtInjectionHost(type, text)
class GohtAttrValue(type: IElementType, text: CharSequence) : GohtInjectionHost(type, text)
class GohtEgoHtml(type: IElementType, text: CharSequence) : GohtInjectionHost(type, text)
class GohtFilterContent(type: IElementType, text: CharSequence) : GohtInjectionHost(type, text)
class GohtFuncParams(type: IElementType, text: CharSequence) : GohtInjectionHost(type, text)
