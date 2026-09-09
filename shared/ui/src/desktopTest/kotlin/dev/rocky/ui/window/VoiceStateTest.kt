package dev.rocky.ui.window

import dev.rocky.core.voice.AudioInputDevice
import dev.rocky.core.voice.LocalTranscriptionConfiguration
import dev.rocky.core.voice.SystemVoice
import dev.rocky.core.voice.VoiceConfiguration
import dev.rocky.core.voice.VoiceOutputConfiguration
import dev.rocky.core.voice.VoiceService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceStateTest {
    @Test
    fun readsEachSuggestionOnlyOnce() = runBlocking {
        val service = FakeVoiceService()
        val state = VoiceState(service, readyConfiguration) {}

        state.speakSuggestion(this, "suggestion-1", "Uma ideia", silenced = false)
        waitUntil { service.spoken.size == 1 }
        state.speakSuggestion(this, "suggestion-1", "Uma ideia", silenced = false)
        delay(10)

        assertEquals(listOf("Uma ideia"), service.spoken)
    }

    @Test
    fun queuesTheNextSuggestionWhileSpeaking() = runBlocking {
        val service = FakeVoiceService().apply { speechGate = CountDownLatch(1) }
        val state = VoiceState(service, readyConfiguration) {}

        state.speakSuggestion(this, "suggestion-1", "Primeira", silenced = false)
        waitUntil { service.spoken.size == 1 }
        state.speakSuggestion(this, "suggestion-2", "Segunda", silenced = false)
        service.speechGate?.countDown()
        waitUntil { service.spoken.size == 2 }

        assertEquals(listOf("Primeira", "Segunda"), service.spoken)
    }

    @Test
    fun transcribesAnExplicitMicrophoneCapture() = runBlocking {
        val service = FakeVoiceService()
        val state = VoiceState(service, readyConfiguration) {}
        var received: String? = null

        state.startCapture(this) { received = it }
        waitUntil { service.captureStarted }
        assertTrue(state.capturing)
        state.stopCapture(this) { received = it }
        waitUntil { !state.transcribing }

        assertFalse(state.capturing)
        assertEquals("O que o chat achou?", state.transcript)
        assertEquals("O que o chat achou?", received)
    }

    private suspend fun waitUntil(condition: () -> Boolean) {
        repeat(1_000) {
            if (condition()) return
            delay(1)
        }
        error("Timed out waiting for voice state")
    }

    private class FakeVoiceService : VoiceService {
        val spoken = mutableListOf<String>()
        var captureStarted = false
        var speechGate: CountDownLatch? = null

        override fun availableVoices() = listOf(SystemVoice("voice", "Voice", "pt-BR"))
        override fun availableMicrophones() = listOf(AudioInputDevice("mic", "Microphone"))
        override fun speak(text: String, configuration: VoiceOutputConfiguration) {
            spoken += text
            speechGate?.await()
        }
        override fun stopSpeaking() = Unit
        override fun startCapture(microphoneId: String?) {
            captureStarted = true
        }
        override fun stopCaptureAndTranscribe(configuration: LocalTranscriptionConfiguration) =
            "O que o chat achou?"
        override fun cancelCapture() = Unit
        override fun close() = Unit
    }

    private val readyConfiguration = VoiceConfiguration(
        transcription = LocalTranscriptionConfiguration("whisper-cli", "model.bin"),
    )
}
