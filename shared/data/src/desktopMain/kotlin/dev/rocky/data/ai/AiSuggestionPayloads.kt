package dev.rocky.data.ai

import dev.rocky.core.ai.AiGeneratedSuggestion
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object AiSuggestionPayloads {
    private val json = Json { ignoreUnknownKeys = true }

    fun ollamaText(body: String): String =
        body.asObject().objectAt("message").stringAt("content")

    fun openAiText(body: String): String {
        val output = body.asObject().arrayAt("output")
        return output.asSequence()
            .map { it.jsonObject }
            .flatMap { item -> item.arrayAtOrEmpty("content").asSequence() }
            .map { it.jsonObject }
            .first { it.stringAt("type") == "output_text" }
            .stringAt("text")
    }

    fun openRouterText(body: String): String = body.asObject()
        .arrayAt("choices")
        .first()
        .jsonObject
        .objectAt("message")
        .stringAt("content")

    fun suggestion(text: String, allowedMessageIds: Set<String>): AiGeneratedSuggestion? {
        val payload = text.removePrefix("```json").removePrefix("```").removeSuffix("```").trim().asObject()
        val suggestion = payload.stringAt("suggestion").trim()
        if (suggestion.isEmpty()) return null
        val sources = payload.arrayAt("source_message_ids")
            .map { it.jsonPrimitive.content }
            .filterTo(linkedSetOf()) { it in allowedMessageIds }
        require(sources.isNotEmpty()) { "AI suggestion did not cite a valid chat message" }
        return AiGeneratedSuggestion(suggestion, sources)
    }

    fun ollamaModels(body: String): Set<String> = body.asObject().arrayAt("models")
        .mapTo(linkedSetOf()) { it.jsonObject.stringAt("name") }

    private fun String.asObject(): JsonObject = json.parseToJsonElement(this).jsonObject
}

private fun JsonObject.objectAt(name: String): JsonObject =
    requireNotNull(this[name]) { "Missing AI object: $name" }.jsonObject

private fun JsonObject.arrayAt(name: String): JsonArray =
    requireNotNull(this[name]) { "Missing AI array: $name" }.jsonArray

private fun JsonObject.arrayAtOrEmpty(name: String): JsonArray =
    this[name]?.jsonArray ?: JsonArray(emptyList())

private fun JsonObject.stringAt(name: String): String =
    requireNotNull(this[name]) { "Missing AI field: $name" }.jsonPrimitive.content
