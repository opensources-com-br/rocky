package dev.rocky.data.voice

import dev.rocky.core.voice.VoiceOutputConfiguration
import kotlinx.serialization.json.*

internal fun speechPayload(text: String, output: VoiceOutputConfiguration): String = buildJsonObject {
    require(text.isNotBlank() && text.length <= 5000) { "Resposta longa demais para reprodução" }
    put("text", text)
    put("model_id", output.elevenLabs.modelId)
    putJsonObject("voice_settings") {
        put("stability", 0.5)
        put("similarity_boost", 0.75)
        put("speed", (output.speedPercent / 100.0).coerceIn(0.7, 1.2))
    }
}.toString()

internal fun elevenLabsError(status: Int): String = when (status) {
    401 -> "ElevenLabs: chave inválida. Confira a chave em Voz."
    403 -> "ElevenLabs: sua chave ou plano não permite esta voz/modelo."
    429 -> "ElevenLabs: quota ou limite de solicitações atingido. Tente mais tarde."
    400, 404, 422 -> "ElevenLabs: confira a voz, o modelo e os parâmetros selecionados."
    else -> "ElevenLabs indisponível. Verifique a conexão e tente novamente."
}
