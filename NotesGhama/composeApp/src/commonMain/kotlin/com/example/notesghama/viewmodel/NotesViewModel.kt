package com.example.notesghama.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesghama.db.NoteEntity
import com.example.notesghama.di.Dependencies
import com.example.notesghama.repository.NoteRepository
import com.example.notesghama.settings.SettingsManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class NotesUiState {
    object Loading : NotesUiState()
    object Empty : NotesUiState()
    data class Content(val notes: List<NoteEntity>) : NotesUiState()
    data class Error(val message: String) : NotesUiState()
}

class NotesViewModel(
    private val repository: NoteRepository = Dependencies.repository,
    private val settingsManager: SettingsManager = Dependencies.settingsManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<NotesUiState> = combine(
        _searchQuery,
        settingsManager.sortOrderFlow
    ) { query, sortAsc -> Pair(query, sortAsc) }
        .flatMapLatest { (query, sortAsc) ->
            repository.getNotes(query, sortAsc)
        }
        .map { notes ->
            if (notes.isEmpty()) NotesUiState.Empty else NotesUiState.Content(notes)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotesUiState.Loading
        )

    val favorites: StateFlow<List<NoteEntity>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNote(title: String, content: String) = viewModelScope.launch {
        repository.insertNote(title, content)
    }

    fun updateNote(id: Long, title: String, content: String) = viewModelScope.launch {
        repository.updateNote(id, title, content)
    }

    fun deleteNote(id: Long) = viewModelScope.launch {
        repository.deleteNote(id)
    }

    fun toggleFavorite(id: Long) = viewModelScope.launch {
        repository.toggleFavorite(id)
    }

    suspend fun getNoteById(id: Long): NoteEntity? {
        return repository.getNoteById(id)
    }
}