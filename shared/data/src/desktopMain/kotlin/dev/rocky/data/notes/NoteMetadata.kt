package dev.rocky.data.notes

import dev.rocky.core.live.LiveNote
import kotlinx.serialization.json.*

internal fun noteMetadata(note: LiveNote): String = buildJsonObject {
    put("sessionId", note.sessionId)
    put("sessionLabel", note.sessionLabel)
    note.offsetMillis?.let { put("offsetMillis", it) }
    put("completed", note.completed)
    put("messageCount", note.messageCount)
}.toString()

internal fun LiveNote.withMetadata(value: String): LiveNote {
    val metadata = Json.parseToJsonElement(value).jsonObject
    metadata["offsetMillis"]?.let { require(it.jsonPrimitive.longOrNull != null) }
    metadata["completed"]?.let { require(it.jsonPrimitive.booleanOrNull != null) }
    metadata["messageCount"]?.let { require(it.jsonPrimitive.intOrNull != null) }
    return copy(
        sessionId = metadata["sessionId"]?.jsonPrimitive?.content.orEmpty(),
        sessionLabel = metadata["sessionLabel"]?.jsonPrimitive?.content.orEmpty(),
        offsetMillis = metadata["offsetMillis"]?.jsonPrimitive?.longOrNull,
        completed = metadata["completed"]?.jsonPrimitive?.booleanOrNull ?: false,
        messageCount = metadata["messageCount"]?.jsonPrimitive?.intOrNull ?: 0,
    )
}
