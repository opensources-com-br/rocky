package dev.rocky.data.youtube

import java.net.ServerSocket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class YouTubeAuthorizationReceiverTest {
    @Test fun acceptsOnlyTheExpectedOAuthState() {
        val port = ServerSocket(0).use { it.localPort }
        val codes = mutableListOf<String>()
        YouTubeAuthorizationReceiver(
            "http://127.0.0.1:$port/oauth/youtube/callback",
            "expected",
            codes::add,
        ).use {
            val client = HttpClient.newHttpClient()
            fun callback(state: String) = client.send(
                HttpRequest.newBuilder(
                    URI.create("http://127.0.0.1:$port/oauth/youtube/callback?code=auth&state=$state"),
                ).GET().build(),
                HttpResponse.BodyHandlers.ofString(),
            )
            assertEquals(400, callback("wrong").statusCode())
            assertEquals(200, callback("expected").statusCode())
        }
        assertEquals(listOf("auth"), codes)
    }
}
