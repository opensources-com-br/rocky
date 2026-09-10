package dev.rocky.ui.window

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Composable
internal fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) = MaterialText(
    text = localize(text),
    modifier = modifier,
    color = color,
    fontSize = fontSize,
    fontStyle = fontStyle,
    fontWeight = fontWeight,
    fontFamily = fontFamily,
    letterSpacing = letterSpacing,
    textDecoration = textDecoration,
    textAlign = textAlign,
    lineHeight = lineHeight,
    overflow = overflow,
    softWrap = softWrap,
    maxLines = maxLines,
    onTextLayout = onTextLayout,
    style = style,
)

@Composable
private fun localize(text: String): String {
    val language = LocalRockyLanguage.current
    val translation = settingsTranslations.entries.firstOrNull { text == it.key || text == it.value }
    return when (language) {
        dev.rocky.core.locale.RockyLanguage.English -> translation?.value ?: text
        dev.rocky.core.locale.RockyLanguage.PortugueseBrazil -> translation?.key ?: text
    }
}

private val settingsTranslations = mapOf(
    "Configurações" to "Settings",
    "concluir" to "done",
    "Agente" to "Agent",
    "Voz" to "Voice",
    "Plataformas" to "Platforms",
    "Nome do agente" to "Agent name",
    "Como o chat e você chamam o assistente." to "How you and the chat address the assistant.",
    "Tom de voz" to "Tone of voice",
    "Define como o agente formula as intervenções." to "How the agent phrases its interventions.",
    "Direto" to "Direct",
    "Animado" to "Energetic",
    "Analítico" to "Analytical",
    "Irônico" to "Ironic",
    "Frequência de fala" to "Speaking frequency",
    "Quantas vezes por 10 minutos ele pode intervir." to "How many times it may intervene every 10 minutes.",
    "Interrupção automática" to "Automatic interruption",
    "em breve" to "coming soon",
    "Provedor de IA" to "AI provider",
    "Escolha processamento local ou uma API configurada por você." to "Choose local processing or an API you configure.",
    "Ollama local" to "Local Ollama",
    "Endereço do provedor" to "Provider address",
    "Modelo" to "Model",
    "A chave fica apenas na memória e será apagada ao fechar o Rocky." to "The key remains in memory and is cleared when Rocky closes.",
    "Testar conexão" to "Test connection",
    "Testando…" to "Testing…",
    "Sugestões automáticas" to "Automatic suggestions",
    "Analisa o chat em lotes de três mensagens para evitar uma chamada por comentário." to "Analyzes chat in batches of three messages to avoid one request per comment.",
    "analisar durante a live" to "analyze during the stream",
    "ativo" to "active",
    "inativo" to "inactive",
    "Ao usar uma API, as mensagens selecionadas são enviadas ao provedor configurado." to "When using an API, selected messages are sent to the configured provider.",
    "Leitura das sugestões" to "Read suggestions aloud",
    "Usa uma voz instalada no sistema e a saída de áudio padrão." to "Uses a system voice and the default audio output.",
    "ler novas sugestões" to "read new suggestions",
    "falando" to "speaking",
    "Voz do sistema" to "System voice",
    "Voz padrão" to "Default voice",
    "Velocidade" to "Speed",
    "Ritmo da fala." to "Speaking pace.",
    "Volume" to "Volume",
    "Aplicado pelo Windows; no macOS, ajuste a saída padrão do sistema." to "Applied by Windows; on macOS, adjust the default system output.",
    "Testar voz" to "Test voice",
    "Parar voz" to "Stop voice",
    "Entrada do streamer" to "Streamer input",
    "configurar" to "configure",
    "pronta" to "ready",
    "Microfone" to "Microphone",
    "Entrada padrão" to "Default input",
    "Executável whisper-cli" to "whisper-cli executable",
    "Modelo GGML (.bin)" to "GGML model (.bin)",
    "Escolher" to "Choose",
    "A saída de monitoramento pode entrar na transmissão se o OBS capturar todo o áudio do computador." to "Monitoring audio may enter the stream if OBS captures all computer audio.",
    "Conexão com plataformas" to "Platform connections",
    "A demonstração funciona sem conta. Conecte a Twitch para receber um chat real." to "The demo works without an account. Connect Twitch to receive real chat.",
    "1. Crie um aplicativo do tipo Public na Twitch. 2. Copie o Client ID. 3. Conecte e autorize o canal da live." to "1. Create a Public Twitch application. 2. Copy its Client ID. 3. Connect and authorize the stream channel.",
    "O Client ID identifica seu aplicativo público da Twitch. Tokens ficam apenas na memória e são apagados ao desconectar ou fechar o Rocky." to "The Client ID identifies your public Twitch application. Tokens stay in memory and are cleared when you disconnect or close Rocky.",
    "Como criar o Client ID" to "How to create a Client ID",
    "Cole o Client ID do seu aplicativo" to "Paste your application's Client ID",
    "Conectar Twitch" to "Connect Twitch",
    "Desconectar" to "Disconnect",
    "Abrir Twitch" to "Open Twitch",
    "Use este código para autorizar o Rocky na Twitch." to "Use this code to authorize Rocky on Twitch.",
    "Kick" to "Kick",
    "YouTube" to "YouTube",
    "Facebook" to "Facebook",
    "Em breve" to "Coming soon",
    "Configure o Rocky" to "Set up Rocky",
    "Complete os três passos para acompanhar uma live real. Você pode voltar a este guia até concluir." to "Complete all three steps to follow a real stream. You can return to this guide until you finish.",
    "Informe o Client ID de um aplicativo público e autorize o canal que fará a transmissão." to "Enter the Client ID of a public application and authorize the channel that will stream.",
    "Conecte sua Twitch" to "Connect your Twitch",
    "Configurar Twitch" to "Set up Twitch",
    "Revisar Twitch" to "Review Twitch",
    "Configure a IA" to "Configure AI",
    "Escolha Ollama local ou OpenAI API e use “Testar conexão” antes da live." to "Choose local Ollama or the OpenAI API and use “Test connection” before the stream.",
    "Configurar IA" to "Configure AI",
    "Revisar IA" to "Review AI",
    "Teste a voz" to "Test voice",
    "Escolha uma voz do sistema e confirme o áudio com “Testar voz”. O microfone é opcional." to "Choose a system voice and confirm the audio with “Test voice”. The microphone is optional.",
    "Configurar voz" to "Configure voice",
    "Revisar voz" to "Review voice",
    "pendente" to "pending",
    "pronto" to "ready",
    "Concluir configuração" to "Finish setup",
    "Usar demonstração" to "Use demo",
    "Rocky não exige uma conta própria. As notas ficam no computador e as credenciais sensíveis não são salvas." to "Rocky requires no account of its own. Notes stay on your computer and sensitive credentials are not saved.",
)
