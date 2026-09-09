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
    Path.of(directory, fileName).writeText(notesAsMarkdown(notes))
    return true
}
