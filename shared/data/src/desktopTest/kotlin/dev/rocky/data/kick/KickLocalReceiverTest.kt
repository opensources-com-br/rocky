package dev.rocky.data.kick

import java.net.ServerSocket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.KeyPairGenerator
import java.security.Signature
import java.time.Instant
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals

class KickLocalReceiverTest {
    @Test fun acceptsCallbackAndSignedChatOnce() {
        val port = ServerSocket(0).use { it.localPort }
        val keys = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val pem = "-----BEGIN PUBLIC KEY-----\n${Base64.getMimeEncoder().encodeToString(keys.public.encoded)}\n-----END PUBLIC KEY-----"
        val codes = mutableListOf<String>()
        val messages = mutableListOf<String>()
        KickLocalReceiver("http://localhost:$port/oauth/kick/callback", "state", pem,
            codes::add, { messages += it.text }).use {
            val client = HttpClient.newHttpClient()
            val callback = client.send(HttpRequest.newBuilder(
                URI.create("http://localhost:$port/oauth/kick/callback?code=auth&state=state"))
                .GET().build(), HttpResponse.BodyHandlers.ofString())
            assertEquals(200, callback.statusCode())
            val body = """{"message_id":"01ABC","broadcaster":{"user_id":42},"sender":{"user_id":9,"username":"viewer"},"content":"Olá","created_at":"2026-09-12T12:00:00Z"}"""
            val timestamp = Instant.now().toString()
            val signature = Signature.getInstance("SHA256withRSA").run {
                initSign(keys.private); update("01ABC.$timestamp.$body".toByteArray())
                Base64.getEncoder().encodeToString(sign())
            }
            val request = HttpRequest.newBuilder(URI.create("http://localhost:$port/webhooks/kick"))
                .header("Kick-Event-Message-Id", "01ABC")
                .header("Kick-Event-Message-Timestamp", timestamp)
                .header("Kick-Event-Signature", signature)
                .header("Kick-Event-Type", "chat.message.sent")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build()
            assertEquals(204, client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode())
            assertEquals(401, client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode())
        }
        assertEquals(listOf("auth"), codes)
        assertEquals(listOf("Olá"), messages)
    }
}
