package dev.rocky.ui.window

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.LiveNote
import dev.rocky.core.live.RockySuggestion

internal fun suggestionNote(
    suggestion: RockySuggestion,
    messages: List<ChatMessage>,
    timestamp: String,
): LiveNote {
    val sources = messages.filter { it.id in suggestion.sourceMessageIds }
    return LiveNote(
        id = suggestion.id,
        text = suggestion.text,
        timestamp = timestamp,
        tag = "SUGESTÃO IA",
        sourceMessageIds = suggestion.sourceMessageIds,
        evidence = sources.map { "${it.author}: ${it.text}" },
    )
}
