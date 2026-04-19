package com.example.notesghama.repository

import com.example.notesghama.model.NewsArticle
import com.example.notesghama.model.NewsResponse
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class NewsRepository {
    private val settings = Settings()
    private val jsonConfig = Json { ignoreUnknownKeys = true }
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(jsonConfig)
        }
    }

    suspend fun getNews(): List<NewsArticle> {
        return try {
            val response: NewsResponse = client.get("https://saurav.tech/NewsAPI/top-headlines/category/technology/us.json").body()
            val articles = response.articles.filter { !it.title.isNullOrBlank() }
            val jsonString = jsonConfig.encodeToString(NewsResponse.serializer(), response)
            settings.putString("news_cache", jsonString)
            articles
        } catch (e: Exception) {
            val cache = settings.getString("news_cache", "")
            if (cache.isNotEmpty()) {
                val cachedResponse = jsonConfig.decodeFromString(NewsResponse.serializer(), cache)
                cachedResponse.articles
            } else {
                throw e
            }
        }
    }
}