package dev.rocky.data.voice

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.voice.*
import java.net.InetSocketAddress
import java.util.concurrent.*
import kotlin.test.*

class SpeechCancellationTest {
    @Test fun cancelUnblocksAResponseWaitingForHeadersWithoutFallback() {
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange -> entered.countDown(); release.await(5, TimeUnit.SECONDS); exchange.close() }
        server.start()
        val executor = Executors.newSingleThreadExecutor()
        val fixture = SpeechFixture()
        val service = ProviderVoiceService(fixture, ElevenLabsSpeech(fixture) {
            ElevenLabsSession("http://127.0.0.1:${server.address.port}")
        })
        try {
            val future = executor.submit { service.speak("Hello", VoiceOutputConfiguration(
                provider = SpeechProvider.ElevenLabs, elevenLabs = ElevenLabsConfiguration("key", "voice", fallbackToSystem = true))) }
            assertTrue(entered.await(3, TimeUnit.SECONDS))
            service.stopSpeaking()
            assertFailsWith<ExecutionException> { future.get(2, TimeUnit.SECONDS) }
            assertTrue(fixture.spoken.isEmpty())
        } finally { release.countDown(); service.close(); executor.shutdownNow(); server.stop(0) }
    }
    @Test fun cancelUnblocksAStreamWaitingForMoreAudio() {
        val release = CountDownLatch(1)
        val received = CountDownLatch(1)
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            exchange.sendResponseHeaders(200, 0)
            exchange.responseBody.use { it.write(byteArrayOf(1, 2)); it.flush(); release.await(5, TimeUnit.SECONDS) }
        }
        server.start()
        val executor = Executors.newSingleThreadExecutor()
        val session = ElevenLabsSession("http://127.0.0.1:${server.address.port}")
        try {
            val future = executor.submit {
                session.open("/", "key").use { input ->
                    check(input.readNBytes(2).size == 2)
                    received.countDown()
                    input.read()
                    session.checkActive()
                }
            }
            assertTrue(received.await(3, TimeUnit.SECONDS))
            session.close()
            assertFailsWith<ExecutionException> { future.get(2, TimeUnit.SECONDS) }
        } finally { release.countDown(); session.close(); executor.shutdownNow(); server.stop(0) }
    }
}
