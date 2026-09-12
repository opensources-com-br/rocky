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
        evidence = sources.map(::messageEvidence),
    )
}

internal fun messageEvidence(message: ChatMessage): String = buildString {
    append("${message.author}: ${message.text}")
    if (message.sessionId != null || message.sourceTimestamp != null || message.authorId != null) {
        append(" [${message.platform}; message=${message.id}")
        message.authorId?.let { append("; author=$it") }
        message.channelId?.let { append("; channel=$it") }
        message.sessionId?.let { append("; session=$it") }
        message.sourceTimestamp?.let { append("; source=$it") }
        message.receivedAtMillis?.let { append("; received_epoch_ms=$it") }
        append("]")
    }
}
