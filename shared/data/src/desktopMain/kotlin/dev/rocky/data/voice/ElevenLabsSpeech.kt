package dev.rocky.data.voice

import dev.rocky.core.voice.*

class ElevenLabsSpeech(private val player: PcmPlayback) : SpeechOutput {
    @Volatile private var session: ElevenLabsSession? = null
    @Volatile var firstAudioMillis = 0L; private set
    @Volatile var startedPlayback = false; private set
    override fun cancel() { session?.close(); player.cancel() }
    override fun speak(text: String, configuration: VoiceOutputConfiguration) {
        firstAudioMillis = 0
        startedPlayback = false
        val voice = configuration.elevenLabs
        require(Regex("[A-Za-z0-9_-]{1,128}").matches(voice.voiceId)) { "ElevenLabs: selecione uma voz." }
        require(Regex("[A-Za-z0-9_-]{1,128}").matches(voice.modelId)) { "ElevenLabs: selecione um modelo." }
        val started = System.nanoTime()
        firstAudioMillis = 0
        val active = ElevenLabsSession().also { session = it }
        try {
            active.open("/v1/text-to-speech/${voice.voiceId}/stream?output_format=pcm_24000", voice.apiKey, speechPayload(text, configuration)).use { input ->
                active.checkActive()
                player.start(24_000, configuration.volumePercent)
                playPcm(input, player, active::checkActive) { startedPlayback = true; firstAudioMillis = (System.nanoTime() - started) / 1_000_000 }
            }
        } finally { active.close(); player.cancel(); if (session === active) session = null }
    }
}
