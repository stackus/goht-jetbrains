package com.github.stackus.gohtjetbrains.highlighter

import com.github.stackus.gohtjetbrains.GohtFileType
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.containers.Interner
import org.jetbrains.plugins.textmate.language.TextMateLanguageDescriptor
import org.jetbrains.plugins.textmate.language.syntax.TextMateSyntaxTable
import java.io.*
import java.nio.file.Path
import java.util.zip.ZipInputStream
import org.jetbrains.plugins.textmate.bundles.readTextMateBundle

private fun deleteFile(file: File) {
    val children = file.listFiles()
    if (children != null) {
        for (child in children) {
            deleteFile(child)
        }
    }
    file.delete()
}
@Throws(IOException::class)
private fun extract(zip: ZipInputStream, target: File) {
    try {
        while (true) {
            val entry = zip.nextEntry ?: break
            val file = File(target, entry.getName())
            if (!file.toPath().normalize().startsWith(target.toPath())) {
                throw IOException("Bad zip entry")
            }
            if (entry.isDirectory()) {
                file.mkdirs()
                continue
            }
            val buffer = ByteArray(4096)
            file.getParentFile().mkdirs()
            val out = BufferedOutputStream(FileOutputStream(file))
            var count: Int
            while (zip.read(buffer).also { count = it } != -1) {
                out.write(buffer, 0, count)
            }
            out.close()
        }
    } finally {
        zip.close()
    }
}
private fun getBundlePath(): Path {
    try {
        val plugin = PluginManagerCore.getPlugin(PluginId.getId("com.github.stackus.gohtjetbrains"))
        val version = plugin?.version ?: "devel"
        val bundleDirectory = File(plugin?.pluginPath.toString() + "/bundles/" + version)
        if (!bundleDirectory.exists()) {
            deleteFile(bundleDirectory.getParentFile())
            bundleDirectory.mkdirs()
            val resource = GohtFileType::class.java.classLoader.getResourceAsStream("bundle.zip")

            extract(ZipInputStream(resource!!), bundleDirectory)
        }
        return Path.of(bundleDirectory.path)
    } catch (ex: IOException) {
        throw UncheckedIOException(ex)
    }
}
fun getTextMateLanguageDescriptor(): TextMateLanguageDescriptor {
    try {
        val bundle = readTextMateBundle(getBundlePath())
        val syntax = TextMateSyntaxTable()
        val interner = Interner.createWeakInterner<CharSequence>()
        val grammars = bundle.readGrammars()
        for (g in grammars) {
            syntax.loadSyntax(g.plist.value, interner)
        }
        return TextMateLanguageDescriptor("source.goht", syntax.getSyntax("source.goht"))
    } catch (ex: Exception) {
        throw RuntimeException(ex)
    }
}