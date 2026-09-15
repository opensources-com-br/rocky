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
