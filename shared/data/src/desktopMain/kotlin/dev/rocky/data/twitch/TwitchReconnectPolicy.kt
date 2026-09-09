package dev.rocky.data.twitch

internal fun twitchReconnectDelaySeconds(attempt: Int): Long =
    minOf(MAX_RECONNECT_DELAY_SECONDS, 1L shl minOf((attempt - 1).coerceAtLeast(0), 5))

private const val MAX_RECONNECT_DELAY_SECONDS = 30L
