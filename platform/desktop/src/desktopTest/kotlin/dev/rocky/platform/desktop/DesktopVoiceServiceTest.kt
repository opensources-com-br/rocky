package dev.rocky.platform.desktop

import dev.rocky.core.voice.LocalTranscriptionConfiguration
import dev.rocky.core.voice.VoiceOutputConfiguration
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DesktopVoiceServiceTest {
    @Test
    fun parsesMacSystemVoices() {
        val voice = DesktopVoiceService.parseMacVoice("Luciana              pt_BR    # Olá! Eu sou Luciana.")

        assertEquals("Luciana", voice?.id)
        assertEquals("pt_BR", voice?.language)
    }

    @Test
    fun keepsSpeechTextAsAnIndependentProcessArgument() {
        val text = "Preço; ${'$'}(comando)"
        val command = DesktopVoiceService.windowsSpeechCommand(
            text,
            VoiceOutputConfiguration("Maria", speedPercent = 120, volumePercent = 70),
        )

        assertEquals(text, command.last())
        assertEquals("Maria", command[5])
        assertTrue(command[4].contains("System.Speech"))
    }

    @Test
    fun buildsWhisperCommandWithPathsAsIndependentArguments() {
        val command = DesktopVoiceService.whisperCommand(
            LocalTranscriptionConfiguration("C:/Rocky Tools/whisper-cli.exe", "C:/Models/ggml-base.bin"),
            Path.of("C:/Temp/voice.wav"),
            Path.of("C:/Temp/transcript"),
        )

        assertEquals("C:/Rocky Tools/whisper-cli.exe", command.first())
        assertEquals("C:/Models/ggml-base.bin", command[2])
        assertEquals(Path.of("C:/Temp/voice.wav").toString(), command[4])
        assertEquals(
            listOf("-nt", "-otxt", "-of", Path.of("C:/Temp/transcript").toString()),
            command.takeLast(4),
        )
    }

    @Test
    fun stopsAProcessAfterItsTimeout() {
        val javaExecutable = Path.of(
            System.getProperty("java.home"),
            "bin",
            if (System.getProperty("os.name").startsWith("Windows")) "java.exe" else "java",
        )
        val source = createTempFile(suffix = ".java")
        try {
            source.writeText(
                "class RockyWait { public static void main(String[] args) throws Exception { Thread.sleep(10000); } }",
            )
            val process = ProcessBuilder(javaExecutable.toString(), source.toString()).start()

            assertTrue(!DesktopVoiceService.waitForProcess(process, 50, TimeUnit.MILLISECONDS))
            process.waitFor(1, TimeUnit.SECONDS)
            assertTrue(!process.isAlive)
        } finally {
            java.nio.file.Files.deleteIfExists(source)
        }
    }
}
