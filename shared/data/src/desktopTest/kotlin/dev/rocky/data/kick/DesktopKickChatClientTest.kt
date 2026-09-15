package dev.rocky.data.kick

import com.sun.net.httpserver.HttpServer
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.kick.KickConnectionEvent
import dev.rocky.core.kick.KickConnectionPhase
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.KeyPairGenerator
import java.util.Base64
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DesktopKickChatClientTest {
    private val requests = CopyOnWriteArrayList<String>()
    private val events = CopyOnWriteArrayList<KickConnectionEvent>()
    private var audienceFailure = 0
    private val keyPem = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        .public.encoded.let { "-----BEGIN PUBLIC KEY-----\\n${Base64.getEncoder().encodeToString(it)}\\n-----END PUBLIC KEY-----" }
    private fun response(path: String) = when (path) {
        "/key" -> """{"data":{"public_key":"$keyPem"}}"""
        "/token" -> """{"access_token":"access","refresh_token":"refresh"}"""
        "/users" -> """{"data":[{"user_id":42,"name":"Rocky"}]}"""
        "/channels" -> """{"data":[{"stream":{"viewer_count":42}}]}"""
        "/subscriptions" -> """{"data":[{"subscription_id":"sub"}]}"""
        else -> error("Unexpected path: $path")
    }
}
