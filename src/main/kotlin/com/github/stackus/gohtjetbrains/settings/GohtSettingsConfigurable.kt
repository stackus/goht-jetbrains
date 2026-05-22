package com.github.stackus.gohtjetbrains.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class GohtSettingsConfigurable : Configurable {
    private var executableField: TextFieldWithBrowseButton? = null
    private var loggingEnabledCheckbox: JBCheckBox? = null
    private var logFileField: TextFieldWithBrowseButton? = null
    private var traceClientCheckbox: JBCheckBox? = null
    private var traceGoplsCheckbox: JBCheckBox? = null

    override fun getDisplayName() = "GoHT LSP"

    override fun createComponent(): JComponent {
        val settings = GohtSettings.getInstance()

        executableField = TextFieldWithBrowseButton().apply {
            text = settings.getExecutablePath()
            addBrowseFolderListener(
                TextBrowseFolderListener(
                    FileChooserDescriptorFactory.singleFile().apply {
                        title = "Select GoHT Executable"
                    }
                )
            )
        }

        loggingEnabledCheckbox = JBCheckBox("Enable Logging").apply {
            isSelected = settings.state.loggingEnabled
        }

        logFileField = TextFieldWithBrowseButton().apply {
            text = settings.state.logFilePath
            addBrowseFolderListener(
                TextBrowseFolderListener(
                    FileChooserDescriptorFactory.singleFile().apply {
                        title = "Select Log File"
                    }
                )
            )
        }

        traceClientCheckbox = JBCheckBox("Trace GoHT Communication").apply {
            isSelected = settings.state.traceClient
        }

        traceGoplsCheckbox = JBCheckBox("Trace GoPls Communication").apply {
            isSelected = settings.state.traceGoPls
        }

        loggingEnabledCheckbox!!.addItemListener { updateLoggingFieldStates() }
        updateLoggingFieldStates()

        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("GoHT executable:"), executableField!!)
            .addSeparator()
            .addComponent(loggingEnabledCheckbox!!)
            .addLabeledComponent(JBLabel("Log file path:"), logFileField!!)
            .addComponent(traceClientCheckbox!!)
            .addComponent(traceGoplsCheckbox!!)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val s = GohtSettings.getInstance().state
        return executableField?.text != s.executablePath ||
            loggingEnabledCheckbox?.isSelected != s.loggingEnabled ||
            logFileField?.text != s.logFilePath ||
            traceClientCheckbox?.isSelected != s.traceClient ||
            traceGoplsCheckbox?.isSelected != s.traceGoPls
    }

    override fun apply() {
        val s = GohtSettings.getInstance().state
        s.executablePath = executableField?.text ?: ""
        s.loggingEnabled = loggingEnabledCheckbox?.isSelected ?: false
        s.logFilePath = logFileField?.text ?: ""
        s.traceClient = traceClientCheckbox?.isSelected ?: false
        s.traceGoPls = traceGoplsCheckbox?.isSelected ?: false
    }

    override fun reset() {
        val s = GohtSettings.getInstance().state
        executableField?.text = s.executablePath
        loggingEnabledCheckbox?.isSelected = s.loggingEnabled
        logFileField?.text = s.logFilePath
        traceClientCheckbox?.isSelected = s.traceClient
        traceGoplsCheckbox?.isSelected = s.traceGoPls
        updateLoggingFieldStates()
    }

    override fun disposeUIResources() {
        executableField = null
        loggingEnabledCheckbox = null
        logFileField = null
        traceClientCheckbox = null
        traceGoplsCheckbox = null
    }

    private fun updateLoggingFieldStates() {
        val enabled = loggingEnabledCheckbox?.isSelected ?: false
        logFileField?.isEnabled = enabled
        traceClientCheckbox?.isEnabled = enabled
        traceGoplsCheckbox?.isEnabled = enabled
    }
}
