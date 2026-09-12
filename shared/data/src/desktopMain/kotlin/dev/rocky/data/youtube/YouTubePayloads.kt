package dev.rocky.data.youtube

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.youtube.YouTubeAccount
import dev.rocky.core.youtube.YouTubeBroadcast
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal data class YouTubeTokens(
    val accessToken: String,
    val refreshToken: String?,
    val expiresInSeconds: Long,
)

internal data class YouTubeChatPage(
    val messages: List<ChatMessage>,
    val nextPageToken: String?,
    val pollingIntervalMillis: Long,
)

internal object YouTubePayloads {
    private val json = Json { ignoreUnknownKeys = true }

    fun tokens(body: String): YouTubeTokens = body.objectValue().let {
        YouTubeTokens(
            accessToken = it.string("access_token"),
            refreshToken = it.optionalString("refresh_token"),
            expiresInSeconds = it.optionalString("expires_in")?.toLongOrNull() ?: 3600,
        )
    }

    fun account(body: String): YouTubeAccount = body.firstItem().let {
        YouTubeAccount(it.string("id"), it.objectAt("snippet").string("title"))
    }

    fun broadcast(body: String): YouTubeBroadcast? = body.items().firstOrNull()?.let {
        val snippet = it.objectAt("snippet")
        val liveChatId = snippet.optionalString("liveChatId") ?: return null
        YouTubeBroadcast(it.string("id"), liveChatId, snippet.string("title"))
    }

    fun viewerCount(body: String): Int? = body.items().firstOrNull()
        ?.objectAt("liveStreamingDetails")?.optionalString("concurrentViewers")?.toIntOrNull()

    fun chatPage(body: String): YouTubeChatPage {
        val payload = body.objectValue()
        val messages = payload.items().mapNotNull { item ->
            val snippet = item.objectAt("snippet")
            val text = snippet.optionalString("displayMessage")?.trim().orEmpty()
            if (text.isEmpty()) return@mapNotNull null
            val author = item.objectAt("authorDetails")
            ChatMessage(
                id = item.string("id"),
                author = author.string("displayName"),
                text = text,
                platform = StreamPlatform.YouTube,
                authorId = author.optionalString("channelId"),
                sourceTimestamp = snippet.optionalString("publishedAt"),
            )
        }
        return YouTubeChatPage(
            messages = messages,
            nextPageToken = payload.optionalString("nextPageToken"),
            pollingIntervalMillis = payload.optionalString("pollingIntervalMillis")?.toLongOrNull() ?: 5000,
        )
    }

    fun error(body: String): String? = runCatching {
        body.objectValue()["error"]?.jsonObject?.optionalString("message")
    }.getOrNull()

    private fun String.objectValue() = json.parseToJsonElement(this).jsonObject
    private fun String.items() = objectValue().items()
    private fun JsonObject.items() = this["items"]?.jsonArray.orEmpty().map { it.jsonObject }
    private fun String.firstItem() = items().first()
    private fun JsonObject.objectAt(name: String) = requireNotNull(this[name]) { "Missing YouTube object: $name" }.jsonObject
    private fun JsonObject.string(name: String) = requireNotNull(optionalString(name)) { "Missing YouTube field: $name" }
    private fun JsonObject.optionalString(name: String) = this[name]?.jsonPrimitive?.content
}
