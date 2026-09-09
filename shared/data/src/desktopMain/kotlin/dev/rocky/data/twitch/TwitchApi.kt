package dev.rocky.data.twitch

import dev.rocky.core.twitch.TwitchAccount
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class TwitchApi(
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .build(),
) {
    fun startDeviceAuthorization(clientId: String): DeviceAuthorization {
        val response = postForm(
            DEVICE_ENDPOINT,
            mapOf("client_id" to clientId, "scopes" to CHAT_SCOPE),
        )
        response.requireSuccess()
        return TwitchAuthPayloads.deviceAuthorization(response.body())
    }

    fun pollDeviceTokens(clientId: String, deviceCode: String): TwitchTokens {
        val response = postForm(
            TOKEN_ENDPOINT,
            mapOf(
                "client_id" to clientId,
                "scopes" to CHAT_SCOPE,
                "device_code" to deviceCode,
                "grant_type" to DEVICE_GRANT,
            ),
        )
        response.requireSuccess()
        return TwitchAuthPayloads.tokens(response.body())
    }

    fun refreshTokens(clientId: String, refreshToken: String): TwitchTokens {
        val response = postForm(
            TOKEN_ENDPOINT,
            mapOf(
                "client_id" to clientId,
                "refresh_token" to refreshToken,
                "grant_type" to "refresh_token",
            ),
        )
        response.requireSuccess()
        return TwitchAuthPayloads.tokens(response.body())
    }

    fun validate(accessToken: String): TwitchAccount {
        val request = HttpRequest.newBuilder(URI.create(VALIDATE_ENDPOINT))
            .timeout(Duration.ofSeconds(20))
            .header("Authorization", "OAuth $accessToken")
            .GET()
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        response.requireSuccess()
        return TwitchAuthPayloads.account(response.body())
    }

    fun subscribeToChat(
        clientId: String,
        accessToken: String,
        account: TwitchAccount,
        sessionId: String,
    ) {
        val condition = buildJsonObject {
            put("broadcaster_user_id", account.userId)
            put("user_id", account.userId)
        }
        val transport = buildJsonObject {
            put("method", "websocket")
            put("session_id", sessionId)
        }
        val body = buildJsonObject {
            put("type", "channel.chat.message")
            put("version", "1")
            put("condition", condition)
            put("transport", transport)
        }.toString()
        val request = HttpRequest.newBuilder(URI.create(EVENTSUB_ENDPOINT))
            .timeout(Duration.ofSeconds(20))
            .header("Authorization", "Bearer $accessToken")
            .header("Client-Id", clientId)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        response.requireSuccess()
    }

    private fun postForm(endpoint: String, fields: Map<String, String>): HttpResponse<String> {
        val body = fields.entries.joinToString("&") { (name, value) ->
            "${name.urlEncode()}=${value.urlEncode()}"
        }
        val request = HttpRequest.newBuilder(URI.create(endpoint))
            .timeout(Duration.ofSeconds(20))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString())
    }

    private fun HttpResponse<String>.requireSuccess() {
        if (statusCode() !in 200..299) {
            throw TwitchApiException(
                statusCode = statusCode(),
                twitchMessage = TwitchAuthPayloads.error(body()),
            )
        }
    }

    private fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)

    private companion object {
        const val CHAT_SCOPE = "user:read:chat"
        const val DEVICE_ENDPOINT = "https://id.twitch.tv/oauth2/device"
        const val TOKEN_ENDPOINT = "https://id.twitch.tv/oauth2/token"
        const val VALIDATE_ENDPOINT = "https://id.twitch.tv/oauth2/validate"
        const val EVENTSUB_ENDPOINT = "https://api.twitch.tv/helix/eventsub/subscriptions"
        const val DEVICE_GRANT = "urn:ietf:params:oauth:grant-type:device_code"
    }
}

internal class TwitchApiException(
    val statusCode: Int,
    val twitchMessage: String?,
) : Exception(twitchMessage ?: "Twitch request failed with HTTP $statusCode")
