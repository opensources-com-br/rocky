package dev.rocky.ui.window

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.rocky.core.live.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class RecordExperienceTest {
    @get:Rule val rule = createComposeRule()

    @Test fun marksGroupedQuestionAnsweredAndReopensIt() {
        val records = LocalNotesState(TransientNoteRepository())
        records.save(LiveNote("q", "Qual jogo?", "now", QUESTION_TAG, sessionId = "live", messageCount = 2))
        rule.setContent { QuestionQueueDialog(records, "live") {} }
        rule.onNodeWithText("Qual jogo?").assertIsDisplayed()
        rule.onNodeWithText("Marcar respondida").performClick()
        rule.onNodeWithText("Respondidas (1)").performClick()
        rule.onNodeWithText("Qual jogo?").assertIsDisplayed()
        rule.onNodeWithText("Reabrir").performClick()
        assertFalse(records.notes.single().completed)
    }

    @Test fun importsOnlyAfterConfirmationAndAllowsCancellation() {
        val records = LocalNotesState(TransientNoteRepository())
        val incoming = listOf(LiveNote("n", "imported", "now", "NOTA"))
        rule.setContent { RecordTransferSettings(records, { true }, { incoming }) }
        rule.onNodeWithText("Importar backup").performClick()
        assertTrue(records.notes.isEmpty())
        rule.onNodeWithText("Cancelar").performClick()
        assertTrue(records.notes.isEmpty())
        rule.onNodeWithText("Importar backup").performClick()
        rule.onNodeWithText("Importar").performClick()
        assertEquals(incoming, records.notes.toList())
    }
}
