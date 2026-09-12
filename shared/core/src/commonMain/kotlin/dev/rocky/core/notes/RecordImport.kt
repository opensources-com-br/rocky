package dev.rocky.core.notes

import dev.rocky.core.live.LiveNote

fun recordsToImport(existing: List<LiveNote>, incoming: List<LiveNote>): List<LiveNote> {
    require(incoming.size <= 10_000) { "O backup excede 10.000 registros." }
    require(incoming.map { it.id }.distinct().size == incoming.size) { "O backup contém IDs duplicados." }
    val byId = existing.associateBy { it.id }
    incoming.forEach { note ->
        require(note.id.isNotBlank() && note.id.length <= 300 && note.text.length <= 1_000_000)
        require(note.evidence.size <= 1000 && note.sourceMessageIds.size <= 1000)
        require(note.sessionId.length <= 300 && note.sessionLabel.length <= 1000 && note.messageCount >= 0)
        require(note.offsetMillis == null || note.offsetMillis >= 0)
        require(byId[note.id] == null || byId[note.id] == note) {
            "Há registros diferentes com o mesmo ID. Nenhum registro foi importado."
        }
    }
    return incoming.filter { it.id !in byId }
}
