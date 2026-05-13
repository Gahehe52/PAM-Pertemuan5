package com.example.aiapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import data.network.GeminiService
import domain.repository.AIRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import ui.screen.NutritionScreen
import ui.viewmodel.NutritionViewModel

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun App(apiKey: String) {
    MaterialTheme {
        val viewModel = remember {
            val client = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        // SANGAT PENTING: Mencegah Ktor mengirim "inlineData": null ke Gemini
                        explicitNulls = false
                    })
                }
            }
            val service = GeminiService(client, apiKey)
            val repository = AIRepositoryImpl(service)
            NutritionViewModel(repository)
        }

        NutritionScreen(viewModel)
    }
}