package com.github.stackus.gohtjetbrains

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object GohtFileType : LanguageFileType(GohtLanguage) {
    override fun getName(): String = "goht"

    override fun getDescription(): String = "HTML UI language in Go and Haml."

    override fun getDefaultExtension(): String = "goht"

    override fun getIcon(): Icon = GohtIcons.FILE
}