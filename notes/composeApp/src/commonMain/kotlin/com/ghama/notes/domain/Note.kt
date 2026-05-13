package com.ghama.notes.domain

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String
)