package dev.rocky.core.notes

import dev.rocky.core.live.LiveNote

interface NoteRepository {
    fun getAll(): List<LiveNote>

    fun save(note: LiveNote)

    fun update(note: LiveNote)

    fun delete(noteId: String)
}
