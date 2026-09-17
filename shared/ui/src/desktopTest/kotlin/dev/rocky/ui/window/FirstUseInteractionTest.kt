package dev.rocky.ui.window

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue

class FirstUseInteractionTest {
    @get:Rule val rule = createComposeRule()

    private fun render(platform: Boolean, ai: Boolean, voice: Boolean = false, complete: () -> Unit = {}) {
        rule.setContent {
            FirstUseContent(platform, ai, voice, {}, {}, {}, complete)
        }
    }

    @Test fun showsSetupGuide() {
        render(platform = false, ai = false)
        rule.onNodeWithText("Set up Rocky").assertIsDisplayed()
    }

    @Test fun requiresConnectedPlatform() {
        render(platform = false, ai = true, voice = true)
        rule.onNodeWithText("Finish setup").assertIsNotEnabled()
    }

    @Test fun requiresVerifiedAi() {
        render(platform = true, ai = false, voice = true)
        rule.onNodeWithText("Finish setup").assertIsNotEnabled()
    }
}
