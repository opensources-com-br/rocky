package dev.rocky.ui.window

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
    val providers = linkedMapOf(
        "Ollama local" to AiProviderKind.Ollama,
        "OpenAI API" to AiProviderKind.OpenAI,
        "Anthropic API" to AiProviderKind.Anthropic,
        "Google Gemini API" to AiProviderKind.Gemini,
        "xAI Grok API" to AiProviderKind.Grok,
        "OpenRouter" to AiProviderKind.OpenRouter,
    )
    val providerLabel = providers.entries.first { it.value == ai.configuration.provider }.key
    val localProvider = ai.configuration.provider == AiProviderKind.Ollama

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        SettingsPreferenceGroup(tr("Connection", "Conexão")) {
            SettingsPreferenceRow(tr("AI provider", "Provedor de IA")) {
                SettingsPreferenceMenu(providers.keys.toList(), providerLabel, "ai-provider-menu") {
                    ai.updateProvider(providers.getValue(it))
                    apiKeyVisible = false
                }
            }
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            Column(modifier = Modifier.padding(16.dp)) {
                AiTextField(
                    value = ai.configuration.model,
                    label = tr("Model", "Modelo"),
                    placeholder = "llama3.2",
                    tag = "ai-model",
                    onValueChange = ai::updateModel,
                )
                Text(
                    if (localProvider)
                        tr("Use a model installed in Ollama. No API key required.", "Use um modelo instalado no Ollama. Não exige chave de API.")
                    else tr("Use a model available in your provider account. API usage may be billed separately.",
                        "Use um modelo disponível na sua conta do provedor. O uso da API pode ser cobrado separadamente."),
                    modifier = Modifier.padding(top = 8.dp),
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(enabled = !ai.loadingModels, onClick = { ai.loadModels(scope) }) {
                        Text(if (ai.loadingModels) tr("Loading…", "Carregando…") else tr("Find models", "Buscar modelos"))
                    }
                    if (ai.models.isNotEmpty()) Box {
                        TextButton(onClick = { modelsOpen = true }) { Text(tr("Choose model", "Escolher modelo")) }
                        DropdownMenu(expanded = modelsOpen, onDismissRequest = { modelsOpen = false }) {
                            ai.models.forEach { model ->
                                DropdownMenuItem(onClick = { ai.updateModel(model); modelsOpen = false }) { Text(model) }
                            }
                        }
                    }
                }
            }
            Divider(color = RockyColors.Border, modifier = Modifier.padding(horizontal = 16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                TextButton(onClick = { advanced = !advanced }, modifier = Modifier.testTag("ai-advanced-connection")) {
                    Text(tr("Advanced connection settings", "Configuração avançada da conexão"))
                }
                if (advanced) {
                    AiTextField(ai.configuration.endpoint, tr("Provider address", "Endereço do provedor"),
                        "http://localhost:11434", "ai-endpoint", ai::updateEndpoint)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        if (!localProvider) SettingsPreferenceGroup(tr("Credentials", "Credenciais")) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = ai.configuration.apiKey,
                    onValueChange = ai::updateApiKey,
                    modifier = Modifier.fillMaxWidth().testTag("ai-api-key"),
                    label = { Text("API key") },
                    placeholder = {
                        Text(when (ai.configuration.provider) {
                            AiProviderKind.OpenRouter -> "sk-or-v1-…"
                            AiProviderKind.Anthropic -> "sk-ant-…"
                            AiProviderKind.Gemini -> "AIza…"
                            AiProviderKind.Grok -> "xai-…"
                            else -> "sk-…"
                        })
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.body2,
                    shape = RoundedCornerShape(8.dp),
                    colors = aiFieldColors(),
                    visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { CredentialVisibilityButton(apiKeyVisible) { apiKeyVisible = !apiKeyVisible } },
                )
                Text(
                    tr("Save to store the key in the system vault. Edits stay in memory until saved.",
                        "Salve para guardar a chave no cofre do sistema. A edição permanece na memória até salvar."),
                    modifier = Modifier.padding(top = 8.dp),
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
                )
                if (ai.configuration.provider == AiProviderKind.OpenRouter) {
                    Text(
                        tr(
                            "openrouter/free selects an available free model. Availability and limits may vary.",
                            "openrouter/free seleciona um modelo gratuito disponível. Disponibilidade e limites podem variar.",
                        ),
                        modifier = Modifier.padding(top = 5.dp),
                        color = RockyColors.TextSecondary,
                        style = MaterialTheme.typography.caption,
                    )
                }
                if (ai.configuration.provider == AiProviderKind.Anthropic) {
                    Text(
                        tr(
                            "Claude API keys are separate from a Claude subscription.",
                            "Chaves da API Claude são separadas de uma assinatura Claude.",
                        ),
                        modifier = Modifier.padding(top = 5.dp),
                        color = RockyColors.TextSecondary,
                        style = MaterialTheme.typography.caption,
                    )
                }
                if (ai.configuration.provider == AiProviderKind.Gemini) {
                    Text(
                        tr(
                            "Create a Gemini API key in Google AI Studio; it is separate from a Gemini app subscription.",
                            "Crie uma chave da API Gemini no Google AI Studio; ela é separada de uma assinatura do app Gemini.",
                        ),
                        modifier = Modifier.padding(top = 5.dp),
                        color = RockyColors.TextSecondary,
                        style = MaterialTheme.typography.caption,
                    )
                }
                if (ai.configuration.provider == AiProviderKind.Grok) {
                    Text(
                        tr(
                            "Create an xAI API key in the xAI Console; it is separate from a Grok subscription.",
                            "Crie uma chave da API xAI no Console xAI; ela é separada de uma assinatura Grok.",
                        ),
                        modifier = Modifier.padding(top = 5.dp),
                        color = RockyColors.TextSecondary,
                        style = MaterialTheme.typography.caption,
                    )
                }

            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            OutlinedButton(onClick = ai::saveConfiguration) { Text(tr("Save configuration and key", "Salvar configuração e chave")) }
            if (!localProvider) {
                TextButton(
                    onClick = { ai.updateApiKey(""); ai.saveConfiguration(); apiKeyVisible = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = RockyColors.WindowClose),
                ) { Text(tr("Delete key", "Apagar chave")) }
            }
        }
        SettingsPreferenceGroup(tr("Verification", "Verificação")) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    tr("Connection test generates a short response with synthetic data. API usage may be charged.",
                        "O teste gera uma resposta curta com dados de exemplo. Pode haver cobrança de uso da API."),
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
                )
                Button(
                    onClick = { ai.testConnection(scope) },
                    modifier = Modifier.testTag("ai-test-connection"),
                    enabled = !ai.testing && ai.configuration.model.isNotBlank() &&
                        (localProvider || ai.configuration.apiKey.isNotBlank()),
                    colors = ButtonDefaults.buttonColors(backgroundColor = RockyColors.Accent, contentColor = Color.Black),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                    shape = RoundedCornerShape(8.dp),
                ) { Text(if (ai.testing) tr("Testing…", "Testando…") else tr("Test connection", "Testar conexão")) }
                ai.status?.let { status ->
                    Text(status, modifier = Modifier.testTag("ai-status"), color = RockyColors.TextSecondary,
                        style = MaterialTheme.typography.caption)
                }
            }
        }
        ContextSettings(ai)
        SettingsPreferenceGroup(tr("Automatic suggestions", "Sugestões automáticas")) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingSwitch(
                    tr("Analyze during the live stream", "analisar durante a live"),
                    ai.automaticAnalysis, ai::updateAutomaticAnalysis, Modifier.testTag("automatic-analysis"),
                )
                Text(
                    tr("Analyzes chat in batches of three messages. With an API, selected messages are sent to your provider.",
                        "Analisa o chat em lotes de três mensagens. Ao usar uma API, as mensagens selecionadas são enviadas ao provedor."),
                    modifier = Modifier.padding(top = 8.dp),
                    color = RockyColors.TextSecondary,
                    style = MaterialTheme.typography.caption,
                )
            }
        }
    }
}

@Composable
private fun aiFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    textColor = RockyColors.TextPrimary,
    backgroundColor = RockyColors.Surface,
    focusedBorderColor = RockyColors.Accent,
    unfocusedBorderColor = RockyColors.Border,
    cursorColor = RockyColors.Accent,
)

@Composable
private fun AiTextField(value: String, label: String, placeholder: String, tag: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().testTag(tag),
        label = { Text(label) }, placeholder = { Text(placeholder) },
        singleLine = true, textStyle = MaterialTheme.typography.body2,
        shape = RoundedCornerShape(8.dp), colors = aiFieldColors(),
    )
}
