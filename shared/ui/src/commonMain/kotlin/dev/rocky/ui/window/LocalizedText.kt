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
    val translation = allTranslations.entries.firstOrNull { text == it.key || text == it.value }
    return when (language) {
        dev.rocky.core.locale.RockyLanguage.English -> translation?.value ?: dynamicEnglish(text)
        dev.rocky.core.locale.RockyLanguage.PortugueseBrazil -> translation?.key ?: text
    }
}

private fun dynamicEnglish(text: String): String = when {
    text.length > 3 && text[0].isDigit() && text.substring(1, 3) == ". " ->
        "${text[0]}. ${allTranslations[text.substring(3)] ?: text.substring(3)}"
    text.endsWith(" mensagens recebidas") -> text.removeSuffix(" mensagens recebidas") + " messages received"
    text.endsWith(" mensagem recebida") -> text.removeSuffix(" mensagem recebida") + " message received"
    text.endsWith(" ideias salvas") -> text.removeSuffix(" ideias salvas") + " saved ideas"
    text.endsWith(" ideia salva") -> text.removeSuffix(" ideia salva") + " saved idea"
    text.endsWith(" notas salvas") -> text.removeSuffix(" notas salvas") + " saved notes"
    text.endsWith(" nota salva") -> text.removeSuffix(" nota salva") + " saved note"
    text.endsWith(" mensagens") -> text.removeSuffix(" mensagens") + " messages"
    text.endsWith(" mensagem") -> text.removeSuffix(" mensagem") + " message"
    text.startsWith("Recebendo o chat de ") -> text.replaceFirst("Recebendo o chat de ", "Receiving chat from ")
    text.startsWith("Analisando ") && text.endsWith(" mensagens…") -> text.replaceFirst("Analisando ", "Analyzing ").replace(" mensagens…", " messages…")
    text.startsWith("Você: ") -> text.replaceFirst("Você: ", "You: ")
    text.startsWith("Fale agora · nível ") -> text.replaceFirst("Fale agora · nível ", "Speak now · level ")
    text.startsWith("O microfone fica ativo e reage quando você chama “") -> text
        .replaceFirst("O microfone fica ativo e reage quando você chama “", "The microphone stays active and reacts when you say “")
    text.startsWith("Fale uma frase; ") -> text
        .replaceFirst("Fale uma frase; ", "Say something; ")
        .replace(" transcreve e responde em áudio.", " transcribes it and answers aloud.")
    text.startsWith("Fale com ") -> text.replaceFirst("Fale com ", "Talk to ")
    text.startsWith("Clique em “Fale com ") -> text
        .replaceFirst("Clique em “Fale com ", "Click “Talk to ")
        .replace("” para gravar e clique novamente para transcrever localmente.", "” to record, then click again to transcribe locally.")
    text.startsWith("Olá, eu sou ") -> text.replaceFirst("Olá, eu sou ", "Hello, I am ").replace(". A voz do chat, em acordes.", ". The voice of chat, in tune.")
    text.startsWith("SUGESTÃO DO ") -> text.replaceFirst("SUGESTÃO DO ", "SUGGESTION FROM ")
    text.contains(" assistindo · ") -> text.replace(" assistindo · ", " watching · ")
    text.contains(" na Twitch") -> text.replace(" na Twitch", " on Twitch")
    text.contains(" no YouTube") -> text.replace(" no YouTube", " on YouTube")
    text.contains(" no Kick") -> text.replace(" no Kick", " on Kick")
    text.startsWith("audiência indisponível · ") -> text.replaceFirst("audiência indisponível", "audience unavailable")
    else -> text
}

private val allTranslations by lazy { settingsTranslations + liveTranslations + releaseTranslations }

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
    "Microfone ouvindo" to "Microphone listening",
    "Teste de conversa" to "Conversation test",
    "Fale agora. A gravação termina automaticamente." to "Speak now. Recording stops automatically.",
    "Prepare o reconhecimento local uma vez. O Rocky instalará o mecanismo e baixará o modelo de voz." to "Set up local recognition once. Rocky will install the engine and download the voice model.",
    "Configurar reconhecimento de voz" to "Set up voice recognition",
    "Preparando…" to "Setting up…",
    "Preparando reconhecimento de voz…" to "Setting up voice recognition…",
    "Reconhecimento de voz pronto" to "Voice recognition ready",
    "Faça o teste de conversa abaixo" to "Run the conversation test below",
    "Configure o reconhecimento de voz para iniciar o teste" to "Set up voice recognition to start the test",
    "Testar conversa por voz" to "Test voice conversation",
    "Ouvindo…" to "Listening…",
    "configurar" to "configure",
    "pronta" to "ready",
    "Microfone" to "Microphone",
    "Entrada padrão" to "Default input",
    "Executável whisper-cli" to "whisper-cli executable",
    "Modelo GGML (.bin)" to "GGML model (.bin)",
    "Escolher" to "Choose",
    "A saída de monitoramento pode entrar na transmissão se o OBS capturar todo o áudio do computador." to "Monitoring audio may enter the stream if OBS captures all computer audio.",
    "Conexão com plataformas" to "Platform connections",
    "Este build não inclui um Client ID. Crie um aplicativo público na Twitch e cole o identificador abaixo." to "This build does not include a client ID. Create a public Twitch application and paste its identifier below.",
    "O Client ID já está configurado. Conecte e autorize o canal que fará a transmissão." to "The client ID is already configured. Connect and authorize the channel that will stream.",
    "Criar Client ID" to "Create client ID",
    "Sobre o Client ID" to "About the client ID",
    "Conecte a Twitch para receber o chat e os dados da sua live." to "Connect Twitch to receive chat and data from your stream.",
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
    "Conecte a Twitch e a IA para acompanhar uma live real. A voz é opcional e pode ser configurada depois." to "Connect Twitch and AI to follow a real stream. Voice is optional and can be configured later.",
    "Conecte a Twitch ou a Kick e configure a IA para começar por texto. Ative a voz quando quiser." to "Connect Twitch or Kick and configure AI to start with text. Enable voice whenever you want.",
    "Conecte sua Twitch nas configurações para acompanhar uma live." to "Connect your Twitch in settings to follow a stream.",
    "Conecte sua Twitch" to "Connect your Twitch",
    "Conecte sua plataforma" to "Connect your platform",
    "Conexão da Twitch encerrada" to "Twitch connection ended",
    "Conecte a Twitch, a IA e a voz para usar o Rocky durante a live." to "Connect Twitch, AI, and voice to use Rocky during your stream.",
    "Informe o Client ID de um aplicativo público e autorize o canal que fará a transmissão." to "Enter the Client ID of a public application and authorize the channel that will stream.",
    "Conecte sua Twitch" to "Connect your Twitch",
    "Configurar Twitch" to "Set up Twitch",
    "Revisar Twitch" to "Review Twitch",
    "Configurar plataforma" to "Set up platform",
    "Revisar plataforma" to "Review platform",
    "Configure a IA" to "Configure AI",
    "Escolha Ollama local, OpenAI API ou OpenRouter e use “Testar conexão” antes da live." to "Choose local Ollama, the OpenAI API, or OpenRouter and use “Test connection” before the stream.",
    "Configurar IA" to "Configure AI",
    "Revisar IA" to "Review AI",
    "Teste a voz" to "Test voice",
    "Escolha uma voz do sistema e confirme o áudio com “Testar voz”. O microfone é opcional." to "Choose a system voice and confirm the audio with “Test voice”. The microphone is optional.",
    "Configure o whisper.cpp e o microfone, depois confirme o áudio com “Testar voz”." to "Configure whisper.cpp and the microphone, then confirm audio with “Test voice”.",
    "Configurar voz" to "Configure voice",
    "Revisar voz" to "Review voice",
    "pendente" to "pending",
    "pronto" to "ready",
    "opcional" to "optional",
    "Concluir configuração" to "Finish setup",
    "Rocky não exige uma conta própria. As notas ficam no computador. Segredos salvos usam o cofre do sistema; tokens das plataformas ficam na memória." to "Rocky requires no account of its own. Notes stay on your computer. Saved secrets use the system vault; platform tokens stay in memory.",
    "Rocky não exige uma conta própria. As notas ficam no computador e as credenciais sensíveis não são salvas." to "Rocky requires no account of its own. Notes stay on your computer and sensitive credentials are not saved.",
)

private val liveTranslations = mapOf(
    "a voz do chat, em acordes" to "the voice of chat, in tune",
    "Abrir configurações" to "Open settings",
    "Fixar janela" to "Pin window",
    "Desafixar janela" to "Unpin window",
    "Modo compacto" to "Compact mode",
    "Modo expandido" to "Expanded mode",
    "PARADO" to "STOPPED",
    "OUVINDO" to "LISTENING",
    "CHAT ATIVO" to "CHAT ACTIVE",
    "MICROFONE ON" to "MIC ON",
    "ENCERRADO" to "ENDED",
    "ERRO" to "ERROR",
    "Retomar" to "Resume",
    "TOCANDO AGORA" to "PLAYING NOW",
    "Salvar como nota" to "Save as note",
    "Nota salva" to "Note saved",
    "Próxima" to "Next",
    "Silenciar" to "Mute",
    "Conversa" to "Conversation",
    "Superchats" to "Super Chats",
    "Notas" to "Notes",
    "Ideias" to "Ideas",
    "Pulso" to "Pulse",
    "CHAT AO VIVO" to "LIVE CHAT",
    "VOCÊ" to "YOU",
    "Aguardando mensagens do chat…" to "Waiting for chat messages…",
    "Estou acompanhando as mensagens para encontrar algo útil." to "I am following the messages to find something useful.",
    "Estou recebendo o chat real. Posso analisar agora ou aguardar o próximo lote automático." to "I am receiving real chat. I can analyze now or wait for the next automatic batch.",
    "Estou analisando o chat para encontrar uma resposta ou ideia útil." to "I am analyzing chat to find a useful answer or idea.",
    "Analisar agora" to "Analyze now",
    "Analisando…" to "Analyzing…",
    "Configure e teste um provedor na aba IA para gerar sugestões." to "Configure and test a provider in the AI tab to generate suggestions.",
    "Super Chats ainda não estão conectados." to "Super Chats are not connected yet.",
    "A conexão atual da Twitch recebe somente mensagens do chat." to "The current Twitch connection receives chat messages only.",
    "Notas locais" to "Local notes",
    "Exportar .md" to "Export .md",
    "Markdown exportado." to "Markdown exported.",
    "As sugestões salvas durante a live aparecerão aqui." to "Suggestions saved during the stream will appear here.",
    "Ideias da live" to "Stream ideas",
    "A geração automática de ideias ainda não está disponível em sessões reais." to "Automatic idea generation is not available in real sessions yet.",
    "Editar nota" to "Edit note",
    "Conteúdo" to "Content",
    "Salvar" to "Save",
    "Cancelar" to "Cancel",
    "Excluir nota?" to "Delete note?",
    "Esta ação remove a nota deste computador." to "This removes the note from this computer.",
    "Excluir" to "Delete",
    "Nota atualizada." to "Note updated.",
    "Nota excluída." to "Note deleted.",
    "Nota salva localmente." to "Note saved locally.",
    "Não foi possível carregar as notas locais." to "Could not load local notes.",
    "Não foi possível salvar a alteração." to "Could not save the change.",
    "CONEXÃO REAL · TWITCH" to "REAL CONNECTION · TWITCH",
    "CHAT REAL DA TWITCH" to "REAL TWITCH CHAT",
    "CHAT REAL DA LIVE" to "REAL STREAM CHAT",
    "CONECTANDO" to "CONNECTING",
    "RECONECTANDO" to "RECONNECTING",
    "Chat real da Twitch" to "Real Twitch chat",
    "Conectando" to "Connecting",
    "Reconectando" to "Reconnecting",
    "Não conectada" to "Not connected",
    "Conectada" to "Connected",
    "Conectado" to "Connected",
    "Falha na conexão" to "Connection failed",
    "Aguardando autorização" to "Waiting for authorization",
    "Iniciando autenticação" to "Starting authentication",
    "Autenticando" to "Authenticating",
    "Conectando ao chat" to "Connecting to chat",
    "Confirme o código no navegador." to "Confirm the code in your browser.",
    "Informe o Client ID da Twitch." to "Enter the Twitch Client ID.",
    "OUVINDO O CHAT" to "LISTENING TO CHAT",
    "Rocky está falando…" to "Rocky is speaking…",
    "Rocky está transcrevendo" to "Rocky is transcribing",
    "Microfone ativo" to "Microphone active",
    "Microfone ativo · clique para concluir" to "Microphone active · click to finish",
    "Ouvinte ativo" to "Listener active",
    "Ouvinte ativo · fale sua pergunta" to "Listener active · ask your question",
    "Ouvinte ativo · diga “Rocky” e faça sua pergunta" to "Listener active · say “Rocky” and ask your question",
    "Não encontrei mensagens nos últimos dois minutos." to "I found no messages from the last two minutes.",
    "Ativar ouvinte do Rocky" to "Enable Rocky listener",
    "fale sua pergunta · envio automático" to "ask your question · sent automatically",
    "diga “Rocky” e faça sua pergunta" to "say “Rocky” and ask your question",
    "clique para ativar" to "click to enable",
    "Transcrevendo localmente…" to "Transcribing locally…",
    "Ativando o microfone…" to "Starting microphone…",
    "Captura cancelada" to "Capture canceled",
    "Leitura concluída" to "Playback finished",
    "Leitura interrompida" to "Playback stopped",
    "Não foi possível acessar o microfone" to "Could not access the microphone",
    "Não foi possível consultar os dispositivos de áudio" to "Could not load audio devices",
    "Não foi possível transcrever a fala" to "Could not transcribe speech",
    "Não foi possível usar a voz do sistema" to "Could not use the system voice",
    "Não foi possível gerar a sugestão" to "Could not generate a suggestion",
    "Não foi possível testar a conexão" to "Could not test the connection",
    "Sugestão gerada" to "Suggestion generated",
    "Testando conexão…" to "Testing connection…",
    "Nenhuma sugestão relevante agora" to "No relevant suggestion right now",
    "Provedor de IA indisponível" to "AI provider unavailable",
    "Configure o provedor de IA antes de analisar" to "Configure the AI provider before analyzing",
    "Configure o whisper.cpp na aba Voz antes de usar o microfone" to "Configure whisper.cpp in the Voice tab before using the microphone",
    "clique para começar" to "click to start",
    "clique novamente para concluir" to "click again to finish",
    "SUGESTÃO" to "SUGGESTION",
    "SUGESTÃO IA" to "AI SUGGESTION",
    "CLIPE" to "CLIP",
    "PENDÊNCIA" to "PENDING",
    "CONTEÚDO" to "CONTENT",
    "INTERAÇÃO" to "INTERACTION",
    "CONVITE" to "INVITATION",
    "Prometeu mostrar o raio-x do repositório antes de encerrar." to "You promised to show the repository overview before ending.",
    "Chat reagiu forte à parte de deploy — bom material para um corte." to "Chat reacted strongly to the deployment section — good material for a clip.",
    "Três pedidos de compatibilidade com Next.js. Ninguém respondeu ainda." to "Three requests for Next.js compatibility. No one has answered yet.",
    "Série curta respondendo as 5 dúvidas mais repetidas do chat." to "Short series answering the five most repeated chat questions.",
    "Enquete ao vivo: deploy manual ou CI? O chat está dividido." to "Live poll: manual deployment or CI? Chat is divided.",
    "Convidar a Ju para a próxima live — ela respondeu metade do chat." to "Invite Ju to the next stream — she answered half the chat.",
    "Essa aula salvou minha semana, obrigada!" to "This class saved my week, thank you!",
    "Vale a pena usar isso em produção hoje?" to "Is this worth using in production today?",
    "Quantas vagas ainda tem na turma?" to "How many seats are left in the class?",
)

private val releaseTranslations = mapOf(
    "Salvamento desfeito." to "Save undone.",
    "Este item já foi salvo." to "This item has already been saved.",
    "Ideia salva localmente." to "Idea saved locally.",
    "Modelos carregados" to "Models loaded",
    "Nenhum modelo disponível. Verifique o provedor." to "No models available. Check the provider.",
    "Não foi possível listar modelos. Verifique a conexão e a chave." to "Could not list models. Check the connection and key.",

    "Aguardando a conexão da Twitch. As mensagens anteriores estão preservadas." to "Waiting for Twitch to connect. Previous messages are preserved.",
    "Conecte Twitch, Kick ou YouTube nas configurações para acompanhar uma live." to "Connect Twitch, Kick or YouTube in settings to follow a stream.",
    "Aguardando a conexão da plataforma. As mensagens anteriores estão preservadas." to "Waiting for the platform to connect. Previous messages are preserved.",
    "Aguardando mensagens do chat nos últimos dois minutos." to "Waiting for chat messages from the last two minutes.",
    "Dados" to "Data",
    "Dados e privacidade" to "Data and privacy",
    "Teste a voz (opcional)" to "Test voice (optional)",
    "Conecte a Twitch e a IA para começar por texto. Ative a voz quando quiser." to "Connect Twitch and AI to start with text. Enable voice whenever you want.",
    "Rocky não exige uma conta própria. As notas ficam no computador. Chaves de IA salvas usam o cofre do sistema; tokens Twitch ficam na memória." to "Rocky requires no account. Notes stay on your computer. Saved AI keys use system credential storage; Twitch tokens remain in memory.",
    "Use Salvar chave para guardá-la no cofre do sistema. A edição permanece na memória até salvar." to "Save configuration and key to keep it in system credential storage. Unsaved edits remain in memory.",
    "Salvar configuração e chave" to "Save configuration and key",
    "Apagar chave" to "Delete key",
    "Configuração salva no dispositivo" to "Configuration saved on this device",
    "Não foi possível salvar no cofre. Desbloqueie o cofre e tente novamente." to "Could not save to system credential storage. Unlock it and try again.",
    "Tentar carregar novamente" to "Retry loading",
    "Não foi possível exportar. Escolha uma pasta disponível e tente novamente." to "Export failed. Choose an available folder and try again.",
    "Não foi possível abrir o banco de notas. Verifique a pasta de dados e tente carregar novamente. O arquivo original foi preservado." to "Could not open the notes database. Check the data folder and retry. The original file was preserved.",
    "Nenhuma mensagem recebida nos últimos dois minutos" to "No messages received in the last two minutes",
    "Cancelar preparação" to "Cancel setup",
    "Preparação cancelada; você pode tentar novamente." to "Setup cancelled; you can try again.",
    "Todas as notas foram apagadas." to "All notes were deleted.",
    "Rocky não exige uma conta própria." to "Rocky requires no account.",
    "Abrir pasta de dados" to "Open data folder",
    "Pasta de dados aberta." to "Data folder opened.",
    "Exportar notas antes de apagar" to "Export notes before deleting",
    "Apagar notas" to "Delete notes",
    "Redefinir configurações" to "Reset settings",
    "Remover modelo de voz" to "Remove voice model",
    "Confirmar" to "Confirm",
    "Configurações e chave removidas. Reabra o Rocky." to "Settings and key removed. Reopen Rocky.",
    "Modelo gerenciado removido. Modelos externos foram preservados." to "Managed model removed. External models were preserved.",
    "Esta ação apaga todas as notas e suas fontes. Exporte antes de continuar." to "This deletes all notes and their evidence. Export before continuing.",
    "A sessão será interrompida. Reabra o Rocky após concluir. Notas e exportações serão preservadas." to "The session will stop. Reopen Rocky afterwards. Notes and exports will be preserved.",
    "O ouvinte será interrompido e apenas o modelo gerenciado será removido. Notas e modelos externos serão preservados." to "Listening will stop and only the managed model will be removed. Notes and external models will be preserved.",
    "Não foi possível concluir. Verifique o acesso ao cofre ou à pasta de dados e tente novamente." to "Could not finish. Check access to system credential storage or the data folder and retry.",
    "Rocky " to "Rocky ",
    "Notas e fontes ficam no SQLite local. O chat é temporário. APIs de IA recebem as mensagens selecionadas; Ollama pode processá-las localmente." to "Notes and evidence stay in local SQLite storage. Chat is temporary. AI APIs receive selected messages; Ollama can process them locally.",
    "Chaves salvas usam Keychain no macOS ou DPAPI do usuário no Windows. Tokens Twitch ficam na memória." to "Saved keys use macOS Keychain or user-scoped Windows DPAPI. Twitch tokens remain in memory.",
    "A exclusão lógica não apaga cópias já exportadas, backups do sistema ou dados retidos pelo provedor de IA." to "Logical deletion does not remove exports, system backups, or data retained by the AI provider.",
    "Microfone capturando · fale agora" to "Microphone capturing · speak now",
    "Cofre indisponível. A chave está apenas na memória; salve novamente quando o cofre estiver disponível." to "System credential storage unavailable. The key is in memory only; save it when storage becomes available.",
)
