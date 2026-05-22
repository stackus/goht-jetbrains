package com.github.stackus.gohtjetbrains.settings

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GohtSettingsTest {

    @Test
    fun `test default logging disabled`() {
        val state = GohtSettings.State()
        assertFalse(state.loggingEnabled)
    }

    @Test
    fun `test default log file path uses home directory`() {
        val state = GohtSettings.State()
        val expected = System.getProperty("user.home") + "/goht-lsp.log"
        assertEquals(expected, state.logFilePath)
    }

    @Test
    fun `test default traceClient is false`() {
        val state = GohtSettings.State()
        assertFalse(state.traceClient)
    }

    @Test
    fun `test default traceGoPls is false`() {
        val state = GohtSettings.State()
        assertFalse(state.traceGoPls)
    }

    @Test
    fun `test lsp args empty when logging disabled`() {
        val state = GohtSettings.State(loggingEnabled = false, logFilePath = "/tmp/test.log", traceClient = true, traceGoPls = true)
        assertEquals(emptyList<String>(), state.buildLspArgs())
    }

    @Test
    fun `test lsp args include logFile when logging enabled`() {
        val state = GohtSettings.State(loggingEnabled = true, logFilePath = "/tmp/goht.log")
        val args = state.buildLspArgs()
        assertTrue(args.contains("--logFile"))
        assertTrue(args.contains("/tmp/goht.log"))
    }

    @Test
    fun `test lsp args include traceClient flag`() {
        val state = GohtSettings.State(loggingEnabled = true, logFilePath = "/tmp/goht.log", traceClient = true)
        assertTrue(state.buildLspArgs().contains("--traceClient"))
    }

    @Test
    fun `test lsp args omit traceClient when false`() {
        val state = GohtSettings.State(loggingEnabled = true, logFilePath = "/tmp/goht.log", traceClient = false)
        assertFalse(state.buildLspArgs().contains("--traceClient"))
    }

    @Test
    fun `test lsp args include traceGoPls flag`() {
        val state = GohtSettings.State(loggingEnabled = true, logFilePath = "/tmp/goht.log", traceGoPls = true)
        assertTrue(state.buildLspArgs().contains("--traceGoPls"))
    }

    @Test
    fun `test lsp args omit traceGoPls when false`() {
        val state = GohtSettings.State(loggingEnabled = true, logFilePath = "/tmp/goht.log", traceGoPls = false)
        assertFalse(state.buildLspArgs().contains("--traceGoPls"))
    }

    @Test
    fun `test lsp args empty when logging enabled but logFilePath blank`() {
        val state = GohtSettings.State(loggingEnabled = true, logFilePath = "")
        assertEquals(emptyList<String>(), state.buildLspArgs())
    }
}
