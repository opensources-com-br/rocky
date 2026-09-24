package dev.rocky.ui.window

import dev.rocky.core.live.*
import org.junit.Test
import org.junit.Assert.*

class LiveWorkspaceTest {
    @Test fun excludesPreviousChatFromNewSession() {
        val workspace = LiveWorkspace(LocalNotesState(TransientNoteRepository()))
        workspace.start("first", "Rocky", 1000)
        val old = ChatMessage("old", "viewer", "Old", StreamPlatform.Twitch, sessionId = "first")
        assertTrue(workspace.includes(old))
        assertTrue(workspace.finish("end"))
        workspace.start("second", "Rocky", 2000)
        assertFalse(workspace.includes(old))
        assertTrue(workspace.includes(old.copy(id = "new", sessionId = "second")))
    }
    @Test fun keepsOneSessionUntilAllPlatformsDisconnect() {
        val records = LocalNotesState(TransientNoteRepository())
        val workspace = LiveWorkspace(records)
        workspace.start("twitch", "Rocky", 1000)
        workspace.start("kick", "Kick", 2000)
        assertTrue(workspace.includes(ChatMessage("k", "viewer", "Question", StreamPlatform.Kick, sessionId = "kick")))
        assertEquals("twitch", workspace.decorate(LiveNote("n", "Note", "now", "NOTE"), 3000).sessionId)
        assertEquals(2000L, workspace.offset(3000))
        assertTrue(workspace.finish("end"))
        workspace.start("kick", "Rocky", 2000)
        assertEquals("kick", workspace.sessionId)
    }
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
        assertNull(momentCommand("marca esse momentoseguinte"))
        assertEquals("1:02:03", momentLabel(3723000))
    }
    @Test fun failedSummaryPreservesTheSessionForRetry() {
        val storage = TransientNoteRepository()
        var offline = true
        val repository = object : dev.rocky.core.notes.NoteRepository by storage {
            override fun save(note: LiveNote) {
                if (offline) error("disk unavailable")
                storage.save(note)
            }
        }
        val workspace = LiveWorkspace(LocalNotesState(repository))
        workspace.start("live", "Live", 1000)
        assertFalse(workspace.finish("end"))
        assertEquals("live", workspace.sessionId)
        assertNull(workspace.summary)
        offline = false
        assertTrue(workspace.finish("end"))
        assertTrue(workspace.finish("end"))
        assertEquals(1, storage.getAll().count { it.tag == SUMMARY_TAG })
    }
}
