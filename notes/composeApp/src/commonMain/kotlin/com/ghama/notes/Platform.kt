package com.ghama.notes

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform