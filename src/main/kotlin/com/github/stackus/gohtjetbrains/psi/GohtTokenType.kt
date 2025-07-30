package com.github.stackus.gohtjetbrains.psi

import com.github.stackus.gohtjetbrains.GohtLanguage
import com.intellij.psi.tree.IElementType

class GohtTokenType(debugName: String) : IElementType(debugName, GohtLanguage) {
    override fun toString(): String {
        return "GohtTokenType." + super.toString()
    }
}