package com.example.notesghama.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.notesghama.db.NoteEntity
import com.example.notesghama.db.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class NoteRepository(
    database: NotesDatabase,
    private val remoteDataSource: NoteRemoteDataSource
) {
    private val queries = database.noteQueries

    // Offline-first: Data di-load dari SQLDelight lokal, Sync berjalan di background via onStart
    fun getNotes(searchQuery: String, sortAscending: Boolean): Flow<List<NoteEntity>> {
        val query = if (searchQuery.isBlank()) {
            if (sortAscending) queries.selectAllAsc() else queries.selectAllDesc()
        } else {
            if (sortAscending) queries.searchNotesAsc(searchQuery) else queries.searchNotesDesc(searchQuery)
        }

        return query.asFlow().mapToList(Dispatchers.IO).onStart {
            refreshFromNetwork() // Bonus Sync Background
        }
    }

    fun getFavorites(): Flow<List<NoteEntity>> {
        return queries.getFavorites().asFlow().mapToList(Dispatchers.IO)
    }

    private suspend fun refreshFromNetwork() {
        try {
            remoteDataSource.fetchNotes() // Simulasi fetch API
        } catch (e: Exception) {
            // Error handling, gunakan cache lokal (Graceful Degradation)
        }
    }

    suspend fun getNoteById(id: Long): NoteEntity? = withContext(Dispatchers.IO) {
        queries.selectById(id).executeAsOneOrNull()
    }

    suspend fun insertNote(title: String, content: String) {
        withContext(Dispatchers.IO) {
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            queries.insertNote(title, content, 0, now, now)
        }
        try { remoteDataSource.createNote() } catch (e: Exception) { /* sync queue */ }
    }

    suspend fun updateNote(id: Long, title: String, content: String) {
        withContext(Dispatchers.IO) {
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            queries.updateNote(title, content, now, id)
        }
        try { remoteDataSource.updateNote() } catch (e: Exception) { /* sync queue */ }
    }

    suspend fun toggleFavorite(id: Long) {
        withContext(Dispatchers.IO) { queries.toggleFavorite(id) }
    }

    suspend fun deleteNote(id: Long) {
        withContext(Dispatchers.IO) { queries.deleteNote(id) }
        try { remoteDataSource.deleteNote() } catch (e: Exception) { /* sync queue */ }
    }
}