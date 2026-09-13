package dev.rocky.data.voice

import dev.rocky.core.voice.*
import kotlinx.serialization.json.*
import kotlin.test.*

class ElevenLabsRequestTest {
    @Test fun preservesQuotedTextWithoutSerializingCredentials() {
        val text = "Olá, \"Rocky\"!\nTeste \\ caminho"
        val output = VoiceOutputConfiguration(elevenLabs = ElevenLabsConfiguration(apiKey = "private-key"))
        val body = speechPayload(text, output)
        assertEquals(text, Json.parseToJsonElement(body).jsonObject["text"]!!.jsonPrimitive.content)
        assertFalse(body.contains("private-key"))
        assertFalse(output.toString().contains("private-key"))
    }
    @Test fun boundsTextAndSpeedBeforeSendingToTheProvider() {
        assertFailsWith<IllegalArgumentException> { speechPayload(" ", VoiceOutputConfiguration()) }
        assertFailsWith<IllegalArgumentException> { speechPayload("a".repeat(5001), VoiceOutputConfiguration()) }
        for ((speed, expected) in listOf(0 to 0.7, 100 to 1.0, 999 to 1.2)) {
            val body = Json.parseToJsonElement(speechPayload("a".repeat(5000),
                VoiceOutputConfiguration(speedPercent = speed))).jsonObject
            assertEquals(expected, body["voice_settings"]!!.jsonObject["speed"]!!.jsonPrimitive.double)
        }
    }
}
