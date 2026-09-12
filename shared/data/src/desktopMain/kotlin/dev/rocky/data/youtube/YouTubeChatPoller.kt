package dev.rocky.data.youtube

import dev.rocky.core.live.ChatMessage
import dev.rocky.core.youtube.YouTubeBroadcast
import java.util.LinkedHashSet

internal class YouTubeChatPoller(
    private val api: YouTubeLiveApi,
    private val access: YouTubeAccessSession,
    private val broadcast: YouTubeBroadcast,
    private val onMessage: (ChatMessage) -> Unit,
    private val onAudience: (Int?) -> Unit,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) {
    private val seenMessageIds = LinkedHashSet<String>()
    private var pageToken: String? = null
    private var lastAudienceRefreshAt = Long.MIN_VALUE

    fun poll(): Long {
        val page = access.request { api.chatPage(it, broadcast.liveChatId, pageToken) }
        pageToken = page.nextPageToken
        page.messages.forEach { message ->
            if (remember(message.id)) onMessage(message)
        }
        refreshAudienceIfNeeded()
        return page.pollingIntervalMillis.coerceIn(1_000L, 30_000L)
    }

    private fun refreshAudienceIfNeeded() {
        val now = currentTimeMillis()
        if (lastAudienceRefreshAt != Long.MIN_VALUE && now - lastAudienceRefreshAt < AUDIENCE_INTERVAL_MILLIS) return
        lastAudienceRefreshAt = now
        runCatching { access.request { api.viewerCount(it, broadcast.id) } }
            .onSuccess(onAudience)
            .onFailure { onAudience(null) }
    }

    private fun remember(id: String): Boolean {
        if (!seenMessageIds.add(id)) return false
        while (seenMessageIds.size > MAX_SEEN_MESSAGES) seenMessageIds.remove(seenMessageIds.first())
        return true
    }

    private companion object {
        const val AUDIENCE_INTERVAL_MILLIS = 30_000L
        const val MAX_SEEN_MESSAGES = 1_000
    }
}
