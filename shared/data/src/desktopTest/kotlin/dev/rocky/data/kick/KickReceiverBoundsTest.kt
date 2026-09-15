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
    private fun send(request: HttpRequest): Int =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).statusCode()
}
