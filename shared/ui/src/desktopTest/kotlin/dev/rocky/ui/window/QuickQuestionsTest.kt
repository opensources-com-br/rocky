package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class QuickQuestionsTest {
    @get:Rule val rule = createComposeRule()
    @Test fun routesAllThreeActionsToGroundedRequests() {
        val requests = mutableListOf<String>()
        rule.setContent { Box(Modifier.width(600.dp)) { QuickQuestions(true) { requests.add(it) } } }
        rule.onNodeWithText("Dúvidas principais").performClick()
        rule.onNodeWithText("O que perdi?").performClick()
        rule.onNodeWithText("Ideias do chat").performClick()
        assertEquals(3, requests.size)
        assertTrue(requests[0].contains("amostra"))
        assertTrue(requests[1].contains("limites"))
        assertTrue(requests[2].contains("fundamentadas"))
    }
}
