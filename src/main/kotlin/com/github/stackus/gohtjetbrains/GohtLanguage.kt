package com.github.stackus.gohtjetbrains

import com.intellij.lang.Language

object GohtLanguage : Language("goht") {
    private fun readResolve(): Any = GohtLanguage

    override fun getDisplayName(): String = "goht"
}