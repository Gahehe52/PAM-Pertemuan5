package ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.error.AIError
import domain.repository.AIRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ChatMessage(
    // Menggunakan Random dari Kotlin Standard Library yang mendukung Multiplatform
    val id: String = Random.nextLong().toString(),
    val text: String,
    val isUser: Boolean,
    val hasImage: Boolean = false
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val error: String? = null
)

class NutritionViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private suspend fun <T> retryWithBackoff(
        times: Int = 3,
        initialDelay: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> Result<T>
    ): Result<T> {
        var currentDelay = initialDelay
        repeat(times - 1) {
            val result = block()
            if (result.isSuccess) return result

            val exception = result.exceptionOrNull()
            if (exception is AIError.RateLimited) {
                delay(exception.retryAfter * 1000L)
            } else if (exception is AIError.ServerError || exception is AIError.NetworkError) {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong()
            } else {
                return result
            }
        }
        return block()
    }

    fun sendMessage(message: String, base64Image: String? = null, mimeType: String? = null) {
        if (message.isBlank() && base64Image == null) return

        _uiState.update { state ->
            state.copy(
                messages = state.messages + ChatMessage(
                    text = message.ifBlank { "[Menganalisis Gambar Makanan]" },
                    isUser = true,
                    hasImage = base64Image != null
                ),
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            val result = retryWithBackoff {
                aiRepository.chat(message, base64Image, mimeType)
            }

            result.onSuccess { responseText ->
                _uiState.update { it.copy(isLoading = false, isStreaming = true) }
                streamTextToUI(responseText)
            }.onFailure { error ->
                val errorMessage = when (error) {
                    is AIError.RateLimited -> "Terlalu banyak permintaan. Coba lagi dalam ${error.retryAfter} detik."
                    is AIError.NetworkError -> "Tidak ada koneksi internet."
                    else -> "Terjadi kesalahan: ${error.message}"
                }
                _uiState.update { it.copy(error = errorMessage, isLoading = false) }
            }
        }
    }

    private suspend fun streamTextToUI(fullText: String) {
        // Menggunakan Random dari Kotlin Standard Library yang mendukung Multiplatform
        val responseMessageId = Random.nextLong().toString()
        _uiState.update { state ->
            state.copy(messages = state.messages + ChatMessage(id = responseMessageId, text = "", isUser = false))
        }

        var currentText = ""
        val chunkSize = 5

        for (i in fullText.indices step chunkSize) {
            currentText += fullText.substring(i, minOf(i + chunkSize, fullText.length))
            _uiState.update { state ->
                val updatedMessages = state.messages.map { msg ->
                    if (msg.id == responseMessageId) msg.copy(text = currentText) else msg
                }
                state.copy(messages = updatedMessages)
            }
            delay(15)
        }
        _uiState.update { it.copy(isStreaming = false) }
    }

    fun clearChat() {
        aiRepository.clearHistory()
        _uiState.update { ChatUiState() }
    }
}