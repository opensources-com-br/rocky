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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import dev.rocky.core.live.LiveIdea
import dev.rocky.core.live.LiveNote
import dev.rocky.core.live.LiveSessionStatus
import dev.rocky.core.locale.RockyLanguage
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.AiProviderConfiguration
import dev.rocky.core.ai.AiProviderKind
import dev.rocky.core.ai.AiSuggestionClient
import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.agent.AgentTone
import dev.rocky.core.live.ChatMessage
import dev.rocky.core.live.StreamPlatform
import dev.rocky.core.facebook.FacebookChatClient
import dev.rocky.core.facebook.FacebookConfiguration
import dev.rocky.core.facebook.FacebookConnectionEvent
import dev.rocky.core.facebook.FacebookConnectionListener
import dev.rocky.core.facebook.FacebookLiveVideo
import dev.rocky.core.facebook.FacebookPage
import dev.rocky.core.tiktok.TikTokAccount
import dev.rocky.core.tiktok.TikTokChatClient
import dev.rocky.core.tiktok.TikTokConfiguration
import dev.rocky.core.tiktok.TikTokConnectionEvent
import dev.rocky.core.tiktok.TikTokConnectionListener
import dev.rocky.core.tiktok.TikTokLiveRoom
import dev.rocky.core.kick.KickAccount
import dev.rocky.core.kick.KickChatClient
import dev.rocky.core.kick.KickConfiguration
import dev.rocky.core.kick.KickConnectionEvent
import dev.rocky.core.kick.KickConnectionListener
import dev.rocky.core.notes.NoteRepository
import dev.rocky.core.twitch.TwitchAccount
import dev.rocky.core.twitch.TwitchChatClient
import dev.rocky.core.twitch.TwitchConnectionEvent
import dev.rocky.core.twitch.TwitchConnectionListener
import dev.rocky.core.youtube.YouTubeAccount
import dev.rocky.core.youtube.YouTubeBroadcast
import dev.rocky.core.youtube.YouTubeChatClient
import dev.rocky.core.youtube.YouTubeConfiguration
import dev.rocky.core.youtube.YouTubeConnectionEvent
import dev.rocky.core.youtube.YouTubeConnectionListener
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
        rule.onNodeWithText("total watching").assertIsDisplayed()
        rule.onNodeWithText("msg/min").assertIsDisplayed()
    }

    @Test
    fun showsFiveOnlinePlatformsWithCompactAudiences() {
        rule.setContent {
            PlatformStrip(
                platforms = listOf(
                    PlatformStatus("Twitch", "@twitch", 100_900, 18, PlatformColor.Twitch, connected = true),
                    PlatformStatus("Kick", "@kick", 10_900, 12, PlatformColor.Kick, connected = true),
                    PlatformStatus("YouTube", "YouTube", 1_100, 10, PlatformColor.YouTube, connected = true),
                    PlatformStatus("Facebook", "Facebook", 52, 6, PlatformColor.Facebook, connected = true),
                    PlatformStatus("TikTok", "@tiktok", 88, 9, PlatformColor.TikTok, connected = true),
                ),
            )
        }

        rule.onNodeWithText("5 PLATFORMS ONLINE").assertIsDisplayed()
        rule.onNodeWithText("100.9K").assertIsDisplayed()
        rule.onNodeWithText("10.9K").assertIsDisplayed()
        rule.onNodeWithText("1.1K").assertIsDisplayed()
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
    fun voiceSettingsDoNotShowShortcutControls() {
        render(settingsOpen = true, settingsSection = SettingsSection.Voice)

        assertTrue(rule.onAllNodesWithText("Atalhos globais · Ctrl + Shift + F1–F12").fetchSemanticsNodes().isEmpty())
        assertTrue(rule.onAllNodesWithText("Aplicar atalhos").fetchSemanticsNodes().isEmpty())
        rule.onNodeWithTag("test-conversation").assertExists()
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
            "settings-agent-compact" -> {
                render(settingsOpen = true, settingsSection = SettingsSection.Agent)
                capture("implementation-agent-compact.png")
                return
            }
            "settings-macos" -> {
                render(
                    settingsOpen = true,
                    settingsSection = SettingsSection.Agent,
                    windowWidth = 780.dp,
                    windowHeight = 680.dp,
                    mainWindow = {},
                    settingsWindow = { visible, _, content -> if (visible) content() },
                )
                capture("implementation-settings-macos.png", "rocky-settings-window")
                return
            }
            "settings-ai" -> {
                render(settingsOpen = true, settingsSection = SettingsSection.Ai)
                capture("implementation-settings-ai.png")
                return
            }
            "settings-voice" -> {
                render(settingsOpen = true, settingsSection = SettingsSection.Voice)
                capture("implementation-settings-voice.png")
                return
            }
            "settings-openrouter" -> {
                render(settingsOpen = true, settingsSection = SettingsSection.Ai)
                selectAiProvider("OpenRouter")
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
        rule.onNodeWithText("Conecte Twitch, Kick, YouTube, Facebook ou TikTok nas configurações para acompanhar uma live.").assertExists()
        capture("implementation-main.png")

        val mainSections = mapOf(
            MainSection.Support to "Super Chats ainda não estão conectados.",
            MainSection.Notes to "Notas locais",
            MainSection.Ideas to "Ideias da live",
            MainSection.Pulse to "Até 10 minutos, amostras a cada 5 segundos. Lacunas indicam medições ausentes.",
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
            SettingsSection.Platforms to "Twitch",
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

        assertTrue(rule.onAllNodesWithText("CONEXÃO REAL · TWITCH").fetchSemanticsNodes().isEmpty())
        rule.onNodeWithTag("platform-twitch").performClick()
        rule.onNodeWithText("Desconectar").assertExists()
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
        assertTrue(rule.onAllNodesWithTag("streamer-text-request").fetchSemanticsNodes().isEmpty())
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.PhaseChanged(dev.rocky.core.twitch.TwitchConnectionPhase.Reconnecting))
        }
        capture("implementation-real-session.png")
    }

    @Test
    fun showsKickAsAnUpcomingPlatform() {
        render(settingsOpen = true, settingsSection = SettingsSection.Platforms)

        rule.onNodeWithText("Kick").performScrollTo().assertIsDisplayed()
        rule.onNodeWithText("Em breve").assertIsDisplayed()
        assertTrue(rule.onAllNodesWithText("Conectar Kick").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun connectsAndDisplaysRealYouTubeChat() {
        val youtube = FakeYouTubeChatClient()
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Platforms,
            youtubeChatClient = youtube,
            youtubeConfiguration = YouTubeConfiguration("client-id", "client-secret"),
        )

        rule.onNodeWithText("Conectar YouTube").performScrollTo().performClick()
        rule.runOnIdle {
            youtube.emit(YouTubeConnectionEvent.Connected(
                YouTubeAccount("channel", "Rocky YouTube"),
                YouTubeBroadcast("video", "chat", "Live Rocky"),
            ))
            youtube.emit(YouTubeConnectionEvent.MessageReceived(
                ChatMessage("youtube-message", "viewer", "Mensagem do YouTube", StreamPlatform.YouTube),
            ))
        }
        rule.onNodeWithText("concluir").performClick()

        rule.onNodeWithTag("platform-youtube").performClick()
        rule.onNodeWithText("Desconectar").assertExists()
        rule.onNodeWithText("Mensagem do YouTube").assertExists()
    }

    @Test
    fun connectsAndDisplaysRealFacebookChat() {
        val facebook = FakeFacebookChatClient()
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Platforms,
            facebookChatClient = facebook,
            facebookConfiguration = FacebookConfiguration("app-id", "app-secret"),
        )
        rule.onNodeWithText("Conectar Facebook").performScrollTo().performClick()
        rule.runOnIdle {
            facebook.emit(FacebookConnectionEvent.Connected(
                FacebookPage("page", "Rocky Facebook"), FacebookLiveVideo("live", "Live Rocky"),
            ))
            facebook.emit(FacebookConnectionEvent.MessageReceived(
                ChatMessage("facebook-message", "viewer", "Mensagem do Facebook", StreamPlatform.Facebook),
            ))
        }
        rule.onNodeWithText("concluir").performClick()
        rule.onNodeWithTag("platform-facebook").performClick()
        rule.onNodeWithText("Desconectar").assertExists()
        rule.onNodeWithText("Mensagem do Facebook").assertExists()
    }

    @Test
    fun connectsAndDisplaysRealTikTokChat() {
        val tiktok = FakeTikTokChatClient()
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Platforms,
            tiktokChatClient = tiktok,
            tiktokConfiguration = TikTokConfiguration("rocky_live"),
        )
        rule.onNodeWithText("Conectar TikTok").performScrollTo().performClick()
        rule.runOnIdle {
            tiktok.emit(TikTokConnectionEvent.Connected(
                TikTokAccount("rocky_live", "Rocky TikTok"), TikTokLiveRoom("room", "Live Rocky"),
            ))
            tiktok.emit(TikTokConnectionEvent.MessageReceived(
                ChatMessage("tiktok-message", "viewer", "Mensagem do TikTok", StreamPlatform.TikTok),
            ))
        }
        rule.onNodeWithText("concluir").performClick()
        rule.onNodeWithTag("platform-tiktok").performClick()
        rule.onNodeWithText("Desconectar").assertExists()
        rule.onNodeWithText("Mensagem do TikTok").assertExists()
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
    fun savesAnEarlierAnswerAndUndoesItFromHistory() {
        val twitch = FakeTwitchChatClient()
        val repository = TransientNoteRepository()
        render(settingsOpen = true, settingsSection = SettingsSection.Platforms,
            twitchChatClient = twitch, twitchClientId = "client", noteRepository = repository)
        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emitMessages(1)
        }
        rule.onNodeWithText("concluir").performClick()
        rule.onNodeWithText("Dúvidas principais").performClick()
        rule.waitUntil(3_000) { rule.onAllNodesWithText("O chat quer saber o preço.").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Histórico").performClick()
        rule.onNodeWithText("Histórico da conversa").assertExists()
        rule.onNodeWithText("Salvar ideia").performClick()
        rule.runOnIdle { assertEquals(1, repository.getAll().size) }
        rule.onNodeWithText("Desfazer").performClick()
        rule.runOnIdle { assertTrue(repository.getAll().isEmpty()) }
        rule.onNodeWithText("Fechar").performClick()
        assertTrue(rule.onAllNodesWithTag("streamer-text-request").fetchSemanticsNodes().isEmpty())
    }

    @Test fun preflightExplainsQuickActionsWithoutAudioSetup() {
        render()
        rule.onNodeWithContentDescription("Antes da live").performClick()
        rule.onNodeWithText("Você pode usar as ações rápidas de análise sem microfone.").assertExists()
        rule.onNodeWithText("Testar IA").performClick()
        rule.waitUntil(3_000) { rule.onAllNodesWithText("Conexão com IA verificada").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Plataforma de streaming desconectada").assertExists()
        rule.onNodeWithText("Fechar").performClick()
        rule.onNodeWithText("Dúvidas principais").assertExists()
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
        rule.waitUntil(timeoutMillis = 5_000) { voice.spoken.isNotEmpty() }

        assertEquals("o que o chat quer?", ai.lastRequest)
        assertEquals("O chat quer saber o preço.", voice.spoken.first())
    }

    @Test
    fun savesNotesAndIdeasByVoiceWithoutAnotherAiRequest() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient()
        val repository = TransientNoteRepository()
        val voice = FakeVoiceService().apply {
            transcripts.addAll(listOf("Rocky, o que o chat quer?", "salva isso como nota", "salva isso como ideias", "Rocky, o que o chat quer?"))
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
            rule.waitUntil(5_000) { voice.spoken.size >= turn }
            rule.mainClock.advanceTimeBy(350L)
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
        rule.waitUntil(timeoutMillis = 5_000) { voice.spoken.isNotEmpty() }

        assertEquals("Não consegui consultar o chat agora. Vou continuar ouvindo.", voice.spoken.first())
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
        rule.waitUntil(timeoutMillis = 5_000) { voice.spoken.size >= 2 }

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
        rule.mainClock.advanceTimeBy(300_100)
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
        rule.mainClock.advanceTimeBy(300_100)
        rule.waitUntil(timeoutMillis = 3_000) { ai.requests == 2 }

        rule.onNodeWithText("O chat quer saber o preço.").assertExists()
    }

    @Test
    fun cancellingAVoiceRequestAllowsLaterSuggestionsToBeRead() {
        val twitch = FakeTwitchChatClient()
        val ai = FakeAiSuggestionClient().apply { responseGate = java.util.concurrent.CountDownLatch(1) }
        val voice = FakeVoiceService().apply {
            transcripts += "Rocky, o que o chat quer?"
            transcript = "Conversa sem comando"
        }
        render(settingsOpen = true, settingsSection = SettingsSection.Platforms,
            twitchChatClient = twitch, twitchClientId = "client", aiSuggestionClient = ai,
            voiceService = voice, voiceConfiguration = VoiceConfiguration(readSuggestions = true,
                transcription = LocalTranscriptionConfiguration("whisper-cli", "model.bin")))
        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle {
            twitch.emit(TwitchConnectionEvent.Connected(TwitchAccount("42", "rocky_live")))
            twitch.emitMessages(1)
        }
        rule.onNodeWithText("concluir").performClick()
        rule.waitUntil(5_000) { voice.captureStarts >= 1 }
        rule.mainClock.advanceTimeBy(8_100L)
        rule.waitUntil(5_000) { ai.lastRequest == "o que o chat quer?" }
        rule.onNodeWithText("Cancelar análise").performClick()
        rule.waitUntil(5_000) { ai.interrupted && voice.captureStarts >= 2 }
        assertTrue(voice.spoken.isEmpty())
        ai.responseGate = null
        rule.onNodeWithText("Dúvidas principais").performClick()
        rule.waitUntil(5_000) { voice.spoken.isNotEmpty() }
        assertEquals(listOf("O chat quer saber o preço."), voice.spoken.toList())
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
        rule.onNodeWithText("Dúvidas principais").performClick()
        rule.waitUntil(3_000) { ai.lastRequest != null }
        rule.onNodeWithText("Cancelar análise").performClick()
        rule.waitUntil(3_000) { ai.interrupted }
        ai.responseGate = null
        val firstRequest = ai.lastRequest
        rule.onNodeWithText("O que perdi?").performClick()
        rule.waitUntil(3_000) { ai.lastRequest != firstRequest }
        rule.onNodeWithText("O chat quer saber o preço.").assertExists()
        capture("implementation-active-answer.png")
        val chatHeight = rule.onNodeWithTag("chat-messages").fetchSemanticsNode().boundsInRoot.height
        assertTrue("Chat height: $chatHeight", chatHeight >= 140f)
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

        selectAiProvider("OpenRouter")

        rule.onNodeWithText("https://openrouter.ai/api").assertDoesNotExist()
        rule.onNodeWithText("Configuração avançada da conexão").performClick()
        rule.onNodeWithText("https://openrouter.ai/api").assertExists()
        rule.onNodeWithText("openrouter/free").assertExists()
        rule.onNodeWithTag("ai-api-key").assertExists()
    }

    @Test
    fun configuresAnthropicFromAiSettings() {
        render(settingsOpen = true, settingsSection = SettingsSection.Ai)

        selectAiProvider("Anthropic API")

        rule.onNodeWithText("claude-haiku-4-5-20251001").assertExists()
        rule.onNodeWithTag("ai-api-key").assertExists()
    }

    @Test
    fun configuresGeminiFromAiSettings() {
        render(settingsOpen = true, settingsSection = SettingsSection.Ai)

        selectAiProvider("Google Gemini API")

        rule.onNodeWithText("gemini-3.8-flash").assertExists()
        rule.onNodeWithTag("ai-api-key").assertExists()
    }

    @Test
    fun configuresGrokFromAiSettings() {
        render(settingsOpen = true, settingsSection = SettingsSection.Ai)

        selectAiProvider("xAI Grok API")

        rule.onNodeWithText("grok-4.6").assertExists()
        rule.onNodeWithTag("ai-api-key").assertExists()
    }

    @Test
    fun togglesTwitchClientIdVisibility() {
        render(settingsOpen = true, settingsSection = SettingsSection.Platforms, twitchClientId = "client-id")

        rule.onNodeWithText("Avançado · aplicativo Twitch").performClick()
        rule.onAllNodesWithContentDescription("Mostrar valor")[0].performClick()
        rule.onNodeWithContentDescription("Ocultar valor").assertExists()
    }

    @Test
    fun togglesAiApiKeyVisibility() {
        render(settingsOpen = true, settingsSection = SettingsSection.Ai)
        selectAiProvider("OpenRouter")

        rule.onNodeWithContentDescription("Mostrar valor").performScrollTo().performClick()
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
    fun updatesAgentPreferencesInAWideWindow() {
        var savedAgent = AgentConfiguration()
        var savedLanguage = RockyLanguage.PortugueseBrazil
        render(
            settingsOpen = true,
            windowWidth = 780.dp,
            windowHeight = 680.dp,
            mainWindow = {},
            settingsWindow = { visible, _, content -> if (visible) content() },
            onAgentConfigurationChange = { savedAgent = it },
            onLanguageChange = { savedLanguage = it },
        )

        rule.onNodeWithTag("agent-name-field").performTextReplacement("Acorde")
        rule.onNodeWithTag("agent-tone-menu").performClick()
        rule.onNodeWithText("Analítico").performClick()
        rule.onNodeWithTag("agent-language-menu").performClick()
        rule.onNodeWithText("English").performClick()

        rule.onNodeWithText("Agent name").assertIsDisplayed()
        rule.onNodeWithText("Thoughtful responses with context and careful reasoning.").assertIsDisplayed()
        rule.runOnIdle {
            assertEquals("Acorde", savedAgent.name)
            assertEquals(AgentTone.Analytical, savedAgent.tone)
            assertEquals(RockyLanguage.English, savedLanguage)
        }
        capture("implementation-agent-english.png", "rocky-settings-window")
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
        rule.onNodeWithTag("agent-tone-menu").performClick()
        rule.onNodeWithText("Analítico").performClick()
        rule.onNodeWithText("Respostas ponderadas, com contexto e raciocínio cuidadoso.").assertExists()
        rule.runOnIdle {
            assertEquals("Acorde", saved.name)
            assertEquals(AgentTone.Analytical, saved.tone)
        }
        rule.onNodeWithText("concluir").performClick()
        rule.onNodeWithText("Acorde").assertExists()
    }

    @Test fun preservesAgentNameAcrossSettingsTabs() {
        render(settingsOpen = true, settingsSection = SettingsSection.Agent)
        rule.onNodeWithTag("agent-name-field").performTextReplacement("Acorde")
        rule.onNodeWithText("IA").performClick()
        rule.onAllNodesWithText("Agente")[0].performClick()
        rule.onNodeWithTag("agent-name-field").assertTextContains("Acorde")
    }

    @Test
    fun dataDeletionRequiresConfirmationAndPreservesNotesOnCancel() {
        val repository = TransientNoteRepository()
        repository.save(LiveNote("note", "Keep me", "now", "test"))
        render(settingsOpen = true, settingsSection = SettingsSection.Data, noteRepository = repository)
        rule.onNodeWithText("Apagar notas").performScrollTo().performClick()
        rule.onNodeWithText("Cancelar").performClick()
        assertEquals(1, repository.getAll().size)
        rule.onNodeWithText("Apagar notas").performScrollTo().performClick()
        rule.onNodeWithText("Confirmar").performClick()
        assertTrue(repository.getAll().isEmpty())
    }

    @Test
    fun guidesFirstUseThroughSettings() {
        render(
            firstUseOpen = true,
        )

        rule.onNodeWithText("Configure o Rocky").assertExists()
        rule.onNodeWithText("Configurar plataforma").performClick()
        rule.onNodeWithText("Twitch").assertExists()
        rule.onNodeWithText("concluir").performClick()
        rule.onNodeWithText("Configure o Rocky").assertExists()

        assertTrue(rule.onAllNodesWithText("Usar demonstração").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun rendersSettingsInAnExternalWindowHost() {
        render(
            settingsOpen = true,
            mainWindow = { content ->
                Box(Modifier.testTag("main-window")) { content() }
            },
            settingsWindow = { visible, _, content ->
                if (visible) Box(Modifier.testTag("settings-window")) { content() }
            },
        )

        rule.onNodeWithTag("main-window").assertExists()
        rule.onNodeWithTag("settings-window").assertExists()
        rule.onNodeWithText("Configurações").assertExists()
        rule.onNodeWithText("Conversa").assertExists()
    }

    @Test
    fun rendersAndFiltersTheMacOsSettingsSidebar() {
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Agent,
            windowWidth = 780.dp,
            windowHeight = 680.dp,
        )

        rule.onNodeWithText("Nome, idioma e como o Rocky se comunica.").assertExists()
        rule.onNodeWithText("Buscar").performTextReplacement("Dados")
        assertEquals(1, rule.onAllNodesWithText("Agente").fetchSemanticsNodes().size)
        rule.onAllNodesWithText("Dados")[1].performClick()
        rule.onNodeWithText("Privacidade e armazenamento").assertExists()
    }

    @Test
    fun changesInterfaceLanguage() {
        var savedLanguage = RockyLanguage.PortugueseBrazil
        render(
            settingsOpen = true,
            onLanguageChange = { savedLanguage = it },
        )

        rule.onNodeWithTag("agent-language-menu").performClick()
        rule.onNodeWithText("English").performClick()

        rule.onNodeWithText("Settings").assertExists()
        rule.onNodeWithText("Agent name").assertExists()
        rule.onNodeWithText("done").performClick()
        rule.onNodeWithText("Connect Twitch, Kick, YouTube, Facebook, or TikTok in settings to follow a stream.").assertExists()
        rule.onNodeWithText("Conversation").assertExists()
        rule.runOnIdle { assertEquals(RockyLanguage.English, savedLanguage) }
    }

    @Test
    fun showsFirstUseGuideInEnglish() {
        render(firstUseOpen = true, language = RockyLanguage.English)

        rule.onNodeWithText("Set up Rocky").assertExists()
        rule.onNodeWithText("1. Connect your platform").assertExists()
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


    private fun selectAiProvider(label: String) {
        rule.onNodeWithTag("ai-provider-menu").performScrollTo().performClick()
        rule.onNodeWithText(label).performClick()
    }

    @Test
    fun capturesAndConfiguresGroupedAiSettings() {
        val client = FakeAiSuggestionClient().apply { availableModelNames = listOf("demo-model") }
        render(
            settingsOpen = true,
            settingsSection = SettingsSection.Ai,
            windowWidth = 780.dp,
            windowHeight = 680.dp,
            mainWindow = {},
            settingsWindow = { visible, _, content -> if (visible) content() },
            aiSuggestionClient = client,
        )
        rule.onNodeWithTag("ai-provider-menu").assertIsDisplayed()
        rule.onNodeWithTag("ai-model").assertIsDisplayed()
        capture("implementation-ai-local.png", "rocky-settings-window")

        rule.onNodeWithText("Buscar modelos").performClick()
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodesWithText("Escolher modelo").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText("Escolher modelo").performClick()
        rule.onNodeWithText("demo-model").performClick()
        rule.onNodeWithText("demo-model").assertExists()
        rule.onNodeWithTag("ai-test-connection").performScrollTo().performClick()
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodesWithText("Conectado").fetchSemanticsNodes().isNotEmpty()
        }
        capture("implementation-ai-verified.png", "rocky-settings-window")

        selectAiProvider("OpenRouter")
        rule.onNodeWithTag("ai-api-key").performScrollTo().performTextReplacement("test-only-key")
        rule.onNodeWithContentDescription("Mostrar valor").performClick()
        rule.onNodeWithContentDescription("Ocultar valor").assertExists()
        rule.onNodeWithTag("ai-api-key").performTextReplacement("")
        rule.onNodeWithText("Salvar configuração e chave").performScrollTo()
        capture("implementation-ai-credentials.png", "rocky-settings-window")

        rule.onNodeWithTag("ai-advanced-connection").performScrollTo().performClick()
        rule.onNodeWithTag("ai-endpoint").performScrollTo().assertIsDisplayed()
        capture("implementation-ai-advanced.png", "rocky-settings-window")

        rule.onNodeWithTag("ai-profile-menu").performScrollTo().performClick()
        rule.onNodeWithText("Proativo").performClick()
        rule.onNodeWithTag("ai-context-filters").performScrollTo().performClick()
        rule.onNodeWithTag("ai-ignored-bots").performScrollTo().performTextReplacement("bot1,bot2")
        rule.onNodeWithText("Os filtros afetam IA e fila de perguntas; o chat exibido permanece intacto.").performScrollTo()
        capture("implementation-ai-context.png", "rocky-settings-window")
        rule.onNodeWithTag("automatic-analysis").performScrollTo()
        rule.onNodeWithText("Analisa o chat em lotes de três mensagens. Ao usar uma API, as mensagens selecionadas são enviadas ao provedor.").performScrollTo()
        capture("implementation-ai-automatic.png", "rocky-settings-window")
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
        rule.onNodeWithText("Conectar Twitch").assertIsDisplayed().performClick()
    }

    @Test
    fun configuresGroupedVoiceSettings() {
        val voice = FakeVoiceService().apply {
            systemVoices = listOf(SystemVoice("voice-1", "Luciana", "pt-BR"))
            microphones = listOf(AudioInputDevice("mic-1", "Microfone USB"))
        }
        var saved = VoiceConfiguration()
        render(settingsOpen = true, settingsSection = SettingsSection.Voice,
            voiceService = voice, onVoiceConfigurationChange = { saved = it },
            windowWidth = 780.dp, windowHeight = 680.dp, mainWindow = {},
            settingsWindow = { visible, _, content -> if (visible) content() })
        rule.waitUntil(5_000) { rule.onAllNodesWithText("Carregando…").fetchSemanticsNodes().isEmpty() }
        capture("implementation-voice-local.png", "rocky-settings-window")
        rule.onNodeWithTag("voice-system-menu").performClick()
        rule.onNodeWithText("Luciana · pt-BR").performClick()
        assertEquals("voice-1", saved.output.voiceId)
        rule.onNodeWithTag("voice-read-suggestions").performClick()
        assertEquals(true, saved.readSuggestions)
        rule.onNodeWithTag("test-voice").performScrollTo().performClick()
        rule.waitUntil(5_000) { voice.spoken.isNotEmpty() }
        rule.onNodeWithTag("voice-microphone-menu").performScrollTo().performClick()
        rule.onNodeWithText("Microfone USB").performClick()
        assertEquals("mic-1", saved.transcription.microphoneId)
        rule.onNodeWithTag("voice-advanced-paths").performScrollTo().performClick()
        rule.onNodeWithTag("whisper-executable").performScrollTo().performTextReplacement("whisper-cli")
        rule.onNodeWithTag("whisper-model").performScrollTo().performTextReplacement("model.bin")
        assertEquals("model.bin", saved.transcription.modelPath)
        capture("implementation-voice-recognition.png", "rocky-settings-window")
        rule.onNodeWithTag("voice-end-detection").performScrollTo().performClick()
        assertEquals(false, saved.detectEndOfSpeech)
        assertTrue(rule.onAllNodesWithText("Limiar de ruído").fetchSemanticsNodes().isEmpty())
        rule.onNodeWithTag("voice-end-detection").performClick()
        rule.onNodeWithText("Limiar de ruído").performScrollTo().assertIsDisplayed()
        rule.onNodeWithTag("voice-calibrate").performScrollTo()
        capture("implementation-voice-detection.png", "rocky-settings-window")
        rule.onNodeWithTag("voice-audio-timings").performScrollTo().performClick()
        rule.onNodeWithText("Captura: 0 ms · Transcrição: 0 ms").assertExists()
        rule.onNodeWithText("Captura: 0 ms · Transcrição: 0 ms").performScrollTo()
        capture("implementation-voice-diagnostics.png", "rocky-settings-window")
        rule.onNodeWithTag("voice-provider-menu").performScrollTo().performClick()
        rule.onNodeWithText("ElevenLabs").performClick()
        rule.onNodeWithTag("elevenlabs-api-key").performScrollTo().assertIsDisplayed()
        rule.onNodeWithTag("elevenlabs-voice-id").performScrollTo().performTextReplacement("custom-voice")
        assertEquals("custom-voice", saved.output.elevenLabs.voiceId)
        rule.onNodeWithTag("elevenlabs-model-id").performScrollTo().performTextReplacement("custom-model")
        assertEquals("custom-model", saved.output.elevenLabs.modelId)
        rule.onNodeWithTag("elevenlabs-fallback").performScrollTo().performClick()
        assertEquals(true, saved.output.elevenLabs.fallbackToSystem)
        rule.onNodeWithText("Provedor e conexão").performScrollTo()
        capture("implementation-voice-cloud.png", "rocky-settings-window")
    }

    @Test
    fun expandsAdvancedVoiceSettingsAtCompactWidth() {
        render(settingsOpen = true, settingsSection = SettingsSection.Voice)
        rule.onNodeWithTag("voice-advanced-paths").performScrollTo().performClick()
        rule.onNodeWithTag("whisper-model").performScrollTo().assertIsDisplayed()
        capture("implementation-voice-compact-recognition.png")
        rule.onNodeWithTag("voice-audio-timings").performScrollTo().performClick()
        rule.onNodeWithText("Captura: 0 ms · Transcrição: 0 ms").performScrollTo().assertIsDisplayed()
        capture("implementation-voice-compact-diagnostics.png")
    }

    @Test
    fun capturesGroupedPlatformSettings() {
        val twitch = FakeTwitchChatClient()
        render(settingsOpen = true, settingsSection = SettingsSection.Platforms,
            twitchClientId = "client-id", twitchChatClient = twitch,
            windowWidth = 780.dp, windowHeight = 680.dp, mainWindow = {},
            settingsWindow = { visible, _, content -> if (visible) content() })
        capture("implementation-platforms-wide.png", "rocky-settings-window")
        rule.onNodeWithText("Avançado · aplicativo Twitch").performClick()
        rule.onNodeWithTag("twitch-client-id").assertIsDisplayed()
        capture("implementation-platforms-twitch-advanced.png", "rocky-settings-window")
        rule.onNodeWithText("Avançado · aplicativo Twitch").performClick()
        rule.onNodeWithText("Conectar Twitch").performClick()
        rule.runOnIdle { twitch.emit(TwitchConnectionEvent.AuthorizationRequired("ABCD-1234", "https://example.test")) }
        rule.onNodeWithTag("twitch-open-browser").assertIsDisplayed()
        capture("implementation-platforms-authorization.png", "rocky-settings-window")
        rule.onNodeWithText("Kick").performScrollTo().assertIsDisplayed()
        rule.onNodeWithText("Em breve").assertIsDisplayed()
        capture("implementation-platforms-kick-upcoming.png", "rocky-settings-window")
        rule.onNodeWithTag("youtube-client-secret").performScrollTo().assertIsDisplayed()
        capture("implementation-platforms-youtube.png", "rocky-settings-window")
        rule.onNodeWithTag("facebook-app-secret").performScrollTo().assertIsDisplayed()
        capture("implementation-platforms-facebook.png", "rocky-settings-window")
        rule.onNodeWithText("Conectar TikTok").performScrollTo().assertIsDisplayed()
        capture("implementation-platforms-tiktok.png", "rocky-settings-window")
    }

    @Test
    fun capturesCompactPlatformCredentials() {
        render(settingsOpen = true, settingsSection = SettingsSection.Platforms)
        capture("implementation-platforms-compact.png")
        rule.onNodeWithText("Kick").performScrollTo().assertIsDisplayed()
        rule.onNodeWithText("Em breve").assertIsDisplayed()
        capture("implementation-platforms-compact-kick-upcoming.png")
        rule.onNodeWithTag("facebook-app-secret").performScrollTo().assertIsDisplayed()
        capture("implementation-platforms-compact-facebook.png")
        rule.onNodeWithText("Conectar TikTok").performScrollTo().assertIsDisplayed()
        capture("implementation-platforms-compact-tiktok.png")
    }

    @Test
    fun resetsScrollWhenSwitchingToPlatforms() {
        render(settingsOpen = true, settingsSection = SettingsSection.Ai, twitchClientId = "client-id")
        rule.onNodeWithTag("automatic-analysis").performScrollTo()
        rule.onNodeWithText("Plataformas").performClick()
        rule.onNodeWithText("Conectar Twitch").assertIsDisplayed()
        capture("implementation-platforms-after-tab-change.png")
    }

    @Test
    fun capturesAndExercisesGroupedDataSettings() {
        val repository = TransientNoteRepository()
        repository.save(LiveNote("existing", "Registro salvo", "now", "test"))
        var backup = emptyList<LiveNote>()
        var diagnostics = ""
        var startupUpdates = false
        var resetCount = 0
        var removeCount = 0
        var openedDirectory = false
        render(settingsOpen = true, settingsSection = SettingsSection.Data, noteRepository = repository,
            onBackup = { backup = it; true },
            onChooseImport = { listOf(LiveNote("incoming", "Registro importado", "now", "test")) },
            onExportDiagnostic = { diagnostics = it; true },
            onCheckUpdatesOnStartChange = { startupUpdates = it },
            onResetSettings = { resetCount++ }, onRemoveManagedVoiceModel = { removeCount++ },
            onOpenDataDirectory = { openedDirectory = true },
            windowWidth = 780.dp, windowHeight = 680.dp, mainWindow = {},
            settingsWindow = { visible, _, content -> if (visible) content() })
        capture("implementation-data-wide.png", "rocky-settings-window")
        rule.onNodeWithText("Abrir pasta de dados").performClick()
        assertTrue(openedDirectory)
        rule.onNodeWithText("Exportar backup").performScrollTo().performClick()
        assertEquals("existing", backup.single().id)
        rule.onNodeWithText("Backup salvo.").assertExists()
        rule.onNodeWithText("Importar backup").performScrollTo().performClick()
        rule.onNodeWithText("Cancelar").performClick()
        assertEquals(1, repository.getAll().size)
        rule.onNodeWithText("Importar backup").performScrollTo().performClick()
        rule.onNodeWithText("Importar").performClick()
        assertEquals(2, repository.getAll().size)
        rule.onNodeWithText("Verificar atualizações").performScrollTo().performClick()
        rule.waitUntil(5_000) { rule.onAllNodesWithText("Nenhuma versão mais recente no seu canal.").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithTag("data-startup-updates").performScrollTo().performClick()
        assertTrue(startupUpdates)
        capture("implementation-data-updates.png", "rocky-settings-window")
        rule.onNodeWithText("Prévia do diagnóstico").performScrollTo().performClick()
        rule.onNodeWithText("Diagnóstico local").assertIsDisplayed()
        rule.onNodeWithText("Exportar").performClick()
        assertTrue(diagnostics.contains("Saved records: 2"))
        rule.onNodeWithText("Diagnóstico salvo.").assertExists()
        rule.onNodeWithText("Gerenciamento de dados").performScrollTo()
        rule.onNodeWithText("Remover modelo de voz").performScrollTo()
        capture("implementation-data-management.png", "rocky-settings-window")
        rule.onNodeWithText("Redefinir configurações").performScrollTo().performClick()
        capture("implementation-data-confirmation.png", "data-confirmation")
        rule.onNodeWithText("Cancelar").performClick()
        assertEquals(0, resetCount)
        rule.onNodeWithText("Remover modelo de voz").performScrollTo().performClick()
        rule.onNodeWithText("Cancelar").performClick()
        assertEquals(0, removeCount)
        rule.onNodeWithText("Remover modelo de voz").performScrollTo().performClick()
        rule.onNodeWithText("Confirmar").performClick()
        assertEquals(1, removeCount)
        assertEquals(2, repository.getAll().size)
        rule.onNodeWithText("Redefinir configurações").performScrollTo().performClick()
        rule.onNodeWithText("Confirmar").performClick()
        assertEquals(1, resetCount)
        assertEquals(2, repository.getAll().size)
    }

    @Test
    fun capturesCompactDataSettingsAndDisabledDeletion() {
        render(settingsOpen = true, settingsSection = SettingsSection.Data)
        capture("implementation-data-compact.png")
        rule.onNodeWithText("Apagar notas").performScrollTo().assertIsNotEnabled()
        rule.onNodeWithText("Remover modelo de voz").performScrollTo()
        capture("implementation-data-compact-management.png")
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
        kickChatClient: KickChatClient? = null,
        kickConfiguration: KickConfiguration = KickConfiguration(),
        youtubeChatClient: YouTubeChatClient? = null,
        youtubeConfiguration: YouTubeConfiguration = YouTubeConfiguration(),
        facebookChatClient: FacebookChatClient? = null,
        facebookConfiguration: FacebookConfiguration = FacebookConfiguration(),
        tiktokChatClient: TikTokChatClient? = null,
        tiktokConfiguration: TikTokConfiguration = TikTokConfiguration(),
        aiSuggestionClient: AiSuggestionClient? = null,
        aiConfiguration: AiProviderConfiguration = AiProviderConfiguration(AiProviderKind.Ollama, "http://localhost:11434", "test"),
        voiceService: VoiceService = FakeVoiceService(),
        voiceConfiguration: VoiceConfiguration = VoiceConfiguration(),
        onBackup: (List<LiveNote>) -> Boolean = { false },
        onChooseImport: () -> List<LiveNote>? = { null },
        onExportDiagnostic: (String) -> Boolean = { false },
        onCheckUpdatesOnStartChange: (Boolean) -> Unit = {},
        onResetSettings: () -> Unit = {},
        onRemoveManagedVoiceModel: () -> Unit = {},
        onOpenDataDirectory: () -> Unit = {},
        onVoiceConfigurationChange: (VoiceConfiguration) -> Unit = {},
        onAgentConfigurationChange: (AgentConfiguration) -> Unit = {},
        firstUseOpen: Boolean = false,
        onFirstUseFinished: () -> Unit = {},
        onLanguageChange: (RockyLanguage) -> Unit = {},
        language: RockyLanguage = RockyLanguage.PortugueseBrazil,
        mainWindow: RockyMainWindowHost? = null,
        settingsWindow: RockySettingsWindowHost? = null,
        windowWidth: Dp = 462.dp,
        windowHeight: Dp = 820.dp,
    ) {
        rule.setContent {
            key(mainSection, settingsOpen, settingsSection, firstUseOpen) {
                var showingSettings by remember { mutableStateOf(settingsOpen) }
                Box(Modifier.size(windowWidth, windowHeight)) {
                    RockyWindow(
                        compact = compact,
                        pinned = false,
                        onTogglePinned = {},
                        onToggleCompact = {},
                        noteRepository = noteRepository,
                        twitchChatClient = twitchChatClient ?: FakeTwitchChatClient(),
                        kickChatClient = kickChatClient ?: FakeKickChatClient(),
                        youtubeChatClient = youtubeChatClient ?: FakeYouTubeChatClient(),
                        facebookChatClient = facebookChatClient ?: FakeFacebookChatClient(),
                        tiktokChatClient = tiktokChatClient ?: FakeTikTokChatClient(),
                        aiSuggestionClient = aiSuggestionClient ?: FakeAiSuggestionClient(),
                        initialAiConfiguration = aiConfiguration,
                        voiceService = voiceService,
                        initialVoiceConfiguration = voiceConfiguration,
                        onVoiceConfigurationChange = onVoiceConfigurationChange,
                        onAgentConfigurationChange = onAgentConfigurationChange,
                        initialTwitchClientId = twitchClientId,
                        initialKickConfiguration = kickConfiguration,
                        initialYouTubeConfiguration = youtubeConfiguration,
                        initialFacebookConfiguration = facebookConfiguration,
                        initialTikTokConfiguration = tiktokConfiguration,
                        onExportNotes = onExportNotes,
                        onExportIdeas = onExportIdeas,
                        onBackup = onBackup,
                        onChooseImport = onChooseImport,
                        onExportDiagnostic = onExportDiagnostic,
                        onCheckUpdatesOnStartChange = onCheckUpdatesOnStartChange,
                        onResetSettings = onResetSettings,
                        onRemoveManagedVoiceModel = onRemoveManagedVoiceModel,
                        onOpenDataDirectory = onOpenDataDirectory,
                        dataDirectoryLabel = "/Users/example/Library/Application Support/Rocky",
                        initialMainSectionIndex = mainSection.ordinal,
                        initialSettingsOpen = settingsOpen,
                        onSettingsVisibilityChanged = { showingSettings = it },
                        initialSettingsSectionIndex = settingsSection.ordinal,
                        initialFirstUseOpen = firstUseOpen,
                        onFirstUseFinished = onFirstUseFinished,
                        initialLanguage = language,
                        onLanguageChange = onLanguageChange,
                        mainWindow = mainWindow,
                        settingsWindow = settingsWindow,
                    )
                }
            }
        }
    }

    private fun capture(fileName: String, tag: String = "rocky-window") {
        val outputDirectory = System.getenv("ROCKY_SCREENSHOT_DIR")?.let(Path::of) ?: return
        rule.waitForIdle()
        val bitmap = rule.onNodeWithTag(tag).captureToImage().asSkiaBitmap()
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

    private class FakeKickChatClient : KickChatClient {
        private var listener = KickConnectionListener {}
        var lastConfiguration: KickConfiguration? = null
        override fun connect(configuration: KickConfiguration, listener: KickConnectionListener) {
            lastConfiguration = configuration
            this.listener = listener
        }
        override fun disconnect() = Unit
        override fun close() = Unit
        fun emit(event: KickConnectionEvent) = listener.onEvent(event)
    }

    private class FakeYouTubeChatClient : YouTubeChatClient {
        private var listener = YouTubeConnectionListener {}
        override fun connect(configuration: YouTubeConfiguration, listener: YouTubeConnectionListener) {
            this.listener = listener
        }
        override fun disconnect() = Unit
        override fun close() = Unit
        fun emit(event: YouTubeConnectionEvent) = listener.onEvent(event)
    }

    private class FakeFacebookChatClient : FacebookChatClient {
        private var listener = FacebookConnectionListener {}
        override fun connect(configuration: FacebookConfiguration, listener: FacebookConnectionListener) {
            this.listener = listener
        }
        override fun disconnect() = Unit
        override fun close() = Unit
        fun emit(event: FacebookConnectionEvent) = listener.onEvent(event)
    }

    private class FakeTikTokChatClient : TikTokChatClient {
        private var listener = TikTokConnectionListener {}
        override fun connect(configuration: TikTokConfiguration, listener: TikTokConnectionListener) {
            this.listener = listener
        }
        override fun disconnect() = Unit
        override fun close() = Unit
        fun emit(event: TikTokConnectionEvent) = listener.onEvent(event)
    }

    private class FakeAiSuggestionClient : AiSuggestionClient {
        var availableModelNames = emptyList<String>()
        override fun availableModels(configuration: AiProviderConfiguration) = availableModelNames
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
        var systemVoices = emptyList<SystemVoice>()
        var microphones = emptyList<AudioInputDevice>()
        override fun availableVoices(): List<SystemVoice> = systemVoices
        override fun availableMicrophones(): List<AudioInputDevice> = microphones
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
