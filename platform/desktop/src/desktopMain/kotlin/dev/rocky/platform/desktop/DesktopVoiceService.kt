package dev.rocky.platform.desktop

import dev.rocky.core.voice.AudioInputDevice
import dev.rocky.core.voice.LocalTranscriptionConfiguration
import dev.rocky.core.voice.SystemVoice
import dev.rocky.core.voice.VoiceOutputConfiguration
import dev.rocky.core.voice.VoiceService
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit
import java.util.Base64
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

class DesktopVoiceService : VoiceService {
    private val operatingSystem = System.getProperty("os.name").lowercase()
    private val captureLock = Any()
    private val speechLock = Any()
    private val transcriptionLock = Any()

    @Volatile
    private var speechProcess: Process? = null

    @Volatile
    private var captureLine: TargetDataLine? = null

    @Volatile
    private var captureThread: Thread? = null

    @Volatile
    private var currentInputLevel = 0f

    @Volatile
    private var transcriptionProcess: Process? = null

    @Volatile
    private var transcriptionCancelled = false

    private var capturedAudio: ByteArrayOutputStream? = null

    override val automaticTranscriptionSetupSupported: Boolean
        get() = operatingSystem.contains("mac") && homebrewExecutable() != null

    override fun availableVoices(): List<SystemVoice> = runCatching {
        when {
            operatingSystem.contains("mac") -> macVoices()
            operatingSystem.contains("win") -> windowsVoices()
            else -> emptyList()
        }
    }.getOrDefault(emptyList())

    override fun availableMicrophones(): List<AudioInputDevice> = AudioSystem.getMixerInfo().mapNotNull { info ->
        val mixer = AudioSystem.getMixer(info)
        val supportsCapture = mixer.targetLineInfo.any { lineInfo ->
            lineInfo is DataLine.Info && TargetDataLine::class.java.isAssignableFrom(lineInfo.lineClass)
        }
        AudioInputDevice(info.name, info.name).takeIf { supportsCapture }
    }.distinctBy(AudioInputDevice::id)

    override fun speak(text: String, configuration: VoiceOutputConfiguration) {
        require(text.isNotBlank()) { "Speech text cannot be empty" }
        val process = synchronized(speechLock) {
            speechProcess?.let(::stopProcess)
            when {
                operatingSystem.contains("mac") -> ProcessBuilder(macSpeechCommand(text, configuration)).start()
                operatingSystem.contains("win") -> ProcessBuilder(windowsSpeechCommand(text, configuration)).start()
                else -> error("System speech is unavailable on this operating system")
            }.also { speechProcess = it }
        }
        val completed = waitForProcess(process, SPEECH_TIMEOUT_MINUTES, TimeUnit.MINUTES)
        synchronized(speechLock) {
            if (speechProcess === process) {
                speechProcess = null
                check(completed && process.exitValue() == 0) { "System speech failed" }
            }
        }
    }

    override fun stopSpeaking() {
        val process = synchronized(speechLock) {
            speechProcess.also { speechProcess = null }
        }
        process?.let(::stopProcess)
    }

    override fun startCapture(microphoneId: String?) {
        synchronized(transcriptionLock) {
            check(transcriptionProcess == null) { "A transcription is already active" }
            transcriptionCancelled = false
        }
        synchronized(captureLock) {
            check(captureLine == null) { "Microphone capture is already active" }
            val format = captureFormat()
            val info = DataLine.Info(TargetDataLine::class.java, format)
            val line = microphoneId
                ?.let { id -> AudioSystem.getMixerInfo().firstOrNull { it.name == id } }
                ?.let(AudioSystem::getMixer)
                ?.getLine(info) as? TargetDataLine
                ?: AudioSystem.getLine(info) as TargetDataLine
            val output = ByteArrayOutputStream()
            line.open(format)
            line.start()
            currentInputLevel = 0f
            captureLine = line
            capturedAudio = output
            captureThread = Thread({ capture(line, output) }, "rocky-microphone-capture").apply {
                isDaemon = true
                start()
            }
        }
    }

    override fun inputLevel(): Float = currentInputLevel

    override fun prepareTranscription(onProgress: (String) -> Unit): LocalTranscriptionConfiguration {
        check(operatingSystem.contains("mac")) { "A configuração automática ainda está disponível apenas no macOS" }
        val brew = requireNotNull(homebrewExecutable()) { "Instale o Homebrew para configurar o reconhecimento automaticamente" }
        onProgress("Instalando o mecanismo de reconhecimento…")
        runSetupCommand(listOf(brew.toString(), "install", "whisper-cpp"))
        val whisper = brew.parent.resolve("whisper-cli")
        check(Files.isRegularFile(whisper)) { "O whisper-cli não foi encontrado após a instalação" }

        val voiceDirectory = RockyDesktopPaths.voiceDirectory
        Files.createDirectories(voiceDirectory)
        val model = voiceDirectory.resolve(MANAGED_MODEL_NAME)
        if (!Files.isRegularFile(model) || Files.size(model) < MINIMUM_MODEL_BYTES) {
            onProgress("Baixando o modelo de voz…")
            val partial = voiceDirectory.resolve("$MANAGED_MODEL_NAME.part")
            MODEL_URL.openStream().use { input ->
                Files.copy(input, partial, StandardCopyOption.REPLACE_EXISTING)
            }
            check(Files.size(partial) >= MINIMUM_MODEL_BYTES) { "O modelo de voz baixado está incompleto" }
            Files.move(partial, model, StandardCopyOption.REPLACE_EXISTING)
        }
        onProgress("Reconhecimento de voz pronto")
        return LocalTranscriptionConfiguration(whisper.toString(), model.toString())
    }

    override fun detectedTranscription(): LocalTranscriptionConfiguration? {
        val brew = homebrewExecutable() ?: return null
        val whisper = brew.parent.resolve("whisper-cli")
        val model = RockyDesktopPaths.voiceDirectory.resolve(MANAGED_MODEL_NAME)
        return if (Files.isRegularFile(whisper) && Files.isRegularFile(model) && Files.size(model) >= MINIMUM_MODEL_BYTES) {
            LocalTranscriptionConfiguration(whisper.toString(), model.toString())
        } else null
    }

    override fun stopCaptureAndTranscribe(configuration: LocalTranscriptionConfiguration): String {
        val audio = finishCapture()
        require(audio.size >= MINIMUM_AUDIO_BYTES) { "A gravação ficou curta demais para transcrever" }
        validateTranscriptionConfiguration(configuration)
        val wav = Files.createTempFile("rocky-command-", ".wav")
        val outputBase = wav.resolveSibling(wav.fileName.toString().removeSuffix(".wav") + "-transcript")
        val transcript = Path.of("$outputBase.txt")
        val processOutput = Files.createTempFile("rocky-whisper-", ".log")
        var process: Process? = null
        try {
            writeWav(wav, audio)
            process = synchronized(transcriptionLock) {
                check(!transcriptionCancelled) { "A transcrição foi cancelada" }
                ProcessBuilder(whisperCommand(configuration, wav, outputBase))
                    .redirectErrorStream(true)
                    .redirectOutput(processOutput.toFile())
                    .start()
                    .also { transcriptionProcess = it }
            }
            if (!waitForProcess(process, TRANSCRIPTION_TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
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
            process?.takeIf { it.isAlive }?.let(::stopProcess)
            Files.deleteIfExists(wav)
            Files.deleteIfExists(transcript)
            Files.deleteIfExists(processOutput)
        }
    }

    override fun cancelCapture() {
        runCatching { finishCapture() }
        val process = synchronized(transcriptionLock) {
            transcriptionCancelled = true
            transcriptionProcess.also { transcriptionProcess = null }
        }
        process?.let(::stopProcess)
    }

    override fun close() {
        stopSpeaking()
        cancelCapture()
    }

    private fun capture(line: TargetDataLine, output: ByteArrayOutputStream) {
        val buffer = ByteArray(CAPTURE_BUFFER_BYTES)
        while (line.isOpen && output.size() < MAXIMUM_AUDIO_BYTES) {
            val count = runCatching { line.read(buffer, 0, buffer.size) }.getOrDefault(-1)
            if (count <= 0) break
            output.write(buffer, 0, count)
            currentInputLevel = pcmLevel(buffer, count)
        }
    }

    private fun finishCapture(): ByteArray = synchronized(captureLock) {
        val line = captureLine ?: error("Microphone capture is not active")
        line.stop()
        line.close()
        captureThread?.join(CAPTURE_JOIN_TIMEOUT_MILLIS)
        captureLine = null
        captureThread = null
        currentInputLevel = 0f
        (capturedAudio?.toByteArray() ?: byteArrayOf()).also { capturedAudio = null }
    }

    private fun macVoices(): List<SystemVoice> {
        val output = runCommand(listOf("say", "-v", "?"))
        return output.lineSequence().mapNotNull(::parseMacVoice).toList()
    }

    private fun windowsVoices(): List<SystemVoice> {
        val script = "Add-Type -AssemblyName System.Speech; " +
            "(New-Object System.Speech.Synthesis.SpeechSynthesizer).GetInstalledVoices() | " +
            "ForEach-Object { \"${'$'}(${'$'}_.VoiceInfo.Name)|${'$'}(${'$'}_.VoiceInfo.Culture.Name)\" }"
        return runCommand(listOf("powershell.exe", "-NoProfile", "-NonInteractive", "-Command", script))
            .lineSequence()
            .mapNotNull { line ->
                val parts = line.trim().split('|', limit = 2)
                parts.firstOrNull()?.takeIf(String::isNotBlank)?.let { SystemVoice(it, it, parts.getOrNull(1)) }
            }
            .toList()
    }

    private fun runCommand(command: List<String>): String {
        val process = ProcessBuilder(command).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().use { it.readText() }
        check(process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS) && process.exitValue() == 0)
        return output
    }

    private fun homebrewExecutable(): Path? = listOf(
        Path.of("/opt/homebrew/bin/brew"),
        Path.of("/usr/local/bin/brew"),
    ).firstOrNull(Files::isExecutable)

    private fun runSetupCommand(command: List<String>) {
        val builder = ProcessBuilder(command)
            .redirectErrorStream(true)
            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
        builder.environment()["HOMEBREW_NO_AUTO_UPDATE"] = "1"
        builder.environment()["HOMEBREW_NO_INSTALL_CLEANUP"] = "1"
        val process = builder.start()
        check(waitForProcess(process, SETUP_TIMEOUT_MINUTES, TimeUnit.MINUTES) && process.exitValue() == 0) {
            "Não foi possível instalar o mecanismo de reconhecimento"
        }
    }

    companion object {
        internal fun parseMacVoice(line: String): SystemVoice? {
            val match = MAC_VOICE_PATTERN.find(line) ?: return null
            val name = match.groupValues[1].trim()
            return SystemVoice(name, name, match.groupValues[2])
        }

        internal fun macSpeechCommand(text: String, configuration: VoiceOutputConfiguration): List<String> = buildList {
            add("say")
            configuration.voiceId?.takeIf(String::isNotBlank)?.let {
                add("-v")
                add(it)
            }
            add("-r")
            add((configuration.speedPercent.coerceIn(50, 150) * 2).toString())
            add("--")
            add(text)
        }

        internal fun windowsSpeechCommand(text: String, configuration: VoiceOutputConfiguration): List<String> {
            fun literal(value: String) = "[Text.Encoding]::UTF8.GetString([Convert]::FromBase64String('" +
                Base64.getEncoder().encodeToString(value.toByteArray(Charsets.UTF_8)) + "'))"
            val script = "Add-Type -AssemblyName System.Speech; " +
                "${'$'}s = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                "${'$'}voice = ${literal(configuration.voiceId.orEmpty())}; " +
                "if (${'$'}voice) { ${'$'}s.SelectVoice(${'$'}voice) }; " +
                "${'$'}s.Rate = [Math]::Round((${configuration.speedPercent.coerceIn(50, 150)} - 100) / 5); " +
                "${'$'}s.Volume = ${configuration.volumePercent.coerceIn(0, 100)}; " +
                "try { ${'$'}s.Speak((${literal(text)})) } finally { ${'$'}s.Dispose() }"
            return listOf(
                "powershell.exe", "-NoProfile", "-NonInteractive", "-EncodedCommand",
                Base64.getEncoder().encodeToString(script.toByteArray(Charsets.UTF_16LE)),
            )
        }

        internal fun whisperCommand(
            configuration: LocalTranscriptionConfiguration,
            wav: Path,
            outputBase: Path,
        ): List<String> = listOf(
            configuration.executablePath,
            "-m", configuration.modelPath,
            "-f", wav.toString(),
            "-l", configuration.language,
            "-nt", "-otxt", "-of", outputBase.toString(),
        )

        internal fun waitForProcess(process: Process, timeout: Long, unit: TimeUnit): Boolean {
            if (process.waitFor(timeout, unit)) return true
            stopProcess(process)
            return false
        }

        internal fun pcmLevel(bytes: ByteArray, count: Int): Float {
            if (count < 2) return 0f
            var sum = 0.0
            var samples = 0
            var index = 0
            while (index + 1 < count) {
                val sample = ((bytes[index + 1].toInt() shl 8) or (bytes[index].toInt() and 0xff)).toShort().toInt()
                val normalized = sample / 32768.0
                sum += normalized * normalized
                samples += 1
                index += 2
            }
            return kotlin.math.sqrt(sum / samples).toFloat().coerceIn(0f, 1f)
        }

        private fun stopProcess(process: Process) {
            process.destroy()
            if (!process.waitFor(STOP_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) process.destroyForcibly()
        }

        private fun validateTranscriptionConfiguration(configuration: LocalTranscriptionConfiguration) {
            require(Files.isRegularFile(Path.of(configuration.executablePath))) {
                "Selecione o executável whisper-cli"
            }
            require(Files.isRegularFile(Path.of(configuration.modelPath))) {
                "Selecione um modelo GGML do Whisper"
            }
        }

        private fun captureFormat() = AudioFormat(16_000f, 16, 1, true, false)

        private fun writeWav(path: Path, bytes: ByteArray) {
            val format = captureFormat()
            ByteArrayInputStream(bytes).use { input ->
                AudioInputStream(input, format, bytes.size.toLong() / format.frameSize).use { audio ->
                    AudioSystem.write(audio, AudioFileFormat.Type.WAVE, path.toFile())
                }
            }
        }

        private val MAC_VOICE_PATTERN = Regex("^(.+?)\\s{2,}([a-z]{2}_[A-Z]{2})\\s+#")
        private const val CAPTURE_BUFFER_BYTES = 3_200
        private const val MINIMUM_AUDIO_BYTES = 3_200
        private const val MAXIMUM_AUDIO_BYTES = 16_000 * 2 * 60
        private const val CAPTURE_JOIN_TIMEOUT_MILLIS = 1_000L
        private const val STOP_TIMEOUT_MILLIS = 300L
        private const val COMMAND_TIMEOUT_SECONDS = 10L
        private const val SPEECH_TIMEOUT_MINUTES = 5L
        private const val TRANSCRIPTION_TIMEOUT_MINUTES = 2L
        private const val SETUP_TIMEOUT_MINUTES = 15L
        private const val MANAGED_MODEL_NAME = "ggml-base.bin"
        private const val MINIMUM_MODEL_BYTES = 100_000_000L
        private val MODEL_URL = java.net.URI.create(
            "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-base.bin",
        ).toURL()
    }
}
