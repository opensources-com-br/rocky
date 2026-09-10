package dev.rocky.data.notes

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.rocky.core.live.LiveNote
import dev.rocky.core.notes.NoteRepository
import dev.rocky.data.db.RockyDatabase
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SqliteNoteRepository(databasePath: Path) : NoteRepository, AutoCloseable {
    private val driver: JdbcSqliteDriver
    private val database: RockyDatabase

    init {
        databasePath.parent?.let(Files::createDirectories)
        driver = JdbcSqliteDriver(
            url = "jdbc:sqlite:${databasePath.toAbsolutePath()}",
            schema = RockyDatabase.Schema,
        )
        database = RockyDatabase(driver)
    }

    override fun getAll(): List<LiveNote> = database.noteQueries
        .selectAll { id, text, timestamp, tag, sourceIds, evidence ->
            LiveNote(
                id = id,
                text = text,
                timestamp = timestamp,
                tag = tag,
                sourceMessageIds = decodeList(sourceIds).toSet(),
                evidence = decodeList(evidence),
            )
        }
        .executeAsList()

    override fun save(note: LiveNote) {
        database.noteQueries.insertNote(
            id = note.id,
            text = note.text,
            timestamp = note.timestamp,
            tag = note.tag,
            source_message_ids = json.encodeToString(note.sourceMessageIds.toList()),
            evidence = json.encodeToString(note.evidence),
            created_at = System.currentTimeMillis(),
        )
    }

    override fun update(note: LiveNote) {
        database.noteQueries.updateNote(
            text = note.text,
            timestamp = note.timestamp,
            tag = note.tag,
            source_message_ids = json.encodeToString(note.sourceMessageIds.toList()),
            evidence = json.encodeToString(note.evidence),
            id = note.id,
        )
    }

    override fun delete(noteId: String) {
        database.noteQueries.deleteNote(noteId)
    }

    override fun close() {
        driver.close()
    }

    private fun decodeList(value: String): List<String> =
        runCatching { json.decodeFromString<List<String>>(value) }.getOrDefault(emptyList())

    private companion object {
        val json = Json
    }
}
