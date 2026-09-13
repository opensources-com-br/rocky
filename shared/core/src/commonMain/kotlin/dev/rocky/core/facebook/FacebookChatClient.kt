package dev.rocky.core.facebook

import dev.rocky.core.live.ChatMessage

enum class FacebookConnectionPhase {
    Disconnected,
    Authenticating,
    AwaitingAuthorization,
    FindingLive,
    Connected,
    Failed,
}

data class FacebookConfiguration(
    val appId: String = "",
    val appSecret: String = "",
    val redirectUri: String = "http://127.0.0.1:18183/oauth/facebook/callback",
)

data class FacebookPage(val id: String, val name: String)

data class FacebookLiveVideo(val id: String, val title: String)

sealed interface FacebookConnectionEvent {
    data class PhaseChanged(val phase: FacebookConnectionPhase, val detail: String? = null) : FacebookConnectionEvent
    data class AuthorizationRequired(val authorizationUri: String) : FacebookConnectionEvent
    data class Connected(val page: FacebookPage, val liveVideo: FacebookLiveVideo) : FacebookConnectionEvent
    data class AudienceUpdated(val viewerCount: Int?) : FacebookConnectionEvent
    data class MessageReceived(val message: ChatMessage) : FacebookConnectionEvent
}

fun interface FacebookConnectionListener {
    fun onEvent(event: FacebookConnectionEvent)
}

interface FacebookChatClient : AutoCloseable {
    fun connect(configuration: FacebookConfiguration, listener: FacebookConnectionListener)
    fun disconnect()
    override fun close()
}
