package dev.rocky.data.youtube

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

internal class YouTubeTokenApi(
    private val httpClient: HttpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build(),
    private val tokenEndpoint: String = "https://oauth2.googleapis.com/token",
) {
    fun exchangeCode(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        verifier: String,
        code: String,
    ): YouTubeTokens = requestTokens(linkedMapOf(
        "client_id" to clientId,
        "client_secret" to clientSecret,
        "redirect_uri" to redirectUri,
        "grant_type" to "authorization_code",
        "code_verifier" to verifier,
        "code" to code,
    ))

    fun refresh(clientId: String, clientSecret: String, refreshToken: String): YouTubeTokens =
        requestTokens(linkedMapOf(
            "client_id" to clientId,
            "client_secret" to clientSecret,
            "refresh_token" to refreshToken,
            "grant_type" to "refresh_token",
        ))

    private fun requestTokens(fields: Map<String, String>): YouTubeTokens {
        val body = fields.entries.joinToString("&") { (key, value) ->
            "${key.youtubeUrlEncode()}=${value.youtubeUrlEncode()}"
        }
        val request = HttpRequest.newBuilder(URI.create(tokenEndpoint))
            .timeout(Duration.ofSeconds(20))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        return YouTubePayloads.tokens(
            httpClient.send(request, HttpResponse.BodyHandlers.ofString()).requireYouTubeSuccess().body(),
        )
    }
}
