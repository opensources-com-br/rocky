package dev.rocky.data.facebook

import dev.rocky.core.facebook.FacebookLiveVideo
import dev.rocky.core.facebook.FacebookPage
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal data class FacebookPageAccess(val page: FacebookPage, val accessToken: String)
internal data class FacebookCommentPage(val messages: List<ChatMessage>, val after: String?)

internal object FacebookPayloads {
    private val json = Json { ignoreUnknownKeys = true }

    fun accessToken(body: String): String = body.objectValue().string("access_token")

    fun pages(body: String): List<FacebookPageAccess> = body.objectValue().data().map { item ->
        FacebookPageAccess(
            FacebookPage(item.string("id"), item.string("name")),
            item.string("access_token"),
        )
    }

    fun liveVideo(body: String): FacebookLiveVideo? = body.objectValue().data().firstOrNull()?.let { item ->
        FacebookLiveVideo(item.string("id"), item.optionalString("title") ?: "Live do Facebook")
    }

    fun viewerCount(body: String): Int? = body.objectValue().optionalInt("live_views")

    fun comments(body: String): FacebookCommentPage {
        val payload = body.objectValue()
        val messages = payload.data().mapNotNull { item ->
            val text = item.optionalString("message")?.trim().orEmpty()
            if (text.isEmpty()) return@mapNotNull null
            val author = item["from"]?.jsonObject
            ChatMessage(
                id = item.string("id"),
                author = author?.optionalString("name") ?: "Facebook",
                text = text,
                platform = StreamPlatform.Facebook,
                authorId = author?.optionalString("id"),
                sourceTimestamp = item.optionalString("created_time"),
            )
        }.asReversed()
        val after = payload["paging"]?.jsonObject?.get("cursors")?.jsonObject?.optionalString("after")
        return FacebookCommentPage(messages, after)
    }

    fun error(body: String): String? = runCatching {
        body.objectValue()["error"]?.jsonObject?.optionalString("message")
    }.getOrNull()

    private fun String.objectValue() = json.parseToJsonElement(this).jsonObject
    private fun JsonObject.data() = this["data"]?.jsonArray.orEmpty().map { it.jsonObject }
    private fun JsonObject.string(name: String) = requireNotNull(optionalString(name)) { "Missing Facebook field: $name" }
    private fun JsonObject.optionalString(name: String) = this[name]?.jsonPrimitive?.content
    private fun JsonObject.optionalInt(name: String) = this[name]?.jsonPrimitive?.intOrNull
}
