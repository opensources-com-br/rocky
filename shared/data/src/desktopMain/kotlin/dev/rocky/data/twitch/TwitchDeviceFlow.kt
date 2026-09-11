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
        val authorization = retryTransientTwitchRequest {
            api.startDeviceAuthorization(clientId)
        }
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
                when (error.twitchMessage?.lowercase()?.replace(' ', '_')) {
                    "authorization_pending" -> Unit
                    "slow_down" -> intervalMillis += 5_000
                    else -> throw error
                }
            }
        }
        return null
    }
}

internal fun <T> retryTransientTwitchRequest(
    maxAttempts: Int = 3,
    sleep: (Long) -> Unit = Thread::sleep,
    request: () -> T,
): T {
    var attempt = 1
    while (true) {
        try {
            return request()
        } catch (error: Throwable) {
            if (attempt >= maxAttempts || !error.isTransientTwitchFailure()) throw error
            sleep(twitchReconnectDelaySeconds(attempt) * 1_000)
            attempt += 1
        }
    }
}
