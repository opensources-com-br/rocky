package dev.rocky.data.youtube
import java.io.IOException
internal fun youtubeRetryDelay(error: Throwable, attempt: Int): Long? {
    if (attempt !in 1..5) return null
    val retryable = when (error) {
        is YouTubeApiException -> {
            val permanent = setOf("quotaExceeded", "dailyLimitExceeded", "liveChatEnded",
                "liveChatDisabled", "liveChatNotFound", "forbidden", "insufficientPermissions")
            error.reasons.none { it in permanent } && (error.statusCode in 500..599 ||
                error.statusCode == 429 || (error.statusCode == 403 &&
                error.reasons.any { it == "rateLimitExceeded" || it == "userRateLimitExceeded" }))
        }
        is IOException -> true
        else -> false
    }
    if (!retryable) return null
    return maxOf((2_000L shl (attempt - 1)).coerceAtMost(30_000),
        (error as? YouTubeApiException)?.retryAfterMillis ?: 0)
}
