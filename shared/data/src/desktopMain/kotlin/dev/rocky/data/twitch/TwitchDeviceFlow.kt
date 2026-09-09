package dev.rocky.data.twitch

import dev.rocky.core.twitch.TwitchAccount

internal data class TwitchAuthentication(
    val tokens: TwitchTokens,
    val account: TwitchAccount,
)

internal class TwitchDeviceFlow(private val api: TwitchApi) {
    fun authenticate(
        clientId: String,
        isActive: () -> Boolean,
        onAuthorization: (DeviceAuthorization) -> Unit,
    ): TwitchAuthentication? {
        val authorization = api.startDeviceAuthorization(clientId)
        onAuthorization(authorization)
        val deadline = System.currentTimeMillis() + authorization.expiresInSeconds * 1_000
        var intervalMillis = authorization.intervalSeconds * 1_000

        while (isActive() && System.currentTimeMillis() < deadline) {
            Thread.sleep(intervalMillis)
            try {
                val tokens = api.pollDeviceTokens(clientId, authorization.deviceCode)
                return TwitchAuthentication(
                    tokens = tokens,
                    account = api.validate(tokens.accessToken),
                )
            } catch (error: TwitchApiException) {
                when (error.twitchMessage?.lowercase()) {
                    "authorization_pending" -> Unit
                    "slow_down" -> intervalMillis += 5_000
                    else -> throw error
                }
            }
        }
        return null
    }
}
