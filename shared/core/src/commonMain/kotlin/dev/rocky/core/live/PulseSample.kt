package dev.rocky.core.live

data class PulseSample(
    val timeMillis: Long,
    val messagesPerMinute: Int,
    val viewers: Int?,
    val connected: Boolean,
)
