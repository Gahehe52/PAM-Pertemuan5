package domain.repository

import data.model.Content
import data.model.GeminiRequest
import data.model.InlineData
import data.model.Part
import data.network.GeminiService

interface AIRepository {
    suspend fun chat(message: String, base64Image: String? = null, mimeType: String? = null): Result<String>
    fun clearHistory()
}

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    private val conversationHistory = mutableListOf<Content>()

    private val systemPrompt = Content(
        role = "system",
        parts = listOf(Part(text = """
            Kamu adalah nutritionist profesional dengan pengalaman 10 tahun.
            Tugas: Menganalisis makanan, memberikan informasi gizi, dan merekomendasikan resep sehat.
            Rules:
            - Selalu berikan informasi dalam Bahasa Indonesia dengan format yang rapi (Markdown diperbolehkan).
            - Jika pengguna mengirim gambar, analisis kemungkinan makanan di dalam gambar tersebut dan estimasi gizinya (kalori, protein, lemak, karbohidrat).
            - Jika pengguna bertanya resep, berikan resep sehat lengkap dengan estimasi kalori total.
            - Jangan menggunakan emoji sama sekali.
            - Berikan saran kesehatan yang relevan di akhir respons.
        """.trimIndent()))
    )

    override suspend fun chat(message: String, base64Image: String?, mimeType: String?): Result<String> {
        val userParts = mutableListOf<Part>()
        userParts.add(Part(text = message))

        if (base64Image != null && mimeType != null) {
            userParts.add(Part(inlineData = InlineData(mimeType = mimeType, data = base64Image)))
        }

        val userContent = Content(parts = userParts, role = "user")
        conversationHistory.add(userContent)

        val request = GeminiRequest(
            contents = conversationHistory.toList(),
            systemInstruction = systemPrompt
        )

        return geminiService.generateContent(request).onSuccess { responseText ->
            conversationHistory.add(Content(parts = listOf(Part(text = responseText)), role = "model"))
        }.onFailure {
            conversationHistory.removeLastOrNull()
        }
    }

    override fun clearHistory() {
        conversationHistory.clear()
    }
}