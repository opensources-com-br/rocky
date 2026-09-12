package dev.rocky.data.kick

import dev.rocky.core.kick.KickAccount
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal data class KickTokens(val accessToken: String, val refreshToken: String)

internal object KickPayloads {
    private val json = Json { ignoreUnknownKeys = true }

    fun tokens(body: String): KickTokens = body.objectValue().let {
        KickTokens(it.string("access_token"), it.string("refresh_token"))
    }

    fun account(body: String): KickAccount = body.firstDataObject().let {
        KickAccount(it.string("user_id"), it.string("name"))
    }

    fun viewerCount(body: String): Int? = (body.firstDataObject()["stream"] as? JsonObject)
        ?.get("viewer_count")?.jsonPrimitive?.content?.toIntOrNull()

    fun chatMessage(body: String): ChatMessage {
        val payload = body.objectValue()
        val sender = payload["sender"]!!.jsonObject
        val broadcaster = payload["broadcaster"]!!.jsonObject
        return ChatMessage(
            id = payload.string("message_id"),
            author = sender.string("username"),
            text = payload.string("content"),
            platform = StreamPlatform.Kick,
            authorId = sender.string("user_id"),
            channelId = broadcaster.string("user_id"),
            sourceTimestamp = payload["created_at"]?.jsonPrimitive?.content,
        )
    }

    fun error(body: String): String? = runCatching {
        body.objectValue()["message"]?.jsonPrimitive?.content
            ?: body.objectValue()["error"]?.jsonPrimitive?.content
    }.getOrNull()

    fun publicKey(body: String): String = body.objectValue()["data"]!!.jsonObject.string("public_key")

    fun subscriptionIds(body: String): List<String> = body.objectValue()["data"]!!.jsonArray
        .mapNotNull { it.jsonObject["subscription_id"]?.jsonPrimitive?.content }

    private fun String.objectValue() = json.parseToJsonElement(this).jsonObject
    private fun String.firstDataObject() = objectValue()["data"]!!.jsonArray.first().jsonObject
    private fun JsonObject.string(name: String) = requireNotNull(this[name]) {
        "Missing Kick field: $name"
    }.jsonPrimitive.content
}
