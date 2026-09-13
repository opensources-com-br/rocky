package dev.rocky.core.voice

enum class SpeechProvider { System, ElevenLabs }

data class ElevenLabsConfiguration(
    val apiKey: String = "",
    val voiceId: String = "",
    val modelId: String = "eleven_flash_v2_5",
    val fallbackToSystem: Boolean = false,
) {
    override fun toString() = "ElevenLabsConfiguration(voiceId=$voiceId, modelId=$modelId, apiKey=<redacted>)"
}

data class SpeechModel(val id: String, val name: String)
data class VoiceCatalog(val voices: List<SystemVoice>, val models: List<SpeechModel>)
