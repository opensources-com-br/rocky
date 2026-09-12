package dev.rocky.core.twitch

import dev.rocky.core.live.ChatMessage

enum class TwitchConnectionPhase {
    Disconnected,
    Authenticating,
    AwaitingAuthorization,
    Connecting,
    Connected,
    Reconnecting,
    Failed,
}

data class TwitchAccount(
    val userId: String,
    val login: String,
)

sealed interface TwitchConnectionEvent {
    data class PhaseChanged(
        val phase: TwitchConnectionPhase,
        val detail: String? = null,
    ) : TwitchConnectionEvent

    data class AuthorizationRequired(
        val userCode: String,
        val verificationUri: String,
    ) : TwitchConnectionEvent

    data class Connected(val account: TwitchAccount) : TwitchConnectionEvent

    data class AudienceUpdated(val viewerCount: Int?) : TwitchConnectionEvent

    data class MessageReceived(val message: ChatMessage) : TwitchConnectionEvent
}

fun interface TwitchConnectionListener {
    fun onEvent(event: TwitchConnectionEvent)
}

interface TwitchChatClient : AutoCloseable {
    fun connect(clientId: String, listener: TwitchConnectionListener)

    fun disconnect()

    override fun close()
}
