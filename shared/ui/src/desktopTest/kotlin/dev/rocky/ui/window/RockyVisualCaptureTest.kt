package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.key
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
        rule.onNodeWithText("Analisar agora").performClick()
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodesWithText("O chat quer saber o preço.").fetchSemanticsNodes().isNotEmpty()
        }
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
        rule.runOnIdle { assertEquals(RockyLanguage.English, savedLanguage) }
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

    private fun render(
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
    ) {
        rule.setContent {
            key(mainSection, settingsOpen, settingsSection, firstUseOpen) {
                Box(Modifier.size(420.dp, if (settingsOpen) 520.dp else 720.dp)) {
                    RockyWindow(
                        compact = false,
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
                        initialSettingsSectionIndex = settingsSection.ordinal,
                        initialFirstUseOpen = firstUseOpen,
                        onFirstUseFinished = onFirstUseFinished,
                        initialLanguage = RockyLanguage.PortugueseBrazil,
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
    }

    private class FakeAiSuggestionClient : AiSuggestionClient {
        override fun testConnection(configuration: AiProviderConfiguration) =
            AiConnectionResult(true, "Conectado")

        override fun generateSuggestion(
            configuration: AiProviderConfiguration,
            messages: List<ChatMessage>,
            streamerRequest: String?,
            agent: AgentConfiguration,
        ) =
            AiGeneratedSuggestion("O chat quer saber o preço.", setOf(messages.last().id))

        override fun close() = Unit
    }
}
