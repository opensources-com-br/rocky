package dev.rocky.data.kick

import java.net.ServerSocket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.KeyPairGenerator
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals

class KickReceiverBoundsTest {
    @Test fun requiresExactCallbackAndWebhookPaths() = withReceiver { base ->
        assertEquals(404, send(HttpRequest.newBuilder(URI.create("$base/oauth/kick/callback/extra"))
            .GET().build()))
        assertEquals(404, send(HttpRequest.newBuilder(URI.create("$base/webhooks/kick/extra"))
            .POST(HttpRequest.BodyPublishers.noBody()).build()))
    }

    @Test fun rejectsOversizedWebhookBodies() = withReceiver { base ->
        val request = HttpRequest.newBuilder(URI.create("$base/webhooks/kick"))
            .POST(HttpRequest.BodyPublishers.ofString("x".repeat(1_048_577))).build()
        assertEquals(413, send(request))
    }

    private fun withReceiver(test: (String) -> Unit) {
        val port = ServerSocket(0).use { it.localPort }
        val keys = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val pem = "-----BEGIN PUBLIC KEY-----\n${Base64.getEncoder().encodeToString(keys.public.encoded)}\n-----END PUBLIC KEY-----"
        KickLocalReceiver("http://localhost:$port/oauth/kick/callback", "state", pem, {}, {}).use {
            test("http://localhost:$port")
        }
    }

    private fun send(request: HttpRequest): Int =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).statusCode()
}
