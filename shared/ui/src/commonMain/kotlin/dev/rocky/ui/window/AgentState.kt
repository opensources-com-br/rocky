package dev.rocky.ui.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.agent.AgentTone

internal class AgentState(
    initialConfiguration: AgentConfiguration,
    private val onConfigurationChange: (AgentConfiguration) -> Unit,
) {
    var configuration by mutableStateOf(initialConfiguration)
        private set

    val displayName: String
        get() = configuration.name.trim().ifBlank { "Rocky" }

    fun updateName(name: String) = update(configuration.copy(name = name.take(MAX_NAME_LENGTH)))

    fun updateTone(tone: AgentTone) = update(configuration.copy(tone = tone))

    fun updateFrequency(value: Int) = update(
        configuration.copy(interventionsPerTenMinutes = value.coerceIn(1, 9)),
    )

    private fun update(value: AgentConfiguration) {
        configuration = value
        onConfigurationChange(value)
    }

    companion object {
        private const val MAX_NAME_LENGTH = 40
    }
}
