package dev.rocky.core.tiktok

import dev.rocky.core.live.ChatMessage

enum class TikTokConnectionPhase {
    Disconnected,
    Connecting,
    Connected,
    Reconnecting,
    Failed,
}

data class TikTokConfiguration(
    val username: String = "",
)

data class TikTokAccount(
    val username: String,
    val displayName: String,
)

data class TikTokLiveRoom(
    val id: String,
    val title: String,
)

sealed interface TikTokConnectionEvent {
    data class PhaseChanged(val phase: TikTokConnectionPhase, val detail: String? = null) : TikTokConnectionEvent
    data class Connected(val account: TikTokAccount, val room: TikTokLiveRoom) : TikTokConnectionEvent
    data class AudienceUpdated(val viewerCount: Int?) : TikTokConnectionEvent
    data class MessageReceived(val message: ChatMessage) : TikTokConnectionEvent
}

fun interface TikTokConnectionListener {
    fun onEvent(event: TikTokConnectionEvent)
}

interface TikTokChatClient : AutoCloseable {
    fun connect(configuration: TikTokConfiguration, listener: TikTokConnectionListener)
    fun disconnect()
    override fun close()
}
