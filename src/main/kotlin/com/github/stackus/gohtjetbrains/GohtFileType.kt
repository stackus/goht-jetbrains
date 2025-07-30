package com.github.stackus.gohtjetbrains

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object GohtFileType : LanguageFileType(GohtLanguage) {
    override fun getName(): String = "goht"

    override fun getDescription(): String = "Type-safe Go HTML templating with support for HAML, SLIM, and EGO formats."

    override fun getDefaultExtension(): String = "goht"

    override fun getIcon(): Icon = GohtIcons.FILE
}