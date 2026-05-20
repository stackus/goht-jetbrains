package com.github.stackus.gohtjetbrains.psi

import com.github.stackus.gohtjetbrains.GohtFileType
import com.github.stackus.gohtjetbrains.GohtLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class GohtFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, GohtLanguage) {
    override fun getFileType() = GohtFileType
    override fun toString() = "GoHT File"
}
