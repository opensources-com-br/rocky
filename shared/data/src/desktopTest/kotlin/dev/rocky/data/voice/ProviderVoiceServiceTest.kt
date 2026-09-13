package dev.rocky.data.voice

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.voice.*
import java.net.InetSocketAddress
import kotlin.test.*

class ProviderVoiceServiceTest {
    private fun withServer(status: Int, block: (String) -> Unit) {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            exchange.sendResponseHeaders(status, 4)
            exchange.responseBody.use { it.write(byteArrayOf(1, 2, 3, 4)) }
        }
        server.start()
        try { block("http://127.0.0.1:${server.address.port}") } finally { server.stop(0) }
    }
    private val output = VoiceOutputConfiguration(provider = SpeechProvider.ElevenLabs,
        elevenLabs = ElevenLabsConfiguration("test", "voice", fallbackToSystem = true))
    @Test fun streamsAudioWithMicrophonePaused() = withServer(200) { base ->
        val fixture = SpeechFixture().apply { captured = true }
        ProviderVoiceService(fixture, ElevenLabsSpeech(fixture) { ElevenLabsSession(base) }).use {
            it.speak("Hello", output)
            assertEquals(2, fixture.samples)
            assertTrue(fixture.finished)
            assertTrue(fixture.spoken.isEmpty())
        }
    }
}
