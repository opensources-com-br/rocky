package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import dev.rocky.core.agent.InterventionProfile

@Composable
internal fun ContextSettings(ai: AiSuggestionState) {
    var open by remember { mutableStateOf(false) }
    Column {
        val labels = listOf(tr("On demand", "Sob demanda"), tr("Discreet", "Discreto"), tr("Proactive", "Proativo"))
        ChoiceRow(labels, labels[ai.profile.ordinal]) { ai.updateProfile(InterventionProfile.entries[labels.indexOf(it)]) }
        Text(tr("Automatic limits: off / once per 5 minutes / once per 2 minutes. Direct questions take priority.",
            "Limites automáticos: desligado / uma vez a cada 5 minutos / uma vez a cada 2 minutos. Perguntas diretas têm prioridade."),
            style = MaterialTheme.typography.caption)
        TextButton(onClick = { open = !open }) { Text(tr("Chat context filters", "Filtros do contexto")) }
        if (open) {
            SettingSwitch(tr("Ignore !commands and /commands", "Ignorar !comandos e /comandos"), ai.filters.commands, { ai.updateFilters(ai.filters.copy(commands = it)) })
            SettingSwitch(tr("Limit repeated messages and author bursts", "Limitar repetições e rajadas por autor"), ai.filters.repetitions, { ai.updateFilters(ai.filters.copy(repetitions = it)) })
            OutlinedTextField(value = ai.filters.bots.joinToString(","), onValueChange = {
                ai.updateFilters(ai.filters.copy(bots = it.take(2000).split(',').map(String::trim).toSet()))
            }, label = { Text(tr("Ignored bot names, comma separated", "Bots ignorados, separados por vírgula")) })
            Text(tr("Filters affect AI and the question queue; the displayed chat stays intact.",
                "Os filtros afetam IA e fila de perguntas; o chat exibido permanece intacto."), style = MaterialTheme.typography.caption)
        }
    }
}
