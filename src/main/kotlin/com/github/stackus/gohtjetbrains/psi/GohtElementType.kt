package com.github.stackus.gohtjetbrains.psi

import com.github.stackus.gohtjetbrains.GohtLanguage
import com.intellij.psi.tree.IElementType

class GohtElementType(debugName: String) : IElementType(debugName, GohtLanguage) {
    override fun toString(): String {
        return "GohtElementType." + super.toString()
    }
}