package dev.rocky.platform.desktop

import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Path

fun chooseDesktopFile(
    parent: Frame,
    title: String,
    allowedExtensions: Set<String> = emptySet(),
): String? {
    val dialog = FileDialog(parent, title, FileDialog.LOAD)
    if (allowedExtensions.isNotEmpty()) {
        dialog.filenameFilter = java.io.FilenameFilter { _, name ->
            allowedExtensions.any { extension -> name.endsWith(".$extension", ignoreCase = true) }
        }
    }
    dialog.isVisible = true
    val file = dialog.file ?: return null
    return Path.of(dialog.directory, file).toAbsolutePath().normalize().toString()
}
