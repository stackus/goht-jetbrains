package com.github.stackus.gohtjetbrains.lsp

import com.github.stackus.gohtjetbrains.settings.GohtSettings
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider
import java.io.File

class GohtLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        if (file.extension != "goht") return
        val executable = File(GohtSettings.getInstance().getExecutablePath())

        if (!executable.exists()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("GoHT")
                .createNotification(
                    "GoHT LSP not started",
                    "Could not find the 'goht' executable in PATH. Install goht and restart the IDE, or configure the path in Settings → Tools → GoHT.",
                    NotificationType.WARNING
                )
                .addAction(NotificationAction.createSimple("Open GoHT Settings") {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, "GoHT")
                })
                .notify(project)
            return
        }
        serverStarter.ensureServerStarted(GohtLspServerDescriptor(project, executable))
    }
}

