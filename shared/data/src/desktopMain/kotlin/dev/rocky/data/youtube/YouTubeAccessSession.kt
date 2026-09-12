package dev.rocky.data.youtube

import dev.rocky.core.youtube.YouTubeConfiguration

internal class YouTubeAccessSession(
    private val configuration: YouTubeConfiguration,
    private val tokenApi: YouTubeTokenApi,
    initialTokens: YouTubeTokens,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) {
    private var tokens = initialTokens
    private var expiresAtMillis = currentTimeMillis() + initialTokens.expiresInSeconds * 1000

    @Synchronized
    fun <T> request(block: (String) -> T): T {
        if (currentTimeMillis() >= expiresAtMillis - REFRESH_MARGIN_MILLIS) refresh()
        return try {
            block(tokens.accessToken)
        } catch (error: YouTubeApiException) {
            if (error.statusCode != 401) throw error
            refresh()
            block(tokens.accessToken)
        }
    }

    private fun refresh() {
        val refreshToken = requireNotNull(tokens.refreshToken) { "Reconecte o YouTube para renovar a autorização." }
        val refreshed = tokenApi.refresh(configuration.clientId, configuration.clientSecret, refreshToken)
        tokens = refreshed.copy(refreshToken = refreshed.refreshToken ?: refreshToken)
        expiresAtMillis = currentTimeMillis() + refreshed.expiresInSeconds * 1000
    }

    private companion object {
        const val REFRESH_MARGIN_MILLIS = 60_000L
    }
}
