package dev.rocky.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import java.nio.file.Files
import java.nio.file.Path
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.junit.Rule
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
        waitForSuggestion()
        rule.onNodeWithText("Também queria saber o valor.").assertExists()
        capture("implementation-main.png")

        val mainSections = mapOf(
            MainSection.Support to "ju.lia",
            MainSection.Notes to "Prometeu mostrar o raio-x do repositório antes de encerrar.",
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
            SettingsSection.Ai to "Criatividade",
            SettingsSection.Voice to "Velocidade",
            SettingsSection.Platforms to "Contas conectadas",
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
        waitForSuggestion()

        rule.onNodeWithText("Salvar como nota").performClick()

        rule.onNodeWithText("Nota salva").assertExists()
        rule.onNodeWithText("SUGESTÃO").assertExists()
    }

    private fun render(
        mainSection: MainSection = MainSection.Conversation,
        settingsOpen: Boolean = false,
        settingsSection: SettingsSection = SettingsSection.Agent,
    ) {
        rule.setContent {
            key(mainSection, settingsOpen, settingsSection) {
                Box(Modifier.size(420.dp, if (settingsOpen) 520.dp else 720.dp)) {
                    RockyWindow(
                        compact = false,
                        pinned = false,
                        onClose = {},
                        onMinimize = {},
                        onTogglePinned = {},
                        onToggleCompact = {},
                        initialMainSectionIndex = mainSection.ordinal,
                        initialSettingsOpen = settingsOpen,
                        initialSettingsSectionIndex = settingsSection.ordinal,
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
}
