package com.ghama.notes.presentation

import com.ghama.notes.domain.Note

sealed class NotesUiState {
    object Loading : NotesUiState()
    data class Success(val notes: List<Note>) : NotesUiState()
    data class Error(val message: String) : NotesUiState()
}