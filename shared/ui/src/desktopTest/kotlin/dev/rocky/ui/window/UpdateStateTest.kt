package dev.rocky.ui.window

import dev.rocky.core.updates.AvailableUpdate
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class UpdateStateTest {
    @Test fun recoversAfterFailureAndClearsDismissalOnSuccessfulCheck() = runBlocking {
        var fail = true
        val state = UpdateState { if (fail) error("private network details") else AvailableUpdate("v2.0.0", "") }
        state.check(this)
        withTimeout(5000) { while (state.checking) delay(10) }
        assertNull(state.available)
        assertEquals(UpdateCheckNotice.Failed, state.notice)
        fail = false
        state.dismissed = true
        state.check(this)
        withTimeout(5000) { while (state.checking) delay(10) }
        assertEquals("v2.0.0", state.available?.version)
        assertFalse(state.dismissed)
    }
    @Test fun cancelledScopeDoesNotLeaveCheckBusy() = runBlocking {
        val scope = CoroutineScope(Job().apply { cancel() })
        val state = UpdateState { error("Must not contact provider") }
        state.check(scope)
        yield()
        assertFalse(state.checking)
        assertNull(state.available)
    }
}
