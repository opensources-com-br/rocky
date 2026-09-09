package dev.rocky.core.live

enum class StreamPlatform {
    Twitch,
    Kick,
    YouTube,
}

data class ChatMessage(
    val id: String,
    val author: String,
    val text: String,
    val platform: StreamPlatform,
)

data class RockySuggestion(
    val id: String,
    val text: String,
    val sourceMessageIds: Set<String>,
)

data class LiveNote(
    val id: String,
    val text: String,
    val timestamp: String,
    val tag: String,
)

sealed interface LiveEvent {
    data class MessageReceived(val message: ChatMessage) : LiveEvent
    data class SuggestionCreated(val suggestion: RockySuggestion) : LiveEvent
}

data class TimedLiveEvent(
    val delayMillis: Long,
    val event: LiveEvent,
)
