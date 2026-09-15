package dev.rocky.data.kick

import java.io.IOException

internal fun kickRetryDelay(error: Throwable, attempt: Int): Long? {
    if (attempt !in 1..5) return null
    val retryable = when (error) {
        is KickApiException -> error.statusCode == 429 || error.statusCode in 500..599
        is IOException -> true
        else -> false
    }
    if (!retryable) return null
    return maxOf((2_000L shl (attempt - 1)).coerceAtMost(30_000),
        (error as? KickApiException)?.retryAfterMillis ?: 0)
}
