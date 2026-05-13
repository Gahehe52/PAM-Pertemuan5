package com.ghama.notes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghama.notes.domain.Note
import com.ghama.notes.domain.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    val uiState: StateFlow<NotesUiState> = repository.getAllNotes()
        .map { NotesUiState.Success(it) as NotesUiState }
        .catch { emit(NotesUiState.Error(it.message ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotesUiState.Loading
        )

    fun addNote(title: String, content: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertNote(Note(title = title, content = content))
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }
}