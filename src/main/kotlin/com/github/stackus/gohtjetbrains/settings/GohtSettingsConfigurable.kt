package com.github.stackus.gohtjetbrains.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class GohtSettingsConfigurable : Configurable {
    private var executableField: TextFieldWithBrowseButton? = null

    override fun getDisplayName() = "GoHT"

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

//        executableField = TextFieldWithBrowseButton().apply {
//            addActionListener(
//                TextBrowseFolderListener(
//                    FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor().apply {
//                        title = "Select GoHT Executable"
//                    }
////                FileChooserDescriptorFactory.createSingleFileDescriptor().apply {
////                    title = "Select GoHT Executable"
////                }
//            ))
//        }
        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("GoHT executable:"), executableField!!)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified() =
        executableField?.text != GohtSettings.getInstance().state.executablePath

    override fun apply() {
        GohtSettings.getInstance().state.executablePath = executableField?.text ?: ""
    }

    override fun reset() {
        executableField?.text = GohtSettings.getInstance().state.executablePath
    }

    override fun disposeUIResources() {
        executableField = null
    }
}
