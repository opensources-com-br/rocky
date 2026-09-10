package dev.rocky.ui.window

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirstUseContentTest {
    @Test
    fun requiresTwitchAndAiButNotVoice() {
        assertFalse(firstUseReady(twitchConnected = true, aiVerified = false))
        assertFalse(firstUseReady(twitchConnected = false, aiVerified = true))
        assertTrue(firstUseReady(twitchConnected = true, aiVerified = true))
    }
}
