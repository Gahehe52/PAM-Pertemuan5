package com.example.notesghama.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesghama.model.NewsArticle
import com.example.notesghama.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NewsState {
    object Loading : NewsState()
    data class Success(val data: List<NewsArticle>) : NewsState()
    data class Error(val message: String) : NewsState()
}

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _state = MutableStateFlow<NewsState>(NewsState.Loading)
    val state = _state.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing = _refreshing.asStateFlow()

    init { fetch() }

    fun fetch() {
        viewModelScope.launch {
            _state.value = NewsState.Loading
            try {
                _state.value = NewsState.Success(repository.getNews())
            } catch (e: Exception) {
                _state.value = NewsState.Error("Gagal memuat data")
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _refreshing.value = true
            try {
                _state.value = NewsState.Success(repository.getNews())
            } catch (e: Exception) {
                _state.value = NewsState.Error("Refresh gagal")
            } finally {
                _refreshing.value = false
            }
        }
    }
}