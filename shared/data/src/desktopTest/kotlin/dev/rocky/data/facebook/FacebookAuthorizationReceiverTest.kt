package dev.rocky.data.facebook

import java.net.ServerSocket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FacebookAuthorizationReceiverTest {
    @Test fun acceptsOnlyTheExpectedOAuthState() {
        val port = ServerSocket(0).use { it.localPort }
        val codes = ConcurrentLinkedQueue<String>()
        val received = CountDownLatch(1)
        FacebookAuthorizationReceiver(
            "http://127.0.0.1:$port/oauth/facebook/callback",
            "expected",
            { code -> codes.add(code); received.countDown() },
        ).use {
            val client = HttpClient.newHttpClient()
            fun callback(state: String) = client.send(
                HttpRequest.newBuilder(
                    URI.create("http://127.0.0.1:$port/oauth/facebook/callback?code=$state-auth&state=$state"),
                ).GET().build(),
                HttpResponse.BodyHandlers.ofString(),
            )
            assertEquals(400, callback("wrong").statusCode())
            assertEquals(200, callback("expected").statusCode())
            assertTrue(received.await(5, TimeUnit.SECONDS), "The authorization callback did not finish")
        }
        assertEquals(listOf("expected-auth"), codes.toList())
    }
}
