package dev.rocky.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.LiveEvent
import dev.rocky.core.live.LiveNote
import dev.rocky.core.live.RockySuggestion
import dev.rocky.core.live.SimulatedLiveScript
import dev.rocky.core.live.StreamPlatform
import kotlinx.coroutines.delay

internal class SimulatedLiveState {
    val messages = mutableStateListOf<ChatMessage>()
    val notes = mutableStateListOf<LiveNote>()

    var suggestion by mutableStateOf<RockySuggestion?>(null)
        private set

    var suggestionSaved by mutableStateOf(false)
        private set

    val sourceCounts: Map<StreamPlatform, Int>
        get() = messages.groupingBy(ChatMessage::platform).eachCount()

    fun receive(event: LiveEvent) {
        when (event) {
            is LiveEvent.MessageReceived -> messages += event.message
            is LiveEvent.SuggestionCreated -> suggestion = event.suggestion
        }
    }

    fun saveSuggestion(): Boolean {
        val currentSuggestion = suggestion ?: return false
        if (suggestionSaved) return false

        notes.add(
            LiveNote(
                id = "note-${currentSuggestion.id}",
                text = currentSuggestion.text,
                timestamp = "agora",
                tag = "SUGESTÃO",
            ),
        )
        suggestionSaved = true
        return true
    }

    fun dismissSuggestion() {
        suggestion = null
    }
}

@Composable
internal fun rememberSimulatedLiveState(): SimulatedLiveState {
    val state = remember { SimulatedLiveState() }
    LaunchedEffect(state) {
        SimulatedLiveScript.events.forEach { timedEvent ->
            delay(timedEvent.delayMillis)
            state.receive(timedEvent.event)
        }
    }
    return state
}
