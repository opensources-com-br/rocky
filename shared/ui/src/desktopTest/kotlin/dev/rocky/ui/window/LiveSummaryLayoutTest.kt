package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import dev.rocky.core.live.LiveSessionStatus
import dev.rocky.core.live.RockySuggestion
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LiveSummaryLayoutTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun givesLongSuggestionsRoomToWrap() {
        val text = "Resposta detalhada para o streamer. ".repeat(20)
        rule.setContent {
            Box(Modifier.size(462.dp, 400.dp)) {
                LiveSummary(
                    suggestion = RockySuggestion("suggestion", text, emptySet()),
                    sessionStatus = LiveSessionStatus.Running,
                    onSaveNote = {},
                    onNext = {},
                    onSilence = {},
                )
            }
        }

        val height = rule.onNodeWithText(text).fetchSemanticsNode().boundsInRoot.height
        assertTrue("Suggestion height: $height", height >= 160f)
    }
}
