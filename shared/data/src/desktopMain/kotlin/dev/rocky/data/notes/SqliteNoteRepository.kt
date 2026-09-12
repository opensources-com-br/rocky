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
        try {
        addColumnIfMissing("source_message_ids", "TEXT NOT NULL DEFAULT '[]'")
        addColumnIfMissing("evidence", "TEXT NOT NULL DEFAULT '[]'")
        addColumnIfMissing("metadata", "TEXT NOT NULL DEFAULT '{}'")
        database = RockyDatabase(driver)
        database.noteQueries.deleteLegacyDemoNotes()
        } catch (error: Exception) {
            driver.close()
            throw error
        }
    }

    override fun getAll(): List<LiveNote> = database.noteQueries
        .selectAll { id, text, timestamp, tag, sourceIds, evidence, metadata ->
            LiveNote(
                id = id,
                text = text,
                timestamp = timestamp,
                tag = tag,
                sourceMessageIds = decodeList(sourceIds).toSet(),
                evidence = decodeList(evidence),
            ).withMetadata(metadata)
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
            metadata = noteMetadata(note),
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
            metadata = noteMetadata(note),
            id = note.id,
        )
    }

    override fun delete(noteId: String) {
        database.noteQueries.deleteNote(noteId)
    }

    override fun importNotes(notes: List<LiveNote>): Int = database.transactionWithResult {
        val fresh = dev.rocky.core.notes.recordsToImport(getAll(), notes)
        fresh.forEach(::save)
        fresh.size
    }

    override fun deleteAll() { database.noteQueries.deleteAllNotes() }

    override fun close() {
        driver.close()
    }

    private fun addColumnIfMissing(name: String, definition: String) {
        val columns = driver.executeQuery(
            identifier = null,
            sql = "PRAGMA table_info(note)",
            mapper = { cursor ->
                val result = mutableSetOf<String>()
                while (cursor.next().value) result += requireNotNull(cursor.getString(1))
                app.cash.sqldelight.db.QueryResult.Value(result)
            },
            parameters = 0,
            binders = null,
        ).value
        if (name !in columns) {
            driver.execute(null, "ALTER TABLE note ADD COLUMN $name $definition", 0, null).value
        }
    }

    private fun decodeList(value: String): List<String> =
        runCatching { json.decodeFromString<List<String>>(value) }.getOrDefault(emptyList())

    private companion object {
        val json = Json
    }
}
