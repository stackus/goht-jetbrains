package com.github.stackus.gohtjetbrains.injection

import com.github.stackus.gohtjetbrains.psi.GohtInjectionHost
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiFile

class GohtHighlightInfoFilter : HighlightInfoFilter {
    override fun accept(highlightInfo: HighlightInfo, file: PsiFile?): Boolean {
        if (file == null) return true
        if (!isGohtInjectedFile(file)) return true
        // Allow syntax coloring (TEXT_ATTRIBUTES, INFORMATION); suppress problem markers.
        return highlightInfo.severity < HighlightSeverity.WEAK_WARNING
    }

    internal fun isGohtInjectedFile(file: PsiFile): Boolean {
        val manager = InjectedLanguageManager.getInstance(file.project)
        return manager.isInjectedFragment(file) && manager.getInjectionHost(file) is GohtInjectionHost
    }
}
