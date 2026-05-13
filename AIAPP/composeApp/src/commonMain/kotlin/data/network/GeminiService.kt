package data.network

import data.model.GeminiRequest
import data.model.GeminiResponse
import data.error.AIError
import data.error.safeAICall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GeminiService(
    private val client: HttpClient,
    private val apiKey: String
) {
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models"
    private val model = "gemini-1.5-flash" // Menggunakan 1.5 flash yang lebih teruji untuk V1Beta

    suspend fun generateContent(request: GeminiRequest): Result<String> = safeAICall {
        val response: GeminiResponse = client.post("$baseUrl/$model:generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        val candidate = response.candidates.firstOrNull()
        if (candidate == null) {
            throw AIError.ParseError("Respons API ditolak atau format tidak sesuai.")
        }

        if (candidate.content == null) {
            throw AIError.ParseError("Diblokir oleh filter keamanan (Alasan: ${candidate.finishReason}).")
        }

        candidate.content.parts.firstOrNull()?.text
            ?: throw AIError.ParseError("Berhasil terhubung, tapi teks dari AI kosong.")
    }
}