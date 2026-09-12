package dev.rocky.data.ai

import dev.rocky.core.agent.AgentConfiguration
import dev.rocky.core.ai.AiConnectionResult
import dev.rocky.core.ai.AiGeneratedSuggestion
import dev.rocky.core.ai.buildAiSuggestionPrompt
import dev.rocky.core.live.ChatMessage
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class OllamaAiClient(private val httpClient: HttpClient) {
    fun testConnection(endpoint: String, model: String): AiConnectionResult = runCatching {
        val response = httpClient.send(
            request(endpoint, "/api/tags").GET().build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireSuccess()
        val models = AiSuggestionPayloads.ollamaModels(response.body())
        val requested = if (':' in model) model else "$model:latest"
        val available = models.any { (if (':' in it) it else "$it:latest") == requested }
        if (available) {
            AiConnectionResult(true, "Ollama conectado · $model disponível")
        } else {
            AiConnectionResult(false, "Ollama conectado, mas o modelo $model não foi encontrado")
        }
    }.getOrElse { AiConnectionResult(false, it.userMessage("Não foi possível conectar ao Ollama")) }

    fun generate(
        endpoint: String,
        model: String,
        messages: List<ChatMessage>,
        streamerRequest: String? = null,
        agent: AgentConfiguration = AgentConfiguration(),
    ): AiGeneratedSuggestion? {
        val prompt = buildAiSuggestionPrompt(messages, streamerRequest, agent)
        val body = buildJsonObject {
            put("model", model)
            put("stream", false)
            put("format", "json")
            put("keep_alive", "10m")
            put("messages", buildJsonArray {
                add(buildJsonObject {
                    put("role", "system")
                    put("content", prompt.instructions)
                })
                add(buildJsonObject {
                    put("role", "user")
                    put("content", prompt.input)
                })
            })
        }.toString()
        val response = httpClient.send(
            request(endpoint, "/api/chat")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        ).requireSuccess()
        return AiSuggestionPayloads.suggestion(
            AiSuggestionPayloads.ollamaText(response.body()),
            prompt.messageIds,
        )?.copy(reportedTokens = reportedTokenCount(response.body(), "prompt_eval_count", "eval_count"))
    }

    private fun request(endpoint: String, path: String): HttpRequest.Builder = HttpRequest.newBuilder(
        URI.create("${endpoint.trim().trimEnd('/')}$path"),
    ).timeout(Duration.ofSeconds(45))
}

internal class AiProviderException(
    val statusCode: Int,
    val providerMessage: String? = null,
) : Exception("AI provider returned HTTP $statusCode")

private fun HttpResponse<String>.requireSuccess(): HttpResponse<String> {
    if (statusCode() !in 200..299) throw AiProviderException(statusCode())
    return this
}

internal fun Throwable.userMessage(fallback: String): String = when (this) {
    is dev.rocky.core.ai.AiRequestException -> message ?: fallback
    is AiProviderException -> "$fallback (HTTP $statusCode): " + when (statusCode) {
        401, 403 -> "Confira a chave e as permissões do provedor."
        402, 429 -> "Confira os créditos e limites do provedor; tente novamente mais tarde."
        404 -> "Confira o modelo e o endereço configurados."
        in 500..599 -> "O provedor está indisponível; tente novamente mais tarde."
        else -> "Confira se o modelo aceita o formato de resposta solicitado."
    }
    is java.net.http.HttpTimeoutException -> "$fallback: Tempo limite excedido; tente um modelo mais rápido ou tente novamente."
    is java.io.IOException -> "$fallback: Verifique a conexão e se o provedor está em execução."
    is IllegalArgumentException, is NoSuchElementException -> "$fallback: Resposta inválida ou sem fontes; tente novamente ou escolha outro modelo."
    else -> fallback
}

internal fun reportedTokenCount(body: String, input: String, output: String, nested: Boolean = false): Long? = runCatching {
    val root = kotlinx.serialization.json.Json.parseToJsonElement(body).jsonObject
    val usage = if (nested) root["usage"]?.jsonObject ?: return null else root
    val first = usage[input]?.jsonPrimitive?.content?.toLongOrNull() ?: return null
    val second = usage[output]?.jsonPrimitive?.content?.toLongOrNull() ?: return null
    (first + second).takeIf { first >= 0 && second >= 0 }
}.getOrNull()
