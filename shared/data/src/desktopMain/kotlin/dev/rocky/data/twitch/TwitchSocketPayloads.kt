package dev.rocky.data.twitch

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal sealed interface TwitchSocketEvent {
    data class Welcome(
        val sessionId: String,
        val keepaliveTimeoutSeconds: Long,
    ) : TwitchSocketEvent

    data object Keepalive : TwitchSocketEvent

    data class Reconnect(val url: String) : TwitchSocketEvent

    data class MessageReceived(val message: ChatMessage) : TwitchSocketEvent

    data class Revoked(val reason: String) : TwitchSocketEvent

    data object Unknown : TwitchSocketEvent
}

internal object TwitchSocketPayloads {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(body: String): TwitchSocketEvent {
        val root = json.parseToJsonElement(body).jsonObject
        val messageType = root.objectAt("metadata").stringAt("message_type")
        val payload = root.objectAt("payload")
        return when (messageType) {
            "session_welcome" -> payload.welcome()
            "session_keepalive" -> TwitchSocketEvent.Keepalive
            "session_reconnect" -> TwitchSocketEvent.Reconnect(
                payload.objectAt("session").stringAt("reconnect_url"),
            )
            "notification" -> payload.chatMessage()
            "revocation" -> TwitchSocketEvent.Revoked(
                payload.objectAt("subscription").stringAt("status"),
            )
            else -> TwitchSocketEvent.Unknown
        }
    }

    private fun JsonObject.welcome(): TwitchSocketEvent.Welcome {
        val session = objectAt("session")
        return TwitchSocketEvent.Welcome(
            sessionId = session.stringAt("id"),
            keepaliveTimeoutSeconds = session.longAt("keepalive_timeout_seconds"),
        )
    }

    private fun JsonObject.chatMessage(): TwitchSocketEvent {
        val event = objectAt("event")
        val message = event.objectAt("message")
        return TwitchSocketEvent.MessageReceived(
            ChatMessage(
                id = event.stringAt("message_id"),
                author = event.stringAt("chatter_user_name"),
                text = message.stringAt("text"),
                platform = StreamPlatform.Twitch,
            ),
        )
    }
}

private fun JsonObject.objectAt(name: String): JsonObject =
    requireNotNull(this[name]) { "Missing Twitch object: $name" }.jsonObject

private fun JsonObject.stringAt(name: String): String =
    requireNotNull(this[name]) { "Missing Twitch field: $name" }.jsonPrimitive.content

private fun JsonObject.longAt(name: String): Long = stringAt(name).toLong()
