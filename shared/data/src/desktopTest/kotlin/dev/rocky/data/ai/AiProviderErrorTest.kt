package dev.rocky.data.ai

import kotlin.test.Test
import kotlin.test.assertEquals

class AiProviderErrorTest {
    @Test
    fun preservesProviderAndParsingDetails() {
        assertEquals(
            "Falha ao gerar (HTTP 429): Limite gratuito atingido",
            AiProviderException(429, "Limite gratuito atingido").userMessage("Falha ao gerar"),
        )
        assertEquals(
            "Falha ao gerar: Missing AI array: choices",
            IllegalArgumentException("Missing AI array: choices").userMessage("Falha ao gerar"),
        )
    }
}
