package com.example.notesghama

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform