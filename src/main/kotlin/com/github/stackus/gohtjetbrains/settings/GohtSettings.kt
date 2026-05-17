package com.github.stackus.gohtjetbrains.settings

import com.github.stackus.gohtjetbrains.lsp.findExecutableInPath
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service(Service.Level.APP)
@State(name = "GohtSettings", storages = [Storage("goht.xml")])
class GohtSettings : PersistentStateComponent<GohtSettings.State> {
    data class State(var executablePath: String = "")

    private var state = State()

    override fun getState() = state
    override fun loadState(state: State) { this.state = state }

    companion object {
        fun getInstance(): GohtSettings = service()
    }

    // ---

    fun getExecutablePath(): String {
        var execPath = state.executablePath
        if (execPath.isBlank()) {
            execPath = findExecutableInPath("goht")?.absolutePath ?: ""
            if (!execPath.isBlank()) {
                state.executablePath = execPath
            }
        }
        return execPath
    }
}
