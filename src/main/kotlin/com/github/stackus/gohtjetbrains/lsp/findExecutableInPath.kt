package com.github.stackus.gohtjetbrains.lsp

import com.intellij.openapi.diagnostic.Logger
import java.io.File

fun findExecutableInPath(executableName: String): File? {
    val pathSeparator = if (System.getProperty("os.name").startsWith("Windows")) ";" else ":"
    val paths = System.getenv("PATH").split(pathSeparator)

    val logger = Logger.getInstance("GohtLspServerSupportProvider")

    paths.forEach { path ->
        val fullPath = File(path, executableName)
        // Check for executable without extension on Windows, or directly on Unix-like systems
        if (fullPath.exists() && fullPath.isFile && fullPath.canExecute()) {
            logger.info("Found executable $executableName at $fullPath")
            return fullPath
        }
        // Additional check for Windows: try appending ".exe" to the executable name
        if (System.getProperty("os.name").startsWith("Windows")) {
            val fullPathWithExe = File(path, "$executableName.exe")
            if (fullPathWithExe.exists() && fullPathWithExe.isFile && fullPathWithExe.canExecute()) {
                logger.info("Found executable $executableName at $fullPathWithExe with .exe extension")
                return fullPathWithExe
            }
        }
    }

    logger.warn("Executable $executableName not found in PATH")
    return null
}