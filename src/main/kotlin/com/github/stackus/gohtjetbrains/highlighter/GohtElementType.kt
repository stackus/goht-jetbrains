package com.github.stackus.gohtjetbrains.highlighter

import com.intellij.psi.tree.IElementType
import com.github.stackus.gohtjetbrains.GohtLanguage
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateScope

class GohtElementType(scope: TextMateScope) : IElementType("GOHT_TOKEN", GohtLanguage, false) {
    private val myScope = scope

    fun getScope(): TextMateScope {
        return myScope
    }

    override fun hashCode(): Int {
        return getScope().hashCode()
    }

    override fun toString(): String {
        return myScope.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this.javaClass != other.javaClass) return false
        return (other as GohtElementType).getScope() == this.getScope()
    }
}