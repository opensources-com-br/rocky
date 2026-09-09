package dev.rocky.data.twitch

import kotlin.test.Test
import kotlin.test.assertEquals

class TwitchReconnectPolicyTest {
    @Test
    fun backsOffUntilThirtySeconds() {
        assertEquals(
            listOf(1L, 2L, 4L, 8L, 16L, 30L, 30L),
            (1..7).map(::twitchReconnectDelaySeconds),
        )
    }
}
