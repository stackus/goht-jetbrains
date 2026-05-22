package com.github.stackus.gohtjetbrains.actions

import com.github.stackus.gohtjetbrains.settings.GohtSettings
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.text.StringUtil
import java.io.File

class GenerateFileAction : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = file != null && !file.isDirectory && file.extension == "goht"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val execPath = GohtSettings.getInstance().getExecutablePath()
        val execFile = File(execPath)

        FileDocumentManager.getInstance().saveAllDocuments()

        if (!execFile.exists()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("GoHT")
                .createNotification(
                    "GoHT Generate Failed",
                    "Could not find the 'goht' executable. Configure the path in Settings → Tools → GoHT LSP.",
                    NotificationType.WARNING
                )
                .addAction(NotificationAction.createSimple("Open GoHT LSP Settings") {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, "GoHT LSP")
                })
                .notify(project)
            return
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Generating GoHT file…", true) {
            @Volatile private var processHandler: CapturingProcessHandler? = null

            override fun onCancel() {
                processHandler?.destroyProcess()
            }

            override fun run(indicator: ProgressIndicator) {
                val cmd = GeneralCommandLine(execFile.absolutePath, "generate", "--path", file.path)
                    .withWorkDirectory(project.basePath ?: file.parent?.path)
                processHandler = CapturingProcessHandler(cmd)
                val output = processHandler!!.runProcessWithProgressIndicator(indicator)

                val (title, content, type) = if (output.exitCode == 0) {
                    Triple("GoHT Generate", "Generated: ${file.name}", NotificationType.INFORMATION)
                } else {
                    Triple(
                        "GoHT Generate Failed",
                        StringUtil.escapeXmlEntities(
                            output.stderr.ifBlank { output.stdout }.ifBlank { "exit code ${output.exitCode}" }
                        ),
                        NotificationType.ERROR
                    )
                }

                ApplicationManager.getApplication().invokeLater {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("GoHT")
                        .createNotification(title, content, type)
                        .notify(project)
                }

                file.parent?.refresh(true, false)
            }
        })
    }
}
