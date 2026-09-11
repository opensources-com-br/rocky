package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.dp
import dev.rocky.core.live.LiveIdea
import dev.rocky.core.live.LiveNote
import dev.rocky.core.live.LiveSessionStatus
import dev.rocky.core.locale.RockyLanguage
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiSuggestionClient
import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.agent.AgentTone
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.notes.NoteRepository
import dev.rocky.core.twitch.TwitchAccount
import dev.rocky.core.twitch.TwitchChatClient
import dev.rocky.core.twitch.TwitchConnectionEvent
import dev.rocky.core.twitch.TwitchConnectionListener
import java.nio.file.Files
import java.nio.file.Path
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.junit.Rule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RockyVisualCaptureTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun captureMainInterface() {
        when (System.getenv("ROCKY_CAPTURE_STATE")) {
            "settings-ai" -> {
                render(settingsOpen = true, settingsSection = SettingsSection.Ai)
                capture("implementation-settings-ai.png")
                return
            }
            "settings-openrouter" -> {
                render(settingsOpen = true, settingsSection = SettingsSection.Ai)
                rule.onNodeWithText("OpenRouter").performClick()
                capture("implementation-settings-openrouter.png")
                return
            }
            "settings-platforms" -> {
                render(settingsOpen = true, settingsSection = SettingsSection.Platforms)
                capture("implementation-settings-platforms.png")
                return
            }
        }

        render()
        rule.onNodeWithText("Iniciar").performClick()
        waitForSuggestion()
        rule.onNodeWithText("Também queria saber o valor.").assertExists()
        capture("implementation-main.png")

        val mainSections = mapOf(
            MainSection.Support to "ju.lia",
            MainSection.Notes to "Notas locais",
            MainSection.Ideas to "Série curta respondendo as 5 dúvidas mais repetidas do chat.",
            MainSection.Pulse to "820 assistindo · 26 msg/min",
        )
        mainSections.forEach { (section, visibleText) ->
            render(mainSection = section)
            rule.onNodeWithText(visibleText).assertExists()
            capture("implementation-${section.name.lowercase()}.png")
        }

        val settingsSections = mapOf(
            SettingsSection.Agent to "Nome do agente",
            SettingsSection.Ai to "Provedor de IA",
            SettingsSection.Voice to "Velocidade",
            SettingsSection.Platforms to "Conexão com plataformas",
        )
        settingsSections.forEach { (section, visibleText) ->
            render(settingsOpen = true, settingsSection = section)
            rule.onNodeWithText(visibleText).assertExists()
            capture("implementation-settings-${section.name.lowercase()}.png")
        }
    }

    @Test
    fun saveSimulatedSuggestionAsNote() {
        render()
        rule.onNodeWithText("Iniciar").performClick()
        waitForSuggestion()

        rule.onNodeWithText("Salvar como nota").performClick()

        rule.onNodeWithText("Nota salva").assertExists()
        rule.onNodeWithText("SUGESTÃO").assertExists()
    }

    @Test
    fun startsEndsAndRestartsDemonstration() {
        render()

        rule.onNodeWithText("MODO DEMONSTRAÇÃO").assertExists()
        rule.onNodeWithText("Sem conexão com uma live real").assertExists()
        rule.onNodeWithText("PARADO").assertExists()
        rule.onNodeWithText("Iniciar").performClick()
        rule.onNodeWithText("OUVINDO").assertExists()

        rule.onNodeWithText("Encerrar").performClick()
        rule.onNodeWithText("ENCERRADO").assertExists()

        rule.onNodeWithText("Reiniciar").performClick()
        rule.onNodeWithText("OUVINDO").assertExists()
        rule.onNodeWithText("Encerrar").assertExists()
    }

    @Test
    fun connectsAndDisplaysRealTwitchChat() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient()
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Platforms,
            twitchChatClient = twitch,
            twitchClientId = "client-id",
            aiSuggestionClient = ai,
        )

        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.AuthorizationRequired("ABCD-1234", "https://example.test"))
        }
        rule.onNodeWithText("ABCD-1234").assertExists()

        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emit(
                TwitchConnectionEvent.MessageReceived(
                    ChatMessage("message-1", "viewer", "Mensagem real", StreamPlatform.Twitch),
                ),
            )
        }
        rule.onNodeWithText("concluir").performClick()

        rule.onNodeWithText("CONEXÃO REAL · TWITCH").assertExists()
        rule.onNodeWithText("Mensagem real").assertExists()
        assertTrue(rule.onAllNodesWithText("1.221").fetchSemanticsNodes().isEmpty())
        rule.onNodeWithText("Superchats").performClick()
        rule.onNodeWithText("Super Chats ainda não estão conectados.").assertExists()
        assertTrue(rule.onAllNodesWithText("ju.lia").fetchSemanticsNodes().isEmpty())
        rule.onNodeWithText("Ideias").performClick()
        rule.onNodeWithText("A geração automática de ideias ainda não está disponível em sessões reais.").assertExists()
        assertTrue(
            rule.onAllNodesWithText("Série curta respondendo as 5 dúvidas mais repetidas do chat.")
                .fetchSemanticsNodes().isEmpty(),
        )
        rule.onNodeWithText("Pulso").performClick()
        rule.onNodeWithText("1 mensagem recebida").assertExists()
        rule.onNodeWithText("Conversa").performClick()
        rule.onNodeWithTag("streamer-text-request").performScrollTo()
            .performTextReplacement("Quais dúvidas responder?")
        rule.onNodeWithTag("send-streamer-text-request").performScrollTo().performClick()
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodesWithText("O chat quer saber o preço.").fetchSemanticsNodes().isNotEmpty()
        }
        rule.runOnIdle {
            assertEquals("Quais dúvidas responder?", ai.lastRequest)
            twitch.emit(TwitchConnectionEvent.PhaseChanged(dev.rocky.core.twitch.TwitchConnectionPhase.Reconnecting))
        }
        rule.onNodeWithText("O chat quer saber o preço.").assertExists()
        capture("implementation-real-session.png")
    }

    @Test
    fun analyzesAutomaticBatchesWithoutUsingNext() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient()
        prepareAutomaticSession(twitch, ai)

        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emitMessages(3)
        }
        rule.onNodeWithText("concluir").performClick()
        rule.waitUntil(timeoutMillis = 3_000) {
            rule.onAllNodesWithText("O chat quer saber o preço.").fetchSemanticsNodes().isNotEmpty()
        }

        assertEquals(1, ai.requests)
        rule.runOnIdle { twitch.emitMessages(3, startAt = 3) }
        rule.mainClock.advanceTimeBy(200_100)
        rule.waitUntil(timeoutMillis = 3_000) { ai.requests == 2 }
        assertEquals(2, ai.requests)
    }

    @Test
    fun retriesAnAutomaticBatchAfterAProviderFailure() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient().apply { failuresRemaining = 1 }
        prepareAutomaticSession(twitch, ai)

        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emitMessages(3)
        }
        rule.onNodeWithText("concluir").performClick()
        rule.waitUntil(timeoutMillis = 3_000) { ai.requests == 1 }
        rule.mainClock.advanceTimeBy(200_100)
        rule.waitUntil(timeoutMillis = 3_000) { ai.requests == 2 }

        rule.onNodeWithText("O chat quer saber o preço.").assertExists()
    }

    @Test
    fun sendsTypedStreamerRequest() {
        var request: String? = null
        rule.setContent {
            Box(Modifier.size(700.dp, 400.dp)) {
                StreamerTextRequest(enabled = true, onSend = { request = it })
            }
        }

        rule.onNodeWithTag("streamer-text-request")
            .performTextReplacement("Quais são as dúvidas sobre preço?")
        rule.onNodeWithTag("send-streamer-text-request").performClick()

        rule.runOnIdle { assertEquals("Quais são as dúvidas sobre preço?", request) }
    }

    @Test
    fun configuresOpenRouterFromAiSettings() {
        render(settingsOpen = true, settingsSection = SettingsSection.Ai)

        rule.onNodeWithText("OpenRouter").performClick()

        rule.onNodeWithText("https://openrouter.ai/api").assertExists()
        rule.onNodeWithText("openrouter/free").assertExists()
        rule.onNodeWithTag("ai-api-key").assertExists()
    }

    @Test
    fun togglesTwitchClientIdVisibility() {
        render(settingsOpen = true, settingsSection = SettingsSection.Platforms, twitchClientId = "client-id")

        rule.onNodeWithContentDescription("Mostrar valor").performClick()
        rule.onNodeWithContentDescription("Ocultar valor").assertExists()
    }

    @Test
    fun togglesAiApiKeyVisibility() {
        render(settingsOpen = true, settingsSection = SettingsSection.Ai)
        rule.onNodeWithText("OpenRouter").performClick()

        rule.onNodeWithContentDescription("Mostrar valor").performClick()
        rule.onNodeWithContentDescription("Ocultar valor").assertExists()
    }

    @Test
    fun showsRealSuggestionInCompactMode() {
        var stopped = false
        rule.setContent {
            Box(Modifier.size(340.dp, 125.dp)) {
                CompactContent(LiveSessionStatus.Running, 10, "Resposta ".repeat(80), true, false) {
                    stopped = true
                }
            }
        }
        rule.onNodeWithText("Mute").performClick()
        rule.runOnIdle { assertTrue(stopped) }
        assertEquals(
            "O chat quer saber o preço.",
            compactHeadline(LiveSessionStatus.Running, "O chat quer saber o preço.", real = true),
        )
    }

    @Test
    fun editsDeletesAndExportsLocalNote() {
        val repository = TransientNoteRepository()
        val note = LiveNote("local-note", "Texto original", "agora", "SUGESTÃO")
        repository.save(note)
        var exportedNotes = emptyList<LiveNote>()
        render(
            mainSection = MainSection.Notes,
            noteRepository = repository,
            onExportNotes = {
                exportedNotes = it
                true
            },
        )

        rule.onNodeWithContentDescription("Editar nota").performClick()
        rule.onNodeWithText("Conteúdo").performTextReplacement("Texto editado")
        rule.onNodeWithText("Salvar").performClick()
        rule.onNodeWithText("Texto editado").assertExists()

        rule.onNodeWithText("Exportar .md").performClick()
        rule.runOnIdle { assertEquals("Texto editado", exportedNotes.single().text) }

        rule.onNodeWithContentDescription("Excluir nota").performScrollTo().performClick()
        rule.onNodeWithText("Excluir").performClick()
        rule.onNodeWithText("0 notas salvas").assertExists()
    }

    @Test
    fun exportsVisibleIdeas() {
        var exportedIdeas = emptyList<LiveIdea>()
        render(
            mainSection = MainSection.Ideas,
            onExportIdeas = {
                exportedIdeas = it
                true
            },
        )

        rule.onNodeWithText("Exportar .md").performClick()
        rule.onNodeWithText("Markdown exportado.").assertExists()
        rule.runOnIdle {
            assertEquals(3, exportedIdeas.size)
            assertEquals("CONTEÚDO", exportedIdeas.first().tag)
        }
    }

    @Test
    fun updatesAgentNameAndTone() {
        var saved = AgentConfiguration()
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Agent,
            onAgentConfigurationChange = { saved = it },
        )

        rule.onNodeWithTag("agent-name-field").performTextReplacement("Acorde")
        rule.onNodeWithText("Analítico").performClick()
        rule.runOnIdle {
            assertEquals("Acorde", saved.name)
            assertEquals(AgentTone.Analytical, saved.tone)
        }
        rule.onNodeWithText("concluir").performClick()
        rule.onNodeWithText("Acorde").assertExists()
    }

    @Test
    fun guidesFirstUseThroughSettings() {
        var finished = false
        render(
            firstUseOpen = true,
            onFirstUseFinished = { finished = true },
        )

        rule.onNodeWithText("Configure o Rocky").assertExists()
        rule.onNodeWithText("Configurar Twitch").performClick()
        rule.onNodeWithText("Conexão com plataformas").assertExists()
        rule.onNodeWithText("concluir").performClick()
        rule.onNodeWithText("Configure o Rocky").assertExists()

        rule.onNodeWithText("Usar demonstração").performScrollTo().performClick()
        rule.onNodeWithText("MODO DEMONSTRAÇÃO").assertExists()
        rule.runOnIdle { assertTrue(finished) }
    }

    @Test
    fun changesInterfaceLanguage() {
        var savedLanguage = RockyLanguage.PortugueseBrazil
        render(
            settingsOpen = true,
            onLanguageChange = { savedLanguage = it },
        )

        rule.onNodeWithText("English").performClick()

        rule.onNodeWithText("Settings").assertExists()
        rule.onNodeWithText("Agent name").assertExists()
        rule.onNodeWithText("done").performClick()
        rule.onNodeWithText("DEMO MODE").assertExists()
        rule.onNodeWithText("Start").assertExists()
        rule.onNodeWithText("Conversation").assertExists()
        rule.runOnIdle { assertEquals(RockyLanguage.English, savedLanguage) }
    }

    @Test
    fun showsFirstUseGuideInEnglish() {
        render(firstUseOpen = true, language = RockyLanguage.English)

        rule.onNodeWithText("Set up Rocky").assertExists()
        rule.onNodeWithText("1. Connect your Twitch").assertExists()
        rule.onNodeWithText("2. Configure AI").assertExists()
        rule.onNodeWithText("3. Test voice").assertExists()
    }

    @Test
    fun windowControlsInvokeCallbacks() {
        var pinned = false
        var compact = false
        rule.setContent {
            Box(Modifier.size(420.dp, 720.dp)) {
                RockyWindow(
                    compact = false,
                    pinned = false,
                    onTogglePinned = { pinned = true },
                    onToggleCompact = { compact = true },
                )
            }
        }

        rule.onNodeWithContentDescription("Fixar janela").performClick()
        rule.onNodeWithContentDescription("Modo compacto").performClick()

        rule.runOnIdle {
            assertTrue(pinned)
            assertTrue(compact)
        }
    }

    private fun prepareAutomaticSession(twitch: FakeTwitchChatClient, ai: FakeAiSuggestionClient) {
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Ai,
            twitchChatClient = twitch,
            twitchClientId = "client-id",
            aiSuggestionClient = ai,
        )
        rule.onNodeWithTag("automatic-analysis").performScrollTo().performClick()
        rule.onNodeWithText("Plataformas").performClick()
        rule.onNodeWithText("Conectar Twitch").performClick()
    }

    private fun render(
        compact: Boolean = false,
        mainSection: MainSection = MainSection.Conversation,
        settingsOpen: Boolean = false,
        settingsSection: SettingsSection = SettingsSection.Agent,
        noteRepository: NoteRepository? = null,
        onExportNotes: (List<LiveNote>) -> Boolean = { false },
        onExportIdeas: (List<LiveIdea>) -> Boolean = { false },
        twitchChatClient: TwitchChatClient? = null,
        twitchClientId: String = "",
        aiSuggestionClient: AiSuggestionClient? = null,
        onAgentConfigurationChange: (AgentConfiguration) -> Unit = {},
        firstUseOpen: Boolean = false,
        onFirstUseFinished: () -> Unit = {},
        onLanguageChange: (RockyLanguage) -> Unit = {},
        language: RockyLanguage = RockyLanguage.PortugueseBrazil,
    ) {
        rule.setContent {
            key(mainSection, settingsOpen, settingsSection, firstUseOpen) {
                var showingSettings by remember { mutableStateOf(settingsOpen) }
                Box(Modifier.size(420.dp, 820.dp)) {
                    RockyWindow(
                        compact = compact,
                        pinned = false,
                        onTogglePinned = {},
                        onToggleCompact = {},
                        noteRepository = noteRepository,
                        twitchChatClient = twitchChatClient ?: FakeTwitchChatClient(),
                        aiSuggestionClient = aiSuggestionClient ?: FakeAiSuggestionClient(),
                        onAgentConfigurationChange = onAgentConfigurationChange,
                        initialTwitchClientId = twitchClientId,
                        onExportNotes = onExportNotes,
                        onExportIdeas = onExportIdeas,
                        initialMainSectionIndex = mainSection.ordinal,
                        initialSettingsOpen = settingsOpen,
                        onSettingsVisibilityChanged = { showingSettings = it },
                        initialSettingsSectionIndex = settingsSection.ordinal,
                        initialFirstUseOpen = firstUseOpen,
                        onFirstUseFinished = onFirstUseFinished,
                        initialLanguage = language,
                        onLanguageChange = onLanguageChange,
                    )
                }
            }
        }
    }

    private fun capture(fileName: String) {
        val outputDirectory = System.getenv("ROCKY_SCREENSHOT_DIR")?.let(Path::of) ?: return
        rule.waitForIdle()
        val bitmap = rule.onNodeWithTag("rocky-window").captureToImage().asSkiaBitmap()
        val data = requireNotNull(Image.makeFromBitmap(bitmap).encodeToData(EncodedImageFormat.PNG))
        Files.createDirectories(outputDirectory)
        Files.write(outputDirectory.resolve(fileName), data.bytes)
    }

    private fun waitForSuggestion() {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithText(
                "Sete pessoas perguntaram o preço do curso nos últimos dois minutos. Vale responder agora.",
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private class FakeTwitchChatClient : TwitchChatClient {
        private var listener = TwitchConnectionListener {}

        override fun connect(clientId: String, listener: TwitchConnectionListener) {
            this.listener = listener
        }

        override fun disconnect() = Unit

        override fun close() = Unit

        fun emit(event: TwitchConnectionEvent) = listener.onEvent(event)

        fun emitMessages(count: Int, startAt: Int = 0) = repeat(count) { offset ->
            val index = startAt + offset
            emit(
                TwitchConnectionEvent.MessageReceived(
                    ChatMessage("message-$index", "viewer", "Mensagem $index", StreamPlatform.Twitch),
                ),
            )
        }
    }

    private class FakeAiSuggestionClient : AiSuggestionClient {
        var lastRequest: String? = null
        var requests = 0
        var failuresRemaining = 0
        override fun testConnection(configuration: AiProviderConfiguration) =
            AiConnectionResult(true, "Conectado")

        override fun generateSuggestion(
            configuration: AiProviderConfiguration,
            messages: List<ChatMessage>,
            streamerRequest: String?,
            agent: AgentConfiguration,
        ): AiGeneratedSuggestion {
            requests += 1
            if (failuresRemaining-- > 0) error("Temporary provider failure")
            lastRequest = streamerRequest
            return AiGeneratedSuggestion("O chat quer saber o preço.", setOf(messages.last().id))
        }

        override fun close() = Unit
    }
}
