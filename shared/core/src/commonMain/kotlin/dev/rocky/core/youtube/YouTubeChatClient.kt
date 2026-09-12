package dev.rocky.core.youtube

import dev.rocky.core.live.ChatMessage

enum class YouTubeConnectionPhase {
    Disconnected,
    Authenticating,
    AwaitingAuthorization,
    FindingBroadcast,
    Connected,
    Failed,
}

data class YouTubeConfiguration(
    val clientId: String = "",
    val clientSecret: String = "",
    val redirectUri: String = "http://127.0.0.1:18182/oauth/youtube/callback",
)

data class YouTubeAccount(val channelId: String, val displayName: String)

data class YouTubeBroadcast(
    val id: String,
    val liveChatId: String,
    val title: String,
)

sealed interface YouTubeConnectionEvent {
    data class PhaseChanged(val phase: YouTubeConnectionPhase, val detail: String? = null) : YouTubeConnectionEvent
    data class AuthorizationRequired(val authorizationUri: String) : YouTubeConnectionEvent
    data class Connected(val account: YouTubeAccount, val broadcast: YouTubeBroadcast) : YouTubeConnectionEvent
    data class AudienceUpdated(val viewerCount: Int?) : YouTubeConnectionEvent
    data class MessageReceived(val message: ChatMessage) : YouTubeConnectionEvent
}

fun interface YouTubeConnectionListener {
    fun onEvent(event: YouTubeConnectionEvent)
}

interface YouTubeChatClient : AutoCloseable {
    fun connect(configuration: YouTubeConfiguration, listener: YouTubeConnectionListener)
    fun disconnect()
    override fun close()
}
