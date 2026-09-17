package dev.rocky.data.ai

import kotlin.test.Test
import kotlin.test.assertEquals

class AiProviderErrorTest {
    @Test fun explainsProviderUnavailability() {
        assertEquals(
            "Falha (HTTP 503): O provedor está indisponível; tente novamente mais tarde.",
            AiProviderException(503).userMessage("Falha"),
        )
    }

    @Test fun explainsMissingOrUnavailableModels() {
        assertEquals(
            "Falha (HTTP 404): Confira o modelo e o endereço configurados.",
            AiProviderException(404).userMessage("Falha"),
        )
    }

    @Test fun explainsInvalidCredentialsAndPermissions() {
        for (status in listOf(401, 403)) {
            assertEquals(
                "Falha (HTTP $status): Confira a chave e as permissões do provedor.",
                AiProviderException(status).userMessage("Falha"),
            )
        }
    }

    @Test fun reportsOnlyUsagePresentInProviderResponse() {
        assertEquals(15L, reportedTokenCount("""{"usage":{"input_tokens":10,"output_tokens":5}}""", "input_tokens", "output_tokens", nested = true))
        assertEquals(21L, reportedTotalTokenCount("""{"usageMetadata":{"totalTokenCount":21}}""", "totalTokenCount", "usageMetadata"))
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
        assertEquals(
            "Falha ao gerar: O provedor encerrou a resposta antes de concluir (max_output_tokens).",
            AiResponseIncompleteException("max_output_tokens").userMessage("Falha ao gerar"),
        )
        assertEquals(
            "Falha ao gerar: O provedor bloqueou a resposta (SAFETY). Revise o conteúdo enviado.",
            AiResponseBlockedException("SAFETY").userMessage("Falha ao gerar"),
        )
    }
}
