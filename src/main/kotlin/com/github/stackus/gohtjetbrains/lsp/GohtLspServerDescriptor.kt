package com.github.stackus.gohtjetbrains.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import java.io.File

class GohtLspServerDescriptor (project: Project, val executable: File) :
    ProjectWideLspServerDescriptor(project, "GoHT") {
    override fun isSupportedFile(file: VirtualFile) = file.extension == "goht"
    override fun createCommandLine() = GeneralCommandLine(executable.absolutePath, "lsp")
}