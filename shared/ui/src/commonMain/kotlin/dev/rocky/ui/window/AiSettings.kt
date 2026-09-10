package dev.rocky.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun AiSettings(ai: AiSuggestionState) {
    val scope = rememberCoroutineScope()
    val providerLabel = if (ai.configuration.provider == AiProviderKind.Ollama) "Ollama local" else "OpenAI API"

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        SettingTitle(
            "Provedor de IA",
            "Escolha processamento local ou uma API configurada por você.",
            providerLabel,
        )
        ChoiceRow(listOf("Ollama local", "OpenAI API"), providerLabel) { selected ->
            ai.updateProvider(if (selected == "Ollama local") AiProviderKind.Ollama else AiProviderKind.OpenAI)
        }
        Spacer(Modifier.height(14.dp))
        AiTextField(
            value = ai.configuration.endpoint,
            label = "Endereço do provedor",
            placeholder = "http://localhost:11434",
            tag = "ai-endpoint",
            onValueChange = ai::updateEndpoint,
        )
        Spacer(Modifier.height(10.dp))
        AiTextField(
            value = ai.configuration.model,
            label = "Modelo",
            placeholder = "llama3.2",
            tag = "ai-model",
            onValueChange = ai::updateModel,
        )
        if (ai.configuration.provider == AiProviderKind.OpenAI) {
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = ai.configuration.apiKey,
                onValueChange = ai::updateApiKey,
                modifier = Modifier.fillMaxWidth().testTag("ai-api-key"),
                label = { Text("API key") },
                placeholder = { Text("sk-…") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )
            Text(
                "A chave fica apenas na memória e será apagada ao fechar o Rocky.",
                modifier = Modifier.padding(top = 5.dp),
                color = RockyColors.TextMuted,
                style = MaterialTheme.typography.caption,
            )
        }
        Button(
            onClick = { ai.testConnection(scope) },
            modifier = Modifier.padding(top = 14.dp).testTag("ai-test-connection"),
            enabled = !ai.testing && ai.configuration.model.isNotBlank() &&
                (ai.configuration.provider != AiProviderKind.OpenAI || ai.configuration.apiKey.isNotBlank()),
            colors = ButtonDefaults.buttonColors(backgroundColor = RockyColors.Accent, contentColor = Color.Black),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp),
        ) {
            Text(if (ai.testing) "Testando…" else "Testar conexão", fontWeight = FontWeight.Bold)
        }
        ai.status?.let { status ->
            Text(
                text = status,
                modifier = Modifier.padding(top = 8.dp).testTag("ai-status"),
                color = RockyColors.TextSecondary,
                style = MaterialTheme.typography.caption,
            )
        }
        Spacer(Modifier.height(16.dp))
        SettingTitle(
            "Sugestões automáticas",
            "Analisa o chat em lotes de três mensagens para evitar uma chamada por comentário.",
            if (ai.automaticAnalysis) "ativo" else "inativo",
        )
        Spacer(Modifier.height(8.dp))
        SettingSwitch("analisar durante a live", ai.automaticAnalysis, ai::updateAutomaticAnalysis)
        Text(
            "Ao usar uma API, as mensagens selecionadas são enviadas ao provedor configurado.",
            modifier = Modifier.padding(top = 10.dp),
            color = RockyColors.TextMuted,
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
private fun AiTextField(
    value: String,
    label: String,
    placeholder: String,
    tag: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().testTag(tag),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
    )
}
