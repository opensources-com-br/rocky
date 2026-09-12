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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.ui.theme.RockyColors

@Composable
internal fun AiSettings(ai: AiSuggestionState) {
    val scope = rememberCoroutineScope()
    var modelsOpen by remember { mutableStateOf(false) }
    var advanced by remember { mutableStateOf(false) }
    var apiKeyVisible by remember { mutableStateOf(false) }
    val providerLabel = when (ai.configuration.provider) {
        AiProviderKind.Ollama -> "Ollama local"
        AiProviderKind.OpenAI -> "OpenAI API"
        AiProviderKind.OpenRouter -> "OpenRouter"
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        SettingTitle(
            "Provedor de IA",
            "Escolha processamento local ou uma API configurada por você.",
            providerLabel,
        )
        ChoiceRow(listOf("Ollama local", "OpenAI API", "OpenRouter"), providerLabel) { selected ->
            ai.updateProvider(
                when (selected) {
                    "Ollama local" -> AiProviderKind.Ollama
                    "OpenRouter" -> AiProviderKind.OpenRouter
                    else -> AiProviderKind.OpenAI
                },
            )
        }
        Spacer(Modifier.height(14.dp))
        androidx.compose.material.TextButton(onClick = { advanced = !advanced }) {
            Text(tr("Advanced connection settings", "Configuração avançada da conexão"))
        }
        if (advanced) AiTextField(
            value = ai.configuration.endpoint,
            label = "Endereço do provedor",
            placeholder = "http://localhost:11434",
            tag = "ai-endpoint",
            onValueChange = ai::updateEndpoint,
        )
        Text(if (ai.configuration.provider == AiProviderKind.Ollama)
            tr("Use the name of a model installed in Ollama. No API key required.", "Use o nome de um modelo instalado no Ollama. Não exige chave de API.")
            else tr("Use a model available in your provider account. API usage may be billed separately.", "Use um modelo disponível na sua conta do provedor. O uso da API pode ser cobrado separadamente."),
            style = MaterialTheme.typography.caption)
        Spacer(Modifier.height(10.dp))
        AiTextField(
            value = ai.configuration.model,
            label = "Modelo",
            placeholder = "llama3.2",
            tag = "ai-model",
            onValueChange = ai::updateModel,
        )
        androidx.compose.foundation.layout.Row {
            androidx.compose.material.TextButton(enabled = !ai.loadingModels, onClick = { ai.loadModels(scope) }) {
                Text(if (ai.loadingModels) tr("Loading…", "Carregando…") else tr("Find models", "Buscar modelos"))
            }
            if (ai.models.isNotEmpty()) androidx.compose.foundation.layout.Box {
                androidx.compose.material.TextButton(onClick = { modelsOpen = true }) { Text(tr("Choose model", "Escolher modelo")) }
                androidx.compose.material.DropdownMenu(expanded = modelsOpen, onDismissRequest = { modelsOpen = false }) {
                    ai.models.forEach { model ->
                        androidx.compose.material.DropdownMenuItem(onClick = { ai.updateModel(model); modelsOpen = false }) { Text(model) }
                    }
                }
            }
        }
        if (ai.configuration.provider != AiProviderKind.Ollama) {
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = ai.configuration.apiKey,
                onValueChange = ai::updateApiKey,
                modifier = Modifier.fillMaxWidth().testTag("ai-api-key"),
                label = { Text("API key") },
                placeholder = {
                    Text(if (ai.configuration.provider == AiProviderKind.OpenRouter) "sk-or-v1-…" else "sk-…")
                },
                singleLine = true,
                visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    CredentialVisibilityButton(apiKeyVisible) { apiKeyVisible = !apiKeyVisible }
                },
            )
            Text(
                "Use Salvar chave para guardá-la no cofre do sistema. A edição permanece na memória até salvar.",
                modifier = Modifier.padding(top = 5.dp),
                color = RockyColors.TextMuted,
                style = MaterialTheme.typography.caption,
            )
        }
        Button(onClick = ai::saveConfiguration) { Text("Salvar configuração e chave") }
        if (ai.configuration.provider != AiProviderKind.Ollama) {
            Button(onClick = { ai.updateApiKey(""); ai.saveConfiguration() }) { Text("Apagar chave") }
        }
        if (ai.configuration.provider == AiProviderKind.OpenRouter) {
            Text(
                tr(
                    "openrouter/free selects an available free model. Availability and limits may vary.",
                    "openrouter/free seleciona um modelo gratuito disponível. Disponibilidade e limites podem variar.",
                ),
                modifier = Modifier.padding(top = 5.dp),
                color = RockyColors.TextMuted,
                style = MaterialTheme.typography.caption,
            )
        }
        Text(
            tr("Connection test generates a short response with synthetic data. API usage may be charged.",
                "O teste gera uma resposta curta com dados de exemplo. Pode haver cobrança de uso da API."),
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.caption,
            color = RockyColors.TextMuted,
        )
        Button(
            onClick = { ai.testConnection(scope) },
            modifier = Modifier.padding(top = 14.dp).testTag("ai-test-connection"),
            enabled = !ai.testing && ai.configuration.model.isNotBlank() &&
                (ai.configuration.provider == AiProviderKind.Ollama || ai.configuration.apiKey.isNotBlank()),
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
        SettingSwitch(
            "analisar durante a live",
            ai.automaticAnalysis,
            ai::updateAutomaticAnalysis,
            Modifier.testTag("automatic-analysis"),
        )
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
