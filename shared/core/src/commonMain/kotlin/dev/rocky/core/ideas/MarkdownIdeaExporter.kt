package dev.rocky.core.ideas

import dev.rocky.core.live.LiveIdea

fun ideasAsMarkdown(ideas: List<LiveIdea>): String = buildString {
    appendLine("# Rocky Ideas")
    appendLine()
    if (ideas.isEmpty()) {
        appendLine("No ideas saved.")
        return@buildString
    }

    ideas.forEachIndexed { index, idea ->
        appendLine("## ${idea.timestamp} · ${idea.tag}")
        appendLine()
        appendLine(idea.text)
        if (index < ideas.lastIndex) appendLine()
    }
}
