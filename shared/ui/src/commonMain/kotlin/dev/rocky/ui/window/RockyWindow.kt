package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.rocky.ui.theme.RockyColors
import dev.rocky.ui.theme.RockyTheme

@Composable
fun RockyWindow(
    compact: Boolean,
    pinned: Boolean,
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onTogglePinned: () -> Unit,
    onToggleCompact: () -> Unit,
    onSettingsVisibilityChanged: (Boolean) -> Unit = {},
    initialMainSectionIndex: Int = 0,
    initialSettingsOpen: Boolean = false,
    initialSettingsSectionIndex: Int = 0,
) {
    RockyTheme {
        var settingsOpen by remember { mutableStateOf(initialSettingsOpen) }
        var mainSection by remember {
            mutableStateOf(MainSection.entries[initialMainSectionIndex])
        }
        var settingsSection by remember {
            mutableStateOf(SettingsSection.entries[initialSettingsSectionIndex])
        }
        var promptIndex by remember { mutableIntStateOf(0) }
        var silenced by remember { mutableStateOf(false) }
        var talking by remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier.fillMaxSize().testTag("rocky-window"),
            color = RockyColors.Background,
        ) {
            Column {
                RockyHeader(
                    compact = compact,
                    pinned = pinned,
                    onClose = onClose,
                    onMinimize = onMinimize,
                    onTogglePinned = onTogglePinned,
                    onToggleCompact = onToggleCompact,
                    onOpenSettings = {
                        settingsOpen = true
                        onSettingsVisibilityChanged(true)
                    },
                )
                Divider(color = RockyColors.Divider)
                when {
                    compact -> CompactContent()
                    settingsOpen -> {
                        SettingsHeading(
                            onDone = {
                                settingsOpen = false
                                onSettingsVisibilityChanged(false)
                            },
                        )
                        SettingsNavigation(settingsSection) { settingsSection = it }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            when (settingsSection) {
                                SettingsSection.Agent -> AgentSettings()
                                SettingsSection.Ai -> AiSettings()
                                SettingsSection.Voice -> VoiceSettings()
                                SettingsSection.Platforms -> PlatformSettings(samplePlatforms)
                            }
                        }
                    }
                    else -> {
                        PlatformStrip(samplePlatforms)
                        Divider(color = RockyColors.Divider)
                        LiveSummary(
                            message = prompts[promptIndex],
                            silenced = silenced,
                            onSaveNote = { mainSection = MainSection.Notes },
                            onNext = { promptIndex = (promptIndex + 1) % prompts.size },
                            onSilence = { silenced = !silenced },
                        )
                        MainNavigation(mainSection) { mainSection = it }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            when (mainSection) {
                                MainSection.Conversation -> ConversationContent()
                                MainSection.Support -> SupportContent()
                                MainSection.Notes,
                                MainSection.Ideas -> TimelineContent(mainSection)
                                MainSection.Pulse -> PulseContent(samplePlatforms)
                            }
                        }
                        Divider(color = RockyColors.Divider)
                        AssistantFooter(active = talking) { talking = !talking }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactContent() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)) {
        Text(
            text = "7 perguntas sobre o preço do curso",
            style = MaterialTheme.typography.subtitle1,
            color = RockyColors.TextPrimary,
        )
        Text(
            text = "4 Twitch · 2 YouTube · 1 Kick",
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.caption,
            color = RockyColors.TextSecondary,
        )
    }
}

private val prompts = listOf(
    "Sete pessoas perguntaram o preço do curso nos últimos dois minutos. Vale responder agora.",
    "O chat quer rever o comando final do deploy. Pode ser uma boa hora para repetir.",
    "Há uma dúvida recorrente sobre compatibilidade com Next.js esperando resposta.",
)
