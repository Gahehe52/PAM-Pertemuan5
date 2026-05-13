package com.example.aiapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform