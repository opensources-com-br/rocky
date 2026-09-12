package dev.rocky.data.notes

import dev.rocky.core.live.LiveNote
import kotlinx.serialization.json.*

object RecordBackup {
    fun encode(notes: List<LiveNote>): String {
        dev.rocky.core.notes.recordsToImport(emptyList(), notes)
        return buildJsonObject {
            put("kind", "rocky-records"); put("version", 1)
            putJsonArray("records") { notes.forEach { note ->
                add(buildJsonObject {
                    put("id", note.id); put("text", note.text)
                    put("timestamp", note.timestamp); put("tag", note.tag)
                    putJsonArray("sources") { note.sourceMessageIds.forEach { add(it) } }
                    putJsonArray("evidence") { note.evidence.forEach { add(it) } }
                    put("metadata", Json.parseToJsonElement(noteMetadata(note)))
                })
            } }
        }.toString().also { require(it.encodeToByteArray().size <= 50 * 1024 * 1024) { "Backup excede 50 MB." } }
    }

    fun decode(content: String): List<LiveNote> {
        require(content.encodeToByteArray().size <= 50 * 1024 * 1024) { "Backup excede 50 MB." }
        val root = Json.parseToJsonElement(content).jsonObject
        require(root["kind"]?.jsonPrimitive?.content == "rocky-records" && root["version"]?.jsonPrimitive?.intOrNull == 1) {
            "Formato ou versão de backup incompatível."
        }
        val records = requireNotNull(root["records"]).jsonArray
        require(records.size <= 10_000) { "O backup excede 10.000 registros." }
        return records.map { value ->
            val record = value.jsonObject
            fun string(name: String) = requireNotNull(record[name]).jsonPrimitive.content
            LiveNote(string("id"), string("text"), string("timestamp"), string("tag"),
                requireNotNull(record["sources"]).jsonArray.map { it.jsonPrimitive.content }.toSet(),
                requireNotNull(record["evidence"]).jsonArray.map { it.jsonPrimitive.content })
                .withMetadata(requireNotNull(record["metadata"]).toString())
        }.also { dev.rocky.core.notes.recordsToImport(emptyList(), it) }
    }
}
