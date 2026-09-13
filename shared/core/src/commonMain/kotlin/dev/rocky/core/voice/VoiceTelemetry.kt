package dev.rocky.core.voice

data class VoiceTelemetry(
    val captureMillis: Long = 0,
    val transcriptionMillis: Long = 0,
    val firstAudioMillis: Long = 0,
    val playbackMillis: Long = 0,
    val outputNotice: String? = null,
)
