package dev.rocky.data.twitch

import dev.rocky.core.twitch.TwitchAccount
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal data class DeviceAuthorization(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresInSeconds: Long,
    val intervalSeconds: Long,
)

internal data class TwitchTokens(
    val accessToken: String,
    val refreshToken: String,
)

internal object TwitchAuthPayloads {
    private val json = Json { ignoreUnknownKeys = true }

    fun deviceAuthorization(body: String): DeviceAuthorization {
        val payload = body.asObject()
        return DeviceAuthorization(
            deviceCode = payload.requiredString("device_code"),
            userCode = payload.requiredString("user_code"),
            verificationUri = payload.requiredString("verification_uri"),
            expiresInSeconds = payload.requiredLong("expires_in"),
            intervalSeconds = payload.requiredLong("interval"),
        )
    }

    fun tokens(body: String): TwitchTokens {
        val payload = body.asObject()
        return TwitchTokens(
            accessToken = payload.requiredString("access_token"),
            refreshToken = payload.requiredString("refresh_token"),
        )
    }

    fun account(body: String): TwitchAccount {
        val payload = body.asObject()
        return TwitchAccount(
            userId = payload.requiredString("user_id"),
            login = payload.requiredString("login"),
        )
    }

    fun error(body: String): String? = runCatching {
        val payload = body.asObject()
        payload["message"]?.jsonPrimitive?.content
            ?: payload["error"]?.jsonPrimitive?.content
    }.getOrNull()

    private fun String.asObject(): JsonObject = json.parseToJsonElement(this).jsonObject
}

private fun JsonObject.requiredString(name: String): String =
    requireNotNull(this[name]) { "Missing Twitch field: $name" }.jsonPrimitive.content

private fun JsonObject.requiredLong(name: String): Long =
    requireNotNull(this[name]) { "Missing Twitch field: $name" }.jsonPrimitive.content.toLong()
