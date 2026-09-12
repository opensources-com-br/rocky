package dev.rocky.platform.desktop

import dev.rocky.core.live.LiveNote
import dev.rocky.core.notes.notesAsMarkdown
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Path
import kotlin.io.path.writeText

fun exportNotesAsMarkdown(owner: Frame, notes: List<LiveNote>): Boolean {
    val dialog = FileDialog(owner, "Exportar notas", FileDialog.SAVE).apply {
        file = "rocky-notes.md"
        isVisible = true
    }
    val directory = dialog.directory ?: return false
    val selectedFile = dialog.file ?: return false
    val fileName = if (selectedFile.endsWith(".md", ignoreCase = true)) {
        selectedFile
    } else {
        "$selectedFile.md"
    }
    writeMarkdownAtomically(Path.of(directory, fileName), notesAsMarkdown(notes, "${System.getProperty("rocky.version", "development")} · ${System.getProperty("rocky.commit", "unknown")}"))
    return true
}

internal fun writeMarkdownAtomically(path: Path, content: String) {
    val temporary = java.nio.file.Files.createTempFile(path.toAbsolutePath().parent, ".rocky-export-", ".tmp")
    try {
        temporary.writeText(content)
        try {
            java.nio.file.Files.move(temporary, path, java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
            java.nio.file.Files.move(temporary, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        }
    } finally { java.nio.file.Files.deleteIfExists(temporary) }
}
