package dev.rocky.data.youtube

import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class YouTubeRetryTest {
    @Test fun parsesErrorReasonsAndSanitizesMessages() {
        val body = """{"error":{"errors":[{"reason":"rateLimitExceeded"}],"message":"secret"}}"""
        assertEquals(setOf("rateLimitExceeded"), YouTubePayloads.errorReasons(body))
        assertEquals(emptySet(), YouTubePayloads.errorReasons("unavailable"))
        assertEquals(false, YouTubeApiException(403, "secret").message.orEmpty().contains("secret"))
    }

    @Test fun respectsRetryAfterAndRetriesServiceFailures() {
        assertEquals(60_000L, youtubeRetryDelay(YouTubeApiException(503, null, retryAfterMillis = 60_000), 1))
        assertEquals(2000L, youtubeRetryDelay(YouTubeApiException(429, null), 1))
        assertNull(youtubeRetryDelay(IllegalArgumentException(), 1))
    }

    @Test fun retriesRateLimitsButNotQuotaOrPermissionFailures() {
        assertEquals(2000L, youtubeRetryDelay(YouTubeApiException(403, null, setOf("rateLimitExceeded")), 1))
        for (reason in listOf("quotaExceeded", "dailyLimitExceeded", "forbidden", "liveChatEnded")) {
            assertNull(youtubeRetryDelay(YouTubeApiException(403, null, setOf(reason)), 1))
        }
        assertNull(youtubeRetryDelay(YouTubeApiException(401, null), 1))
    }

    @Test fun backsOffAndStopsAfterFiveFailures() {
        assertEquals(listOf(2000L, 4000L, 8000L, 16000L, 30000L),
            (1..5).map { youtubeRetryDelay(IOException(), it) })
        assertNull(youtubeRetryDelay(IOException(), 6))
        assertNull(youtubeRetryDelay(InterruptedException(), 1))
    }
}
