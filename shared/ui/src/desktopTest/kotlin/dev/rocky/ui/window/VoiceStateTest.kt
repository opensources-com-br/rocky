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
    @Test fun completedAnswerResumesWithoutNextButton() = runBlocking {
        val service = FakeVoiceService()
        val state = VoiceState(service, readyConfiguration) {}
        state.enableListener(this) {}
        waitUntil { state.capturing }
        state.speakSuggestion(this, "first", "Resposta", false)
        waitUntil { service.captureStarts == 2 && state.capturing }
        state.speakSuggestion(this, "second", "Outra resposta", false)
        waitUntil { service.captureStarts == 3 && state.capturing }
        state.resetSession()
    }

    @Test fun interruptedAnswerResumesListenerButResetDoesNot() = runBlocking {
        val service = FakeVoiceService().apply { speechGate = CountDownLatch(1) }
        val state = VoiceState(service, readyConfiguration) {}
        state.enableListener(this) {}
        waitUntil { state.capturing }
        state.stopCapture(this) {
            state.speakSuggestion(this, "answer", "Resposta", false, force = true, onFinished = state::resumeListener)
        }
        waitUntil { service.spoken.isNotEmpty() }
        state.interruptSpeech()
        waitUntil { state.capturing }
        assertEquals(2, service.captureStarts)
        state.resetSession()
        delay(20)
        assertFalse(state.capturing)
        assertFalse(state.listenerEnabled)
    }

    @Test fun queuedForcedAnswerPreservesCompletion() = runBlocking {
        val service = FakeVoiceService().apply { speechGate = CountDownLatch(1) }
        val state = VoiceState(service, VoiceConfiguration()) {}
        var finished = false
        state.speakAcknowledgement(this, "Primeiro", false) {}
        waitUntil { service.spoken.size == 1 }
        state.speakSuggestion(this, "answer", "Segundo", false, force = true) { finished = true }
        service.speechGate!!.countDown()
        waitUntil { finished }
        assertEquals(listOf("Primeiro", "Segundo"), service.spoken)
    }

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
    fun acknowledgesACommandBeforeContinuing() = runBlocking {
        val service = FakeVoiceService()
        val state = VoiceState(service, VoiceConfiguration()) {}
        var finished = false

        state.speakAcknowledgement(this, "Vou verificar.", silenced = false) { finished = true }
        waitUntil { finished }

        assertEquals(listOf("Vou verificar."), service.spoken)
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
    fun testsACompleteMicrophoneConversation() = runBlocking {
        val service = FakeVoiceService()
        val state = VoiceState(service, readyConfiguration, captureDurationMillis = 5L) {}

        state.testConversation(this)
        waitUntil { !state.conversationTesting }

        assertEquals("O que o chat achou?", state.conversationTestTranscript)
        assertEquals(
            "Eu ouvi você dizer: O que o chat achou?. Meu microfone está funcionando.",
            state.conversationTestResponse,
        )
        assertEquals(listOf(state.conversationTestResponse), service.spoken)
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
    fun enablingTheListenerTwiceKeepsOneMicrophoneCapture() = runBlocking {
        val service = FakeVoiceService()
        val state = VoiceState(service, readyConfiguration) {}

        state.enableListener(this) {}
        waitUntil { state.capturing }
        state.enableListener(this) {}

        assertEquals(1, service.captureStarts)
        state.disableListener()
    }

    @Test
    fun doesNotEnableTheListenerBeforeVoiceSetup() = runBlocking {
        val state = VoiceState(FakeVoiceService(), VoiceConfiguration()) {}

        state.toggleListener(this) {}

        assertFalse(state.listenerEnabled)
        assertEquals("Configure o whisper.cpp na aba Voz antes de usar o microfone", state.status)
    }

    @Test
    fun preparesAndPersistsAutomaticTranscriptionSetup() = runBlocking {
        val service = FakeVoiceService().apply {
            setupSupported = true
            preparedTranscription = LocalTranscriptionConfiguration("managed-whisper", "managed-model")
        }
        var saved: VoiceConfiguration? = null
        val initial = VoiceConfiguration(
            transcription = LocalTranscriptionConfiguration("", "", microphoneId = "mic"),
        )
        val state = VoiceState(service, initial) { saved = it }

        state.prepareTranscription(this)
        waitUntil { !state.preparingTranscription }

        assertEquals("managed-whisper", state.configuration.transcription.executablePath)
        assertEquals("managed-model", state.configuration.transcription.modelPath)
        assertEquals("mic", state.configuration.transcription.microphoneId)
        assertEquals(state.configuration, saved)
    }

    @Test
    fun restoresDetectedManagedTranscription() {
        val service = FakeVoiceService().apply {
            detectedTranscription = LocalTranscriptionConfiguration("managed-whisper", "managed-model")
        }
        var saved: VoiceConfiguration? = null

        val state = VoiceState(service, VoiceConfiguration()) { saved = it }

        assertTrue(state.transcriptionReady)
        assertEquals("managed-whisper", saved?.transcription?.executablePath)
        assertEquals("managed-model", saved?.transcription?.modelPath)
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
        var setupSupported = false
        var preparedTranscription = LocalTranscriptionConfiguration("", "")
        var detectedTranscription: LocalTranscriptionConfiguration? = null

        override val automaticTranscriptionSetupSupported: Boolean
            get() = setupSupported

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
        override fun prepareTranscription(onProgress: (String) -> Unit): LocalTranscriptionConfiguration {
            onProgress("Reconhecimento de voz pronto")
            return preparedTranscription
        }
        override fun detectedTranscription(): LocalTranscriptionConfiguration? = detectedTranscription
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
