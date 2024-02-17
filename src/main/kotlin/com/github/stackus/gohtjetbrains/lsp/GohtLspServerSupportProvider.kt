package com.github.stackus.gohtjetbrains.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider

class GohtLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        if (file.extension != "goht") return
        val executable = findExecutableInPath("goht") ?: return
        serverStarter.ensureServerStarted(GohtLspServerDescriptor(project, executable))
    }
}

