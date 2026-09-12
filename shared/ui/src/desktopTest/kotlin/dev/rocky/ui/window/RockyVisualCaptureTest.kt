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
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
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
import dev.rocky.core.voice.AudioInputDevice
import dev.rocky.core.voice.LocalTranscriptionConfiguration
import dev.rocky.core.voice.SystemVoice
import dev.rocky.core.voice.VoiceConfiguration
import dev.rocky.core.voice.VoiceOutputConfiguration
import dev.rocky.core.voice.VoiceService
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
    fun showsWhenTheMicrophoneIsCapturing() {
        rule.setContent {
            AssistantFooter(
                active = true,
                capturing = true,
                inputLevel = 0.42f,
                onTalk = {},
            )
        }

        rule.onNodeWithTag("microphone-level", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("Microphone listening").assertIsDisplayed()
        rule.onNodeWithText("Speak now · level 42%").assertIsDisplayed()
    }

    @Test
    fun showsCurrentLiveMetricsInFooter() {
        rule.setContent {
            AssistantFooter(
                viewerCount = 321,
                messagesPerMinute = 18,
                onTalk = {},
            )
        }

        rule.onNodeWithText("321").assertIsDisplayed()
        rule.onNodeWithText("18").assertIsDisplayed()
        rule.onNodeWithText("assistindo").assertIsDisplayed()
        rule.onNodeWithText("msg/min").assertIsDisplayed()
    }

    @Test
    fun distinguishesAnActiveChatFromAnActiveMicrophone() {
        rule.setContent {
            RockyHeader(
                sessionStatus = LiveSessionStatus.Running,
                twitchPhase = dev.rocky.core.twitch.TwitchConnectionPhase.Connected,
                microphoneActive = false,
            )
        }

        rule.onNodeWithText("CHAT ACTIVE").assertIsDisplayed()
        assertTrue(rule.onAllNodesWithText("MIC ON").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun testsAConversationFromVoiceSettings() {
        val voice = FakeVoiceService().apply { transcript = "Rocky, você está me ouvindo?" }
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Voice,
            voiceService = voice,
            voiceConfiguration = VoiceConfiguration(
                transcription = LocalTranscriptionConfiguration("whisper-cli", "model.bin"),
            ),
        )

        rule.onNodeWithTag("test-conversation").performScrollTo().performClick()
        rule.mainClock.advanceTimeBy(8_100L)
        rule.waitUntil(timeoutMillis = 5_000) { voice.spoken.isNotEmpty() }

        rule.onNodeWithTag("voice-test-transcript").assertIsDisplayed()
        rule.onNodeWithTag("voice-test-response").assertIsDisplayed()
        assertEquals(
            "Eu ouvi você dizer: Rocky, você está me ouvindo?. Meu microfone está funcionando.",
            voice.spoken.single(),
        )
    }

    @Test
    fun preparesVoiceRecognitionFromSettings() {
        val voice = FakeVoiceService().apply { automaticSetupSupported = true }
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Voice,
            voiceService = voice,
        )

        rule.onNodeWithTag("prepare-transcription").performScrollTo().performClick()
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodesWithText("Reconhecimento de voz pronto").fetchSemanticsNodes().isNotEmpty()
        }

        rule.onNodeWithTag("test-conversation").performScrollTo().assertIsDisplayed()
    }

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
        rule.onNodeWithText("Conecte sua Twitch nas configurações para acompanhar uma live.").assertExists()
        capture("implementation-main.png")

        val mainSections = mapOf(
            MainSection.Support to "Super Chats ainda não estão conectados.",
            MainSection.Notes to "Notas locais",
            MainSection.Ideas to "Ideias da live",
            MainSection.Pulse to "audiência indisponível · 0 msg/min",
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
            twitch.emit(TwitchConnectionEvent.AudienceUpdated(321))
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
        rule.onNodeWithText("Ideias da live").assertExists()
        assertTrue(
            rule.onAllNodesWithText("Série curta respondendo as 5 dúvidas mais repetidas do chat.")
                .fetchSemanticsNodes().isEmpty(),
        )
        rule.onNodeWithText("Pulso").performClick()
        rule.onNodeWithText("321 assistindo · 1 msg/min").assertExists()
        rule.onNodeWithText("Conversa").performClick()
        rule.onNodeWithTag("streamer-text-request").assertExists()
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.PhaseChanged(dev.rocky.core.twitch.TwitchConnectionPhase.Reconnecting))
        }
        capture("implementation-real-session.png")
    }

    @Test
    fun startsListeningWhenTwitchConnects() {
        val twitch = FakeTwitchChatClient()
        val voice = FakeVoiceService()
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Platforms,
            twitchChatClient = twitch,
            twitchClientId = "client-id",
            voiceService = voice,
            voiceConfiguration = VoiceConfiguration(
                transcription = LocalTranscriptionConfiguration("whisper-cli", "model.bin"),
            ),
        )

        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle { twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live"))) }
        rule.waitUntil(timeoutMillis = 5_000) { voice.captureStarts == 1 }
    }

    @Test
    fun answersACompleteRockyVoiceCommand() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient()
        val voice = FakeVoiceService().apply { transcript = "Rocky, o que o chat quer?" }
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Platforms,
            twitchChatClient = twitch,
            twitchClientId = "client-id",
            aiSuggestionClient = ai,
            voiceService = voice,
            voiceConfiguration = VoiceConfiguration(
                transcription = LocalTranscriptionConfiguration("whisper-cli", "model.bin"),
            ),
        )

        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emit(TwitchConnectionEvent.MessageReceived(ChatMessage("m1", "viewer", "CS", StreamPlatform.Twitch)))
        }
        rule.waitUntil(timeoutMillis = 5_000) { voice.captureStarts == 1 }
        rule.mainClock.advanceTimeBy(8_100L)
        rule.waitUntil(timeoutMillis = 5_000) { voice.spoken.size >= 2 }

        assertEquals("o que o chat quer?", ai.lastRequest)
        assertEquals(listOf("Vou verificar o chat.", "O chat quer saber o preço."), voice.spoken.take(2))
    }

    @Test
    fun savesNotesAndIdeasByVoiceWithoutAnotherAiRequest() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient()
        val repository = TransientNoteRepository()
        val voice = FakeVoiceService().apply {
            transcripts.addAll(listOf("Rocky, o que o chat quer?", "Rocky, salva isso como nota", "Rocky, salva isso como ideias", "Rocky, o que o chat quer?"))
        }
        render(
            settingsOpen = true, settingsSection = SettingsSection.Platforms,
            twitchChatClient = twitch, twitchClientId = "client-id",
            aiSuggestionClient = ai, voiceService = voice, noteRepository = repository,
            voiceConfiguration = VoiceConfiguration(transcription = LocalTranscriptionConfiguration("whisper-cli", "model.bin")),
        )
        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emitMessages(1)
        }
        for (turn in 1..4) {
            rule.waitUntil(5_000) { voice.captureStarts >= turn }
            rule.mainClock.advanceTimeBy(8_100L)
            rule.waitUntil(5_000) { voice.captureStarts >= turn + 1 }
        }
        rule.runOnIdle {
            assertEquals(2, ai.requests)
            assertEquals(2, repository.getAll().size)
            assertEquals(1, repository.getAll().count { it.tag == IDEA_TAG })
            assertTrue(voice.spoken.contains("Nota salva."))
            assertTrue(voice.spoken.contains("Ideia salva."))
        }
    }

    @Test
    fun reportsAProviderFailureByVoice() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient().apply { failuresRemaining = 1 }
        val voice = FakeVoiceService().apply { transcript = "Rocky, o que o chat quer?" }
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Platforms,
            twitchChatClient = twitch,
            twitchClientId = "client-id",
            aiSuggestionClient = ai,
            voiceService = voice,
            voiceConfiguration = VoiceConfiguration(
                transcription = LocalTranscriptionConfiguration("whisper-cli", "model.bin"),
            ),
        )

        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emit(TwitchConnectionEvent.MessageReceived(ChatMessage("m1", "viewer", "CS", StreamPlatform.Twitch)))
        }
        rule.waitUntil(timeoutMillis = 5_000) { voice.captureStarts == 1 }
        rule.mainClock.advanceTimeBy(8_100L)
        rule.waitUntil(timeoutMillis = 5_000) { voice.spoken.size >= 2 }

        assertEquals("Vou verificar o chat.", voice.spoken.first())
        assertEquals("Não consegui consultar o chat agora. Vou continuar ouvindo.", voice.spoken[1])
    }

    @Test
    fun listensForAQuestionAfterCallingRocky() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient()
        val voice = FakeVoiceService().apply {
            transcripts += listOf("Rocky", "o pessoal está gostando?")
        }
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Platforms,
            twitchChatClient = twitch,
            twitchClientId = "client-id",
            aiSuggestionClient = ai,
            voiceService = voice,
            voiceConfiguration = VoiceConfiguration(
                transcription = LocalTranscriptionConfiguration("whisper-cli", "model.bin"),
            ),
        )

        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emit(TwitchConnectionEvent.MessageReceived(ChatMessage("m1", "viewer", "Gostei", StreamPlatform.Twitch)))
        }
        rule.waitUntil(timeoutMillis = 5_000) { voice.captureStarts == 1 }
        rule.mainClock.advanceTimeBy(8_100L)
        rule.waitUntil(timeoutMillis = 5_000) { voice.captureStarts == 2 }
        rule.mainClock.advanceTimeBy(8_100L)
        rule.waitUntil(timeoutMillis = 5_000) { voice.spoken.size >= 3 }

        assertEquals("o pessoal está gostando?", ai.lastRequest)
        assertEquals("Estou ouvindo.", voice.spoken.first())
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
    fun sendsCancelsAndRetriesFromTheRealWindow() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient().apply { responseGate = java.util.concurrent.CountDownLatch(1) }
        render(settingsOpen = true, settingsSection = SettingsSection.Platforms,
            twitchChatClient = twitch, twitchClientId = "client", aiSuggestionClient = ai)
        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emitMessages(1)
        }
        rule.onNodeWithText("concluir").performClick()
        rule.onNodeWithTag("streamer-text-request").performTextReplacement("Primeiro pedido")
        rule.onNodeWithTag("send-streamer-text-request").performClick()
        rule.waitUntil(3_000) { ai.lastRequest == "Primeiro pedido" }
        rule.onNodeWithText("Cancelar análise").performClick()
        rule.waitUntil(3_000) { ai.interrupted }
        ai.responseGate = null
        rule.onNodeWithTag("streamer-text-request").performTextReplacement("Segundo pedido")
        rule.onNodeWithTag("send-streamer-text-request").performClick()
        rule.waitUntil(3_000) { ai.lastRequest == "Segundo pedido" }
        rule.onNodeWithText("O chat quer saber o preço.").assertExists()
        capture("implementation-active-answer.png")
        val inputHeight = rule.onNodeWithTag("streamer-text-request").fetchSemanticsNode().boundsInRoot.height
        val chatHeight = rule.onNodeWithTag("chat-messages").fetchSemanticsNode().boundsInRoot.height
        assertTrue("Input height: $inputHeight", inputHeight >= 56f)
        assertTrue("Chat height: $chatHeight", chatHeight >= 60f)
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
    fun keepsLatestMessageVisibleInScrollableChat() {
        var messages by mutableStateOf((1..20).map {
            ChatMessage("message-$it", "viewer", "Mensagem $it", StreamPlatform.Twitch)
        })
        rule.setContent { Box(Modifier.size(420.dp, 300.dp)) { ConversationContent(messages) } }

        rule.onNodeWithText("Mensagem 20").assertIsDisplayed()
        rule.onNodeWithTag("chat-messages").performScrollToIndex(0)
        rule.onNodeWithText("Mensagem 1").assertIsDisplayed()
        rule.runOnIdle { messages = messages + ChatMessage("message-21", "viewer", "Mensagem 21", StreamPlatform.Twitch) }
        rule.onNodeWithText("Mensagem 21").assertIsDisplayed()
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
                CompactContent(LiveSessionStatus.Running, 10, "Resposta ".repeat(80), false) {
                    stopped = true
                }
            }
        }
        rule.onNodeWithText("Mute").performClick()
        rule.runOnIdle { assertTrue(stopped) }
        assertEquals(
            "O chat quer saber o preço.",
            compactHeadline(LiveSessionStatus.Running, "O chat quer saber o preço."),
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
    fun doesNotExportPlaceholderIdeas() {
        var exportedIdeas = emptyList<LiveIdea>()
        render(
            mainSection = MainSection.Ideas,
            onExportIdeas = {
                exportedIdeas = it
                true
            },
        )

        rule.onNodeWithText("Exportar .md").assertIsNotEnabled()
        rule.runOnIdle {
            assertTrue(exportedIdeas.isEmpty())
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
    fun dataDeletionRequiresConfirmationAndPreservesNotesOnCancel() {
        val repository = TransientNoteRepository()
        repository.save(LiveNote("note", "Keep me", "now", "test"))
        render(settingsOpen = true, settingsSection = SettingsSection.Data, noteRepository = repository)
        rule.onNodeWithText("Apagar notas").performClick()
        rule.onNodeWithText("Cancelar").performClick()
        assertEquals(1, repository.getAll().size)
        rule.onNodeWithText("Apagar notas").performClick()
        rule.onNodeWithText("Confirmar").performClick()
        assertTrue(repository.getAll().isEmpty())
    }

    @Test
    fun guidesFirstUseThroughSettings() {
        render(
            firstUseOpen = true,
        )

        rule.onNodeWithText("Configure o Rocky").assertExists()
        rule.onNodeWithText("Configurar Twitch").performClick()
        rule.onNodeWithText("Conexão com plataformas").assertExists()
        rule.onNodeWithText("concluir").performClick()
        rule.onNodeWithText("Configure o Rocky").assertExists()

        assertTrue(rule.onAllNodesWithText("Usar demonstração").fetchSemanticsNodes().isEmpty())
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
        rule.onNodeWithText("Connect your Twitch in settings to follow a stream.").assertExists()
        rule.onNodeWithText("Conversation").assertExists()
        rule.runOnIdle { assertEquals(RockyLanguage.English, savedLanguage) }
    }

    @Test
    fun showsFirstUseGuideInEnglish() {
        render(firstUseOpen = true, language = RockyLanguage.English)

        rule.onNodeWithText("Set up Rocky").assertExists()
        rule.onNodeWithText("1. Connect your Twitch").assertExists()
        rule.onNodeWithText("2. Configure AI").assertExists()
        rule.onNodeWithText("3. Test voice (optional)").assertExists()
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
        rule.onNodeWithTag("automatic-analysis").performClick()
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
        voiceService: VoiceService = FakeVoiceService(),
        voiceConfiguration: VoiceConfiguration = VoiceConfiguration(),
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
                        voiceService = voiceService,
                        initialVoiceConfiguration = voiceConfiguration,
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
        @Volatile var lastRequest: String? = null
        @Volatile var responseGate: java.util.concurrent.CountDownLatch? = null
        @Volatile var interrupted = false
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
            try { responseGate?.await() } catch (error: InterruptedException) { interrupted = true; throw error }
            return AiGeneratedSuggestion("O chat quer saber o preço.", setOf(messages.last().id))
        }

        override fun close() = Unit
    }

    private class FakeVoiceService : VoiceService {
        var captureStarts = 0
        var transcript = ""
        var automaticSetupSupported = false
        val transcripts = mutableListOf<String>()
        val spoken = mutableListOf<String>()
        override val automaticTranscriptionSetupSupported: Boolean
            get() = automaticSetupSupported
        override fun availableVoices(): List<SystemVoice> = emptyList()
        override fun availableMicrophones(): List<AudioInputDevice> = emptyList()
        override fun speak(text: String, configuration: VoiceOutputConfiguration) { spoken += text }
        override fun stopSpeaking() = Unit
        override fun startCapture(microphoneId: String?) { captureStarts += 1 }
        override fun prepareTranscription(onProgress: (String) -> Unit): LocalTranscriptionConfiguration {
            onProgress("Reconhecimento de voz pronto")
            return LocalTranscriptionConfiguration("managed-whisper", "managed-model")
        }
        override fun stopCaptureAndTranscribe(configuration: LocalTranscriptionConfiguration) =
            if (transcripts.isEmpty()) transcript else transcripts.removeAt(0)
        override fun cancelCapture() = Unit
        override fun close() = Unit
    }
}
