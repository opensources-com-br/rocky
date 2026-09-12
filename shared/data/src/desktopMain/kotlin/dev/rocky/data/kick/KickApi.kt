package dev.rocky.data.kick

import dev.rocky.core.kick.KickAccount
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

internal class KickApi(
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15)).build(),
) {
    fun exchangeCode(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        verifier: String,
        code: String,
    ): KickTokens = postToken(mapOf(
        "grant_type" to "authorization_code",
        "client_id" to clientId,
        "client_secret" to clientSecret,
        "redirect_uri" to redirectUri,
        "code_verifier" to verifier,
        "code" to code,
    ))

    fun refresh(clientId: String, clientSecret: String, refreshToken: String): KickTokens =
        postToken(mapOf(
            "grant_type" to "refresh_token",
            "client_id" to clientId,
            "client_secret" to clientSecret,
            "refresh_token" to refreshToken,
        ))

    fun account(accessToken: String): KickAccount = KickPayloads.account(get(USERS_ENDPOINT, accessToken))

    fun viewerCount(accessToken: String): Int? = KickPayloads.viewerCount(get(CHANNELS_ENDPOINT, accessToken))

    fun publicKey(): String {
        val request = HttpRequest.newBuilder(URI.create(PUBLIC_KEY_ENDPOINT))
            .timeout(Duration.ofSeconds(20)).GET().build()
        return KickPayloads.publicKey(httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            .requireSuccess().body())
    }

    private fun get(endpoint: String, accessToken: String): String {
        val request = HttpRequest.newBuilder(URI.create(endpoint))
            .timeout(Duration.ofSeconds(20))
            .header("Authorization", "Bearer $accessToken")
            .GET().build()
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).requireSuccess().body()
    }

    private fun postToken(fields: Map<String, String>): KickTokens {
        val body = fields.entries.joinToString("&") { (key, value) ->
            "${key.urlEncode()}=${value.urlEncode()}"
        }
        val request = HttpRequest.newBuilder(URI.create(TOKEN_ENDPOINT))
            .timeout(Duration.ofSeconds(20))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body)).build()
        return KickPayloads.tokens(httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            .requireSuccess().body())
    }

    private fun HttpResponse<String>.requireSuccess(): HttpResponse<String> {
        if (statusCode() !in 200..299) throw KickApiException(statusCode(), KickPayloads.error(body()))
        return this
    }

    private companion object {
        const val TOKEN_ENDPOINT = "https://id.kick.com/oauth/token"
        const val USERS_ENDPOINT = "https://api.kick.com/public/v1/users"
        const val CHANNELS_ENDPOINT = "https://api.kick.com/public/v1/channels"
        const val PUBLIC_KEY_ENDPOINT = "https://api.kick.com/public/v1/public-key"
    }
}

internal class KickApiException(val statusCode: Int, kickMessage: String?) :
    Exception(kickMessage ?: "Kick request failed with HTTP $statusCode")
