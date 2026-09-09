package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun AiSettings() {
    var model by remember { mutableStateOf("Claude Sonnet 4.5") }
    var creativity by remember { mutableStateOf(0.4f) }
    var context by remember { mutableStateOf("Últimos 10 min") }
    var postLiveSummary by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        SettingTitle("Modelo", "Modelo usado para ler o chat e formular falas.", model)
        ChoiceRow(
            listOf("Claude Sonnet 4.5", "Claude Haiku", "Local (offline)"),
            model,
        ) { model = it }
        Spacer(Modifier.height(16.dp))
        SettingTitle(
            "Criatividade",
            "Baixo é literal ao chat, alto sugere mais ideias próprias.",
            "${(creativity * 100).toInt()}%",
        )
        RockySlider(creativity) { creativity = it }
        Spacer(Modifier.height(8.dp))
        SettingTitle("Janela de contexto", "Quanto do histórico do chat ele considera.", context)
        ChoiceRow(listOf("Últimos 2 min", "Últimos 10 min", "Live inteira"), context) { context = it }
        Spacer(Modifier.height(16.dp))
        SettingTitle(
            "Resumo pós-live",
            "Gera notas, clipes sugeridos e dúvidas não respondidas.",
            if (postLiveSummary) "ativo" else "inativo",
        )
        Spacer(Modifier.height(8.dp))
        SettingSwitch("gerar ao encerrar", postLiveSummary) { postLiveSummary = it }
    }
}
