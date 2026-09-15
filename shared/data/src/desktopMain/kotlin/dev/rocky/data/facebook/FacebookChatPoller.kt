package dev.rocky.data.facebook

import dev.rocky.core.facebook.FacebookLiveVideo
import dev.rocky.core.live.ChatMessage
import java.util.LinkedHashSet

internal class FacebookChatPoller(
    private val api: FacebookGraphApi,
    private val pageAccessToken: String,
    private val liveVideo: FacebookLiveVideo,
    private val onMessage: (ChatMessage) -> Unit,
    private val onAudience: (Int?) -> Unit,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) {
    private val seenMessageIds = LinkedHashSet<String>()
    private var lastAudienceRefreshAt = Long.MIN_VALUE
    private var idlePolls = 0

    fun poll(): Long {
        val page = api.comments(liveVideo.id, pageAccessToken)
        val messages = page.messages.filter { remember(it.id) }
        messages.forEach(onMessage)
        idlePolls = if (messages.isEmpty()) (idlePolls + 1).coerceAtMost(3) else 0
        refreshAudienceIfNeeded()
        return POLL_INTERVAL_MILLIS + idlePolls * 1_000L
    }

    private fun refreshAudienceIfNeeded() {
        val now = currentTimeMillis()
        if (lastAudienceRefreshAt != Long.MIN_VALUE && now - lastAudienceRefreshAt < AUDIENCE_INTERVAL_MILLIS) return
        lastAudienceRefreshAt = now
        runCatching { api.viewerCount(liveVideo.id, pageAccessToken) }
            .onSuccess(onAudience)
    }

    private fun remember(id: String): Boolean {
        if (!seenMessageIds.add(id)) return false
        while (seenMessageIds.size > MAX_SEEN_MESSAGES) seenMessageIds.remove(seenMessageIds.first())
        return true
    }

    private companion object {
        const val POLL_INTERVAL_MILLIS = 2_000L
        const val AUDIENCE_INTERVAL_MILLIS = 30_000L
        const val MAX_SEEN_MESSAGES = 1_000
    }
}
