package dev.rocky.data.voice

import dev.rocky.core.voice.*
import java.util.concurrent.atomic.AtomicLong

class ProviderVoiceService(private val local: VoiceService, player: PcmPlayback) : VoiceService by local {
    private val remote = ElevenLabsSpeech(player)
    private val generation = AtomicLong()
    @Volatile private var speechTiming = VoiceTelemetry()
    override val telemetry get() = local.telemetry.copy(firstAudioMillis = speechTiming.firstAudioMillis,
        playbackMillis = speechTiming.playbackMillis, outputNotice = speechTiming.outputNotice)
    override fun loadVoiceCatalog(configuration: ElevenLabsConfiguration) = elevenLabsCatalog(configuration)
    override fun stopSpeaking() { generation.incrementAndGet(); remote.cancel(); local.stopSpeaking() }
    override fun close() { stopSpeaking(); local.close() }

    @Synchronized
    override fun speak(text: String, configuration: VoiceOutputConfiguration) {
        local.cancelCapture()
        val run = generation.get()
        val started = System.nanoTime()
        speechTiming = VoiceTelemetry()
        try {
            if (configuration.provider == SpeechProvider.System) local.speak(text, configuration)
            else try { remote.speak(text, configuration) } catch (error: Exception) {
                if (error is InterruptedException || Thread.currentThread().isInterrupted || generation.get() != run) throw error
                if (!configuration.elevenLabs.fallbackToSystem || remote.startedPlayback) throw error
                speechTiming = speechTiming.copy(outputNotice = "ElevenLabs indisponível; resposta reproduzida com voz local.")
                local.speak(text, configuration.copy(provider = SpeechProvider.System))
            }
        } finally { speechTiming = speechTiming.copy(playbackMillis = (System.nanoTime() - started) / 1_000_000,
            firstAudioMillis = if (configuration.provider == SpeechProvider.ElevenLabs) remote.firstAudioMillis else 0) }
    }
}
