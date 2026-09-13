package dev.rocky.data.voice

import dev.rocky.core.voice.*
import kotlinx.serialization.json.*

fun elevenLabsCatalog(configuration: ElevenLabsConfiguration): VoiceCatalog = ElevenLabsSession().use { session ->
    fun json(path: String): JsonElement = session.open(path, configuration.apiKey).use { input ->
        val bytes = input.readNBytes(1024 * 1024 + 1)
        require(bytes.size <= 1024 * 1024) { "Catálogo de voz excedeu o limite." }
        Json.parseToJsonElement(bytes.toString(Charsets.UTF_8))
    }
    readElevenLabsCatalog(::json)
}

internal fun readElevenLabsCatalog(json: (String) -> JsonElement): VoiceCatalog {
    val voices = mutableListOf<SystemVoice>()
    var token: String? = null
    repeat(10) {
        val suffix = token?.let { "&next_page_token=" + java.net.URLEncoder.encode(it, Charsets.UTF_8) }.orEmpty()
        val page = json("/v2/voices?page_size=100$suffix").jsonObject
        voices += page["voices"]!!.jsonArray.map { item ->
            val v = item.jsonObject
            SystemVoice(v["voice_id"]!!.jsonPrimitive.content, v["name"]!!.jsonPrimitive.content)
        }
        token = page["next_page_token"]?.jsonPrimitive?.contentOrNull
        if (page["has_more"]?.jsonPrimitive?.booleanOrNull != true || token == null) {
            val models = json("/v1/models").jsonArray.filter { it.jsonObject["can_do_text_to_speech"]?.jsonPrimitive?.booleanOrNull == true }
                .map { SpeechModel(it.jsonObject["model_id"]!!.jsonPrimitive.content, it.jsonObject["name"]!!.jsonPrimitive.content) }
            return VoiceCatalog(voices.distinctBy { it.id }, models)
        }
    }
    error("Catálogo muito grande. Informe o ID da voz manualmente.")
}
