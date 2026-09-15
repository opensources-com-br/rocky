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
    private fun withClient(test: (DesktopKickChatClient) -> Unit) {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val path = exchange.requestURI.path
            requests += "${exchange.requestMethod} $path"
            val status = if (path == "/channels" && requests.count { it == "GET /channels" } == 1)
                audienceFailure.takeIf { it != 0 } ?: 200 else 200
            val bytes = response(path).toByteArray()
            exchange.sendResponseHeaders(status, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
        server.start()
        val base = "http://127.0.0.1:${server.address.port}"
        val client = DesktopKickChatClient(KickApi(tokenEndpoint = "$base/token", usersEndpoint = "$base/users",
            channelsEndpoint = "$base/channels", publicKeyEndpoint = "$base/key"),
            KickEventSubscriptions(endpoint = "$base/subscriptions"))
        try { test(client) } finally { client.close(); server.stop(0) }
    }
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
