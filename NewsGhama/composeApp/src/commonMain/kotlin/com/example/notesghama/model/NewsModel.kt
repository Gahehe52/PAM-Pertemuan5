package com.example.notesghama.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticle>
)

@Serializable
data class NewsArticle(
    val title: String? = null,
    val description: String? = null,
    val urlToImage: String? = null,
    val content: String? = null
)