package dev.rocky.data.ai

import kotlin.test.Test
import kotlin.test.assertEquals

class AiProviderErrorTest {
    @Test fun reportsOnlyUsagePresentInProviderResponse() {
        assertEquals(15L, reportedTokenCount("""{"usage":{"input_tokens":10,"output_tokens":5}}""", "input_tokens", "output_tokens", nested = true))
        assertEquals(null, reportedTokenCount("{}", "input_tokens", "output_tokens", nested = true))
    }

    @Test
    fun showsActionsWithoutEchoingProviderPayloads() {
        assertEquals(
            "Falha ao gerar (HTTP 429): Confira os créditos e limites do provedor; tente novamente mais tarde.",
            AiProviderException(429, "Limite gratuito atingido").userMessage("Falha ao gerar"),
        )
        assertEquals(
            "Falha ao gerar: Resposta inválida ou sem fontes; tente novamente ou escolha outro modelo.",
            IllegalArgumentException("Missing AI array: choices").userMessage("Falha ao gerar"),
        )
    }
}
