package dev.rocky.core.notes

import dev.rocky.core.live.LiveNote

fun notesAsMarkdown(notes: List<LiveNote>, buildLabel: String? = null): String = buildString {
    appendLine("# Rocky Notes")
    buildLabel?.let { appendLine(); appendLine("Build: $it") }
    appendLine()
    if (notes.isEmpty()) {
        appendLine("No notes saved.")
        return@buildString
    }

    notes.forEachIndexed { index, note ->
        appendLine("## ${note.timestamp} · ${note.tag}")
        appendLine()
        if (note.sessionLabel.isNotBlank()) appendLine("Live: ${note.sessionLabel}")
        note.offsetMillis?.let { appendLine("Moment: ${dev.rocky.core.live.momentLabel(it)}") }
        if (note.completed) appendLine("Status: completed")
        appendLine(note.text)
        if (note.evidence.isNotEmpty()) {
            appendLine()
            appendLine("### Evidence")
            appendLine()
            note.evidence.forEach { appendLine("- $it") }
        }
        if (index < notes.lastIndex) appendLine()
    }
}
