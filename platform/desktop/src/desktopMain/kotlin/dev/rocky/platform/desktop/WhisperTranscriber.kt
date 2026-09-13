package dev.rocky.platform.desktop

import dev.rocky.core.voice.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

internal class WhisperTranscriber : SpeechTranscriber {
    private val transcriptionLock = Any()
    @Volatile private var transcriptionProcess: Process? = null
    @Volatile private var transcriptionCancelled = false
    fun begin() { synchronized(transcriptionLock) { transcriptionCancelled = false } }
    override fun cancel() {
        val process = synchronized(transcriptionLock) {
            transcriptionCancelled = true
            transcriptionProcess.also { transcriptionProcess = null }
        }
        process?.destroyForcibly()
    }
    override fun transcribe(audio: ByteArray, configuration: LocalTranscriptionConfiguration): String {
        require(audio.size >= 3_200) { "A gravação ficou curta demais para transcrever" }
        require(Files.isRegularFile(Path.of(configuration.executablePath))) { "Selecione o executável whisper-cli" }
        require(Files.isRegularFile(Path.of(configuration.modelPath))) { "Selecione um modelo GGML do Whisper" }
        val wav = Files.createTempFile("rocky-command-", ".wav")
        val outputBase = wav.resolveSibling(wav.fileName.toString().removeSuffix(".wav") + "-transcript")
        val transcript = Path.of("$outputBase.txt")
        val processOutput = Files.createTempFile("rocky-whisper-", ".log")
        var process: Process? = null
        try {
            DesktopVoiceService.writeWav(wav, audio)
            process = synchronized(transcriptionLock) {
                check(!transcriptionCancelled) { "A transcrição foi cancelada" }
                ProcessBuilder(DesktopVoiceService.whisperCommand(configuration, wav, outputBase))
                    .redirectErrorStream(true)
                    .redirectOutput(processOutput.toFile())
                    .start()
                    .also { transcriptionProcess = it }
            }
            if (!DesktopVoiceService.waitForProcess(process, 2L, TimeUnit.MINUTES)) {
                error("A transcrição excedeu o limite de tempo")
            }
            val output = Files.readString(processOutput)
            check(process.exitValue() == 0) {
                output.lineSequence().lastOrNull { it.isNotBlank() } ?: "Falha ao executar whisper.cpp"
            }
            return Files.readString(transcript).trim().also {
                require(it.isNotBlank()) { "Nenhuma fala foi reconhecida" }
            }
        } finally {
            synchronized(transcriptionLock) {
                if (transcriptionProcess === process) transcriptionProcess = null
            }
            process?.takeIf { it.isAlive }?.let({ it.destroyForcibly() })
            Files.deleteIfExists(wav)
            Files.deleteIfExists(transcript)
            Files.deleteIfExists(processOutput)
        }
    }

}
