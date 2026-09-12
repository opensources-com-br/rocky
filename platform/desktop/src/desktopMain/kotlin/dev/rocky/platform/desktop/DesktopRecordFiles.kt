package dev.rocky.platform.desktop

import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Files
import java.nio.file.Path

fun exportRecordFile(owner: Frame, content: String, filename: String): Boolean {
    val dialog = FileDialog(owner, "Exportar registros Rocky", FileDialog.SAVE).apply {
        file = filename; isVisible = true
    }
    val selected = dialog.file ?: return false
    val extension = filename.substringAfterLast('.')
    val target = if (selected.endsWith(".$extension", true)) selected else "$selected.$extension"
    writeMarkdownAtomically(Path.of(dialog.directory, target), content)
    return true
}

fun chooseRecordBackup(owner: Frame): String? {
    val selected = chooseDesktopFile(owner, "Importar backup Rocky", setOf("json")) ?: return null
    val path = Path.of(selected)
    require(Files.isRegularFile(path) && Files.size(path) <= 50 * 1024 * 1024) { "Escolha um backup de até 50 MB." }
    Files.newInputStream(path).use {
        val bytes = it.readNBytes(50 * 1024 * 1024 + 1)
        require(bytes.size <= 50 * 1024 * 1024) { "Backup excede 50 MB." }
        return bytes.toString(Charsets.UTF_8)
    }
}
