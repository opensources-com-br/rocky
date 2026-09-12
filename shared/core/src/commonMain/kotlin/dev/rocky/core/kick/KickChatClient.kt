package dev.rocky.core.kick

import dev.rocky.core.live.ChatMessage

enum class KickConnectionPhase {
    Disconnected,
    Authenticating,
    AwaitingAuthorization,
    Connecting,
    Connected,
    Failed,
}

data class KickConfiguration(
    val clientId: String = "",
    val clientSecret: String = "",
    val redirectUri: String = "http://localhost:18181/oauth/kick/callback",
)

data class KickAccount(val userId: String, val username: String)

sealed interface KickConnectionEvent {
    data class PhaseChanged(val phase: KickConnectionPhase, val detail: String? = null) : KickConnectionEvent
    data class AuthorizationRequired(val authorizationUri: String) : KickConnectionEvent
    data class Connected(val account: KickAccount) : KickConnectionEvent
    data class AudienceUpdated(val viewerCount: Int?) : KickConnectionEvent
    data class MessageReceived(val message: ChatMessage) : KickConnectionEvent
}

fun interface KickConnectionListener {
    fun onEvent(event: KickConnectionEvent)
}

interface KickChatClient : AutoCloseable {
    fun connect(configuration: KickConfiguration, listener: KickConnectionListener)
    fun disconnect()
    override fun close()
}
