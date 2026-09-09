package dev.rocky.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.LiveEvent
import dev.rocky.core.live.LiveNote
import dev.rocky.core.live.LiveSessionMode
import dev.rocky.core.live.LiveSessionStatus
import dev.rocky.core.live.RockySuggestion
import dev.rocky.core.live.SimulatedLiveScript
import dev.rocky.core.live.StreamPlatform
import kotlinx.coroutines.delay
import kotlin.random.Random

internal class SimulatedLiveState {
    val messages = mutableStateListOf<ChatMessage>()
    val mode = LiveSessionMode.Demonstration

    var status by mutableStateOf(LiveSessionStatus.Stopped)
        private set

    var sessionNumber by mutableIntStateOf(0)
        private set

    var suggestion by mutableStateOf<RockySuggestion?>(null)
        private set

    var suggestionSaved by mutableStateOf(false)
        private set

    val sourceCounts: Map<StreamPlatform, Int>
        get() = messages.groupingBy(ChatMessage::platform).eachCount()

    fun receive(event: LiveEvent) {
        if (status != LiveSessionStatus.Running) return

        when (event) {
            is LiveEvent.MessageReceived -> messages += event.message
            is LiveEvent.SuggestionCreated -> suggestion = event.suggestion
        }
    }

    fun createNoteFromSuggestion(): LiveNote? {
        val currentSuggestion = suggestion ?: return null
        if (suggestionSaved) return null

        val note = LiveNote(
            id = "note-${Random.nextLong()}",
            text = currentSuggestion.text,
            timestamp = "agora",
            tag = "SUGESTÃO",
        )
        return note
    }

    fun markSuggestionSaved() {
        suggestionSaved = true
    }

    fun dismissSuggestion() {
        suggestion = null
    }

    fun start() {
        if (status != LiveSessionStatus.Stopped) return
        beginSession()
    }

    fun end() {
        if (status == LiveSessionStatus.Running) {
            status = LiveSessionStatus.Ended
        }
    }

    fun restart() {
        if (status != LiveSessionStatus.Ended) return
        beginSession()
    }

    private fun beginSession() {
        messages.clear()
        suggestion = null
        suggestionSaved = false
        sessionNumber += 1
        status = LiveSessionStatus.Running
    }
}

@Composable
internal fun rememberSimulatedLiveState(): SimulatedLiveState {
    val state = remember { SimulatedLiveState() }
    LaunchedEffect(state.status, state.sessionNumber) {
        if (state.status == LiveSessionStatus.Running) {
            SimulatedLiveScript.events.forEach { timedEvent ->
                delay(timedEvent.delayMillis)
                state.receive(timedEvent.event)
            }
        }
    }
    return state
}
