package com.github.stackus.gohtjetbrains.lsp

import com.github.stackus.gohtjetbrains.settings.GohtSettings
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.customization.LspSemanticTokensSupport
import com.intellij.psi.PsiFile
import java.io.File

class GohtLspServerDescriptor(project: Project, val executable: File) :
    ProjectWideLspServerDescriptor(project, "GoHT") {
    override fun isSupportedFile(file: VirtualFile) = file.extension == "goht"

    override fun createCommandLine(): GeneralCommandLine {
        val args = GohtSettings.getInstance().state.buildLspArgs()
        return GeneralCommandLine((listOf(executable.absolutePath, "lsp") + args).toMutableList())
    }

    override val lspSemanticTokensSupport = object : LspSemanticTokensSupport() {
        override fun shouldAskServerForSemanticTokens(psiFile: PsiFile) = true
    }
}
