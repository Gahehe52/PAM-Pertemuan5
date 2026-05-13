package com.ghama.notes.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(id: Long)
}

class NoteRepositoryImpl : NoteRepository {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L

    override fun getAllNotes(): Flow<List<Note>> = _notes

    override suspend fun insertNote(note: Note) {
        _notes.update { currentList ->
            val newNote = if (note.id == 0L) note.copy(id = nextId++) else note
            currentList + newNote
        }
    }

    override suspend fun deleteNote(id: Long) {
        _notes.update { currentList ->
            currentList.filter { it.id != id }
        }
    }
}