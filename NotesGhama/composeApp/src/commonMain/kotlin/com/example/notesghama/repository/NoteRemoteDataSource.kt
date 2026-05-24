package com.example.notesghama.repository
import kotlinx.coroutines.delay

class NoteRemoteDataSource {
    // Simulasi Remote API (Mocking HTTP Calls)
    suspend fun fetchNotes() {
        delay(1000) // Simulasi latency network
    }

    suspend fun createNote() { delay(500) }
    suspend fun updateNote() { delay(500) }
    suspend fun deleteNote() { delay(500) }
}