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
    fun textOnlySetupDoesNotSpeakWithoutOptIn() = runBlocking {
        val service = FakeVoiceService()
        val state = VoiceState(service, VoiceConfiguration()) {}
        state.speakSuggestion(this, "s1", "Uma ideia", silenced = false)
        assertTrue(service.spoken.isEmpty())
        assertFalse(state.speaking)
    }

    @Test
    fun alwaysSpeaksAnAnswerRequestedByVoice() = runBlocking {
        val service = FakeVoiceService()
        val state = VoiceState(service, VoiceConfiguration()) {}
        var finished = false

        state.speakSuggestion(this, "voice-1", "Resposta", silenced = false, force = true) {
            finished = true
        }
        waitUntil { finished }

        assertEquals(listOf("Resposta"), service.spoken)
    }

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
    fun verifiesAndInvalidatesTheSelectedVoice() = runBlocking {
        val state = VoiceState(FakeVoiceService(), readyConfiguration) {}

        state.testVoice(this)
        waitUntil { !state.speaking }
        assertTrue(state.voiceTested)

        state.updateSpeed(110)
        assertFalse(state.voiceTested)
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
        waitUntil { state.capturing }
        assertTrue(service.captureStarted)
        state.stopCapture(this) { received = it }
        waitUntil { !state.transcribing }

        assertFalse(state.capturing)
        assertEquals("O que o chat achou?", state.transcript)
        assertEquals("O que o chat achou?", received)
    }

    @Test
    fun keepsListeningAfterSubmittingAnAutomaticTurn() = runBlocking {
        val service = FakeVoiceService()
        val state = VoiceState(service, readyConfiguration, captureDurationMillis = 5L) {}
        var received: String? = null

        state.toggleListener(this) { received = it }
        waitUntil { received != null }
        assertTrue(state.listenerEnabled)

        state.resumeListener()
        waitUntil { service.captureStarts >= 2 }
        state.toggleListener(this) {}
        assertFalse(state.listenerEnabled)
    }

    @Test
    fun clearsTranscriptForANewSession() = runBlocking {
        val state = VoiceState(FakeVoiceService(), readyConfiguration) {}

        state.startCapture(this) {}
        waitUntil { state.capturing }
        state.stopCapture(this) {}
        waitUntil { !state.transcribing }
        state.resetSession()

        assertEquals(null, state.transcript)
        assertEquals(null, state.status)
    }

    @Test
    fun ignoresTranscriptionAfterCaptureIsCancelled() = runBlocking {
        val service = FakeVoiceService().apply { transcriptionGate = CountDownLatch(1) }
        val state = VoiceState(service, readyConfiguration) {}

        state.startCapture(this) {}
        waitUntil { state.capturing }
        state.stopCapture(this) {}
        waitUntil { state.transcribing }
        state.cancelCapture()
        service.transcriptionGate?.countDown()
        delay(20)

        assertFalse(state.transcribing)
        assertEquals(null, state.transcript)
        assertEquals("Captura cancelada", state.status)
    }

    @Test
    fun keepsSpeechStoppedAfterItsWorkerCompletes() = runBlocking {
        val service = FakeVoiceService().apply { speechGate = CountDownLatch(1) }
        val state = VoiceState(service, readyConfiguration) {}

        state.speakSuggestion(this, "suggestion-1", "Uma ideia", silenced = false)
        waitUntil { state.speaking }
        state.stopSpeaking()
        delay(20)

        assertFalse(state.speaking)
        assertEquals("Leitura interrompida", state.status)
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
        var captureStarts = 0
        var speechGate: CountDownLatch? = null
        var transcriptionGate: CountDownLatch? = null

        override fun availableVoices() = listOf(SystemVoice("voice", "Voice", "pt-BR"))
        override fun availableMicrophones() = listOf(AudioInputDevice("mic", "Microphone"))
        override fun speak(text: String, configuration: VoiceOutputConfiguration) {
            spoken += text
            speechGate?.await()
        }
        override fun stopSpeaking() {
            speechGate?.countDown()
        }
        override fun startCapture(microphoneId: String?) {
            captureStarted = true
            captureStarts += 1
        }
        override fun stopCaptureAndTranscribe(configuration: LocalTranscriptionConfiguration): String {
            transcriptionGate?.await()
            return "O que o chat achou?"
        }
        override fun cancelCapture() {
            transcriptionGate?.countDown()
        }
        override fun close() = Unit
    }

    private val readyConfiguration = VoiceConfiguration(
        readSuggestions = true,
        transcription = LocalTranscriptionConfiguration("whisper-cli", "model.bin"),
    )
}
