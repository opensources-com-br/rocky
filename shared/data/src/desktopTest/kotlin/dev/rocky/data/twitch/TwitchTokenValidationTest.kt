package dev.rocky.data.twitch

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TwitchTokenValidationTest {
    @Test
    fun refreshesExpiredAccessTokenAndValidatesReplacement() {
        val validated = mutableListOf<String>()
        val result = validateTwitchTokens(
            TwitchTokens("expired", "refresh"),
            validate = {
                validated += it
                if (it == "expired") throw TwitchApiException(401, null)
            },
            refresh = {
                assertEquals("refresh", it)
                TwitchTokens("replacement", "next-refresh")
            },
        )
        assertEquals(listOf("expired", "replacement"), validated)
        assertEquals("next-refresh", result.refreshToken)
    }

    @Test
    fun transientFailureDoesNotRefreshOrDiscardCredentials() {
        assertFailsWith<java.io.IOException> {
            validateTwitchTokens(
                TwitchTokens("valid", "refresh"),
                validate = { throw java.io.IOException("offline") },
                refresh = { error("Must not refresh on network failure") },
            )
        }
    }
}
