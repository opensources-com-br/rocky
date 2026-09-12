package dev.rocky.data.notes

import dev.rocky.core.live.LiveNote
import dev.rocky.core.notes.NoteRepository
import java.nio.file.Path

/** Opening failures remain retryable and never rename, replace or remove the user's database. */
class RecoverableNoteRepository(private val path: Path) : NoteRepository, AutoCloseable {
    private var repository: SqliteNoteRepository? = null
    private fun open(): SqliteNoteRepository = repository ?: SqliteNoteRepository(path).also { repository = it }
    override fun getAll(): List<LiveNote> = open().getAll()
    override fun save(note: LiveNote) = open().save(note)
    override fun update(note: LiveNote) = open().update(note)
    override fun delete(noteId: String) = open().delete(noteId)
    override fun importNotes(notes: List<LiveNote>) = open().importNotes(notes)
    override fun deleteAll() = open().deleteAll()
    override fun close() { repository?.close(); repository = null }
}
