package com.example.notesghama.viewmodel

import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 1. Tambahkan properti isFavorite
data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val isFavorite: Boolean = false
)

class NotesViewModel : ViewModel() {
    private val settings: Settings = Settings()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private var nextId = 1

    init {
        loadNotes() // Muat catatan saat aplikasi dibuka
    }

    // Fungsi untuk menyimpan catatan ke memori permanen HP
    private fun saveNotes() {
        val serialized = _notes.value.joinToString(separator = "|||") {
            "${it.id}===---===${it.title.replace("|||", "")}===---===${it.content.replace("|||", "")}===---===${it.isFavorite}"
        }
        settings.putString("saved_notes_data", serialized)
        settings.putInt("next_note_id_data", nextId)
    }

    // Fungsi untuk menarik catatan dari memori permanen HP
    private fun loadNotes() {
        val savedString = settings.getString("saved_notes_data", "")
        if (savedString.isNotEmpty()) {
            val loadedNotes = savedString.split("|||").mapNotNull {
                val parts = it.split("===---===")
                if (parts.size == 4) {
                    Note(parts[0].toIntOrNull() ?: 0, parts[1], parts[2], parts[3].toBooleanStrictOrNull() ?: false)
                } else null
            }
            _notes.value = loadedNotes
            nextId = settings.getInt("next_note_id_data", (loadedNotes.maxOfOrNull { it.id } ?: 0) + 1)
        } else {
            // Jika kosong (baru pertama kali install)
            _notes.value = listOf(
                Note(1, "Catatan Pertama", "Catatan ini sekarang permanen lho!", false),
                Note(2, "Ide Proyek PAM", "Jangan lupa tambahkan video demo untuk tugas 5.", true)
            )
            nextId = 3
            saveNotes()
        }
    }

    fun addNote(title: String, content: String) {
        val newNote = Note(id = nextId++, title = title, content = content)
        _notes.update { it + newNote }
        saveNotes()
    }

    fun updateNote(id: Int, newTitle: String, newContent: String) {
        _notes.update { currentNotes ->
            currentNotes.map { if (it.id == id) it.copy(title = newTitle, content = newContent) else it }
        }
        saveNotes()
    }

    fun deleteNote(id: Int) {
        _notes.update { currentNotes -> currentNotes.filter { it.id != id } }
        saveNotes()
    }

    // Fungsi BARU: Mengubah status Favorit
    fun toggleFavorite(id: Int) {
        _notes.update { currentNotes ->
            currentNotes.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
        }
        saveNotes()
    }

    fun getNoteById(id: Int): Note? {
        return _notes.value.find { it.id == id }
    }
}