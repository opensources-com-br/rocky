package dev.rocky.ui.window

import kotlin.test.*

class ConversationTurnsTest {
    @Test fun supersededRequestsCannotFinishANewTurn() {
        val turns = ConversationTurns { 100L }
        val old = turns.begin()
        val current = turns.begin()
        turns.finish(old)
        assertFalse(turns.accepts(old))
        assertTrue(turns.accepts(current))
        assertFalse(turns.acceptsFollowUp("resume isso"))
    }
    @Test fun wakeAndFollowUpWindowsExpire() {
        var now = 0L
        val turns = ConversationTurns { now }
        turns.awaitCommand()
        assertTrue(turns.awaitingCommand())
        now = 8_001
        assertFalse(turns.awaitingCommand())
        turns.finish(turns.begin())
        assertTrue(turns.acceptsFollowUp("explica melhor"))
        assertFalse(turns.acceptsFollowUp("vamos jogar agora"))
        now += 12_001
        assertFalse(turns.acceptsFollowUp("resume isso"))
    }
}
