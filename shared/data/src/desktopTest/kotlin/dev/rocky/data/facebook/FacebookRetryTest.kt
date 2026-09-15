package dev.rocky.data.facebook

import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FacebookRetryTest {
    @Test fun respectsServerRetryDelay() {
        assertEquals(60_000L, facebookRetryDelay(FacebookRequestFailure(true, 60_000), 1))
    }

    @Test fun neverRetriesPermanentOrInterruptedFailures() {
        assertNull(facebookRetryDelay(FacebookRequestFailure(false), 1))
        assertNull(facebookRetryDelay(InterruptedException(), 1))
        assertNull(facebookRetryDelay(IllegalArgumentException(), 1))
    }

    @Test fun backsOffAndStopsAfterFiveFailures() {
        assertEquals(listOf(2000L, 4000L, 8000L, 16000L, 30000L),
            (1..5).map { facebookRetryDelay(IOException(), it) })
        assertNull(facebookRetryDelay(IOException(), 6))
    }
}
