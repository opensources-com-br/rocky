package dev.rocky.data.twitch

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TwitchReconnectPolicyTest {
    @Test
    fun retriesSubscriptionTimeoutsAndServerFailuresOnly() {
        assertTrue(java.io.IOException("offline").isTransientTwitchFailure())
        assertTrue(TwitchApiException(429, null).isTransientTwitchFailure())
        assertTrue(TwitchApiException(503, null).isTransientTwitchFailure())
        assertFalse(TwitchApiException(403, null).isTransientTwitchFailure())
    }

    @Test
    fun backsOffUntilThirtySeconds() {
        assertEquals(
            listOf(1L, 2L, 4L, 8L, 16L, 30L, 30L),
            (1..7).map(::twitchReconnectDelaySeconds),
        )
    }

    @Test
    fun retriesPeriodicValidationAfterTransientFailures() {
        val now = 4_000_000L

        assertFalse(java.net.http.HttpTimeoutException("timeout").requiresNewTwitchAuthorization())
        assertEquals(
            now - TWITCH_TOKEN_VALIDATION_INTERVAL_MILLIS + 1_000,
            nextValidationRetryReferenceTime(now, 1),
        )
        assertEquals(
            now - TWITCH_TOKEN_VALIDATION_INTERVAL_MILLIS + 30_000,
            nextValidationRetryReferenceTime(now, 7),
        )
    }

    @Test
    fun requestsNewAuthorizationForRejectedCredentials() {
        assertTrue(TwitchApiException(401, "invalid token").requiresNewTwitchAuthorization())
        assertTrue(TwitchApiException(403, "forbidden").requiresNewTwitchAuthorization())
        assertFalse(TwitchApiException(500, "temporary failure").requiresNewTwitchAuthorization())
    }
}
