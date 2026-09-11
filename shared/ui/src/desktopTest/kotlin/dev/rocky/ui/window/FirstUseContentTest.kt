package dev.rocky.ui.window

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirstUseContentTest {
    @Test
    fun requiresTwitchAndAiButNotVoice() {
        assertFalse(firstUseReady(twitchConnected = true, aiVerified = false, voiceVerified = true))
        assertFalse(firstUseReady(twitchConnected = false, aiVerified = true, voiceVerified = true))
        assertFalse(firstUseReady(twitchConnected = true, aiVerified = true, voiceVerified = false))
        assertTrue(firstUseReady(twitchConnected = true, aiVerified = true, voiceVerified = true))
    }
}
