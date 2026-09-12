package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class ConversationLayoutTest {
    @get:Rule val rule = createComposeRule()

    @Test fun placesTheNewestMessageAtTheBottom() {
        rule.setContent {
            Box(Modifier.size(462.dp, 300.dp)) {
                ConversationContent(
                    messages = listOf(
                        ChatMessage("new", "viewer", "Newest", StreamPlatform.Twitch, receivedAtMillis = 3),
                        ChatMessage("old", "viewer", "Oldest", StreamPlatform.Twitch, receivedAtMillis = 1),
                        ChatMessage("middle", "viewer", "Middle", StreamPlatform.Twitch, receivedAtMillis = 2),
                    ),
                )
            }
        }

        val oldestTop = rule.onNodeWithText("Oldest").fetchSemanticsNode().boundsInRoot.top
        val middleTop = rule.onNodeWithText("Middle").fetchSemanticsNode().boundsInRoot.top
        val newestTop = rule.onNodeWithText("Newest").fetchSemanticsNode().boundsInRoot.top
        assertTrue("Message positions: $oldestTop, $middleTop, $newestTop", oldestTop < middleTop && middleTop < newestTop)
    }

    @Test fun preservesChatWhileBusyControlsScrollInShortWindow() {
        var cancelled = false
        rule.setContent {
            Box(Modifier.size(462.dp, 250.dp)) {
                ConversationContent(
                    messages = listOf(ChatMessage("m", "viewer", "Hello", StreamPlatform.Twitch)),
                    textRequestEnabled = true, analyzing = true,
                    streamerSpeech = "A long question that needs room in the conversation",
                    onCancelAnalysis = { cancelled = true },
                )
            }
        }
        val height = rule.onNodeWithTag("chat-messages").fetchSemanticsNode().boundsInRoot.height
        assertTrue("Chat height: $height", height >= 80f)
        rule.onNodeWithText("Cancel analysis").performScrollTo().performClick()
        assertTrue(cancelled)
        rule.onNodeWithText("Hello").assertIsDisplayed()
        rule.onNodeWithTag("streamer-text-request").performScrollTo().performTextReplacement("Next request")
        rule.onNodeWithTag("send-streamer-text-request").assertIsEnabled()
    }
}
