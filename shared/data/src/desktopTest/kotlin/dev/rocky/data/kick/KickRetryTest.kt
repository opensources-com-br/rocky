package dev.rocky.data.kick

import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KickRetryTest {
    @Test fun retriesServiceAndRateLimitFailuresButNotPermissions() {
        assertEquals(2000L, kickRetryDelay(KickApiException(503, null), 1))
        assertEquals(2000L, kickRetryDelay(KickApiException(429, null), 1))
        assertNull(kickRetryDelay(KickApiException(401, null), 1))
        assertNull(kickRetryDelay(KickApiException(403, null), 1))
    }

    @Test fun backsOffAndLimitsRetries() {
        assertEquals(listOf(2000L, 4000L, 8000L, 16000L, 30000L),
            (1..5).map { kickRetryDelay(IOException(), it) })
        assertNull(kickRetryDelay(IOException(), 6))
        assertNull(kickRetryDelay(InterruptedException(), 1))
    }
}
