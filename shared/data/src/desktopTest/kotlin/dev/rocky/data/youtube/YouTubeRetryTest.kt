package dev.rocky.data.youtube

import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class YouTubeRetryTest {
    @Test fun backsOffAndStopsAfterFiveFailures() {
        assertEquals(listOf(2000L, 4000L, 8000L, 16000L, 30000L),
            (1..5).map { youtubeRetryDelay(IOException(), it) })
        assertNull(youtubeRetryDelay(IOException(), 6))
        assertNull(youtubeRetryDelay(InterruptedException(), 1))
    }
}
