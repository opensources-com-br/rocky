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

    fun poll(): Long {
        val page = api.comments(liveVideo.id, pageAccessToken)
        page.messages.forEach { message -> if (remember(message.id)) onMessage(message) }
        refreshAudienceIfNeeded()
        return POLL_INTERVAL_MILLIS
    }

    private fun refreshAudienceIfNeeded() {
        val now = currentTimeMillis()
        if (lastAudienceRefreshAt != Long.MIN_VALUE && now - lastAudienceRefreshAt < AUDIENCE_INTERVAL_MILLIS) return
        lastAudienceRefreshAt = now
        runCatching { api.viewerCount(liveVideo.id, pageAccessToken) }
            .onSuccess(onAudience)
            .onFailure { onAudience(null) }
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
