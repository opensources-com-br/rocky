package dev.rocky.platform.desktop

import dev.rocky.core.ideas.ideasAsMarkdown
import dev.rocky.core.live.LiveIdea
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Path
import kotlin.io.path.writeText

fun exportIdeasAsMarkdown(owner: Frame, ideas: List<LiveIdea>): Boolean {
    val dialog = FileDialog(owner, "Exportar ideias", FileDialog.SAVE).apply {
        file = "rocky-ideas.md"
        isVisible = true
    }
    val directory = dialog.directory ?: return false
    val selectedFile = dialog.file ?: return false
    val fileName = if (selectedFile.endsWith(".md", ignoreCase = true)) {
        selectedFile
    } else {
        "$selectedFile.md"
    }
    Path.of(directory, fileName).writeText(ideasAsMarkdown(ideas))
    return true
}
