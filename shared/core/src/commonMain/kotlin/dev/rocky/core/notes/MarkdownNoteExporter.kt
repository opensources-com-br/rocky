package dev.rocky.core.notes

import dev.rocky.core.live.LiveNote

fun notesAsMarkdown(notes: List<LiveNote>): String = buildString {
    appendLine("# Rocky Notes")
    appendLine()
    if (notes.isEmpty()) {
        appendLine("No notes saved.")
        return@buildString
    }

    notes.forEachIndexed { index, note ->
        appendLine("## ${note.timestamp} · ${note.tag}")
        appendLine()
        appendLine(note.text)
        if (index < notes.lastIndex) appendLine()
    }
}
