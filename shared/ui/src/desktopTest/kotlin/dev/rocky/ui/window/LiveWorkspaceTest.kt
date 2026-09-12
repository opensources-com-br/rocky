package dev.rocky.ui.window

import dev.rocky.core.live.*
import org.junit.Test
import org.junit.Assert.*

class LiveWorkspaceTest {
    @Test fun summarizesOnlyCurrentRecordsAndPersistsOnce() {
        val repo = TransientNoteRepository()
        val records = LocalNotesState(repo)
        val workspace = LiveWorkspace(records)
        workspace.start("live", "Live", 1000)
        val idea = workspace.decorate(LiveNote("i", "Fazer tutorial", "now", IDEA_TAG, completed = true), 43000)
        assertEquals(42000L, idea.offsetMillis)
        records.save(idea)
        records.save(idea.copy(id = "old", text = "OUTRA LIVE", sessionId = "old"))
        records.save(idea.copy(id = "q", text = "Pergunta pendente?", tag = QUESTION_TAG, completed = false))
        records.save(idea.copy(id = "a", text = "Já respondida?", tag = QUESTION_TAG))
        assertTrue(workspace.finish("end"))
        assertTrue(workspace.finish("end"))
        val summary = repo.getAll().single { it.tag == SUMMARY_TAG }
        assertTrue(summary.text.contains("Fazer tutorial"))
        assertTrue(summary.text.contains("realizada"))
        assertTrue(summary.text.contains("Pergunta pendente?"))
        assertFalse(summary.text.contains("OUTRA LIVE"))
        assertFalse(summary.text.contains("Já respondida?"))
        assertEquals("live", summary.sessionId)
        assertEquals("", workspace.sessionId)
    }
    @Test fun recognizesNamedAndUnnamedVoiceMarkers() {
        assertEquals("Momento marcado", momentCommand("marca esse momento"))
        assertEquals("vitória", momentCommand("marca esse momento: vitória"))
        assertNull(momentCommand("qual foi o melhor momento?"))
        assertEquals("1:02:03", momentLabel(3723000))
    }
}
