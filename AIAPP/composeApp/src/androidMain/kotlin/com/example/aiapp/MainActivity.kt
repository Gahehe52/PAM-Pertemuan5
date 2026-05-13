package com.example.aiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Meneruskan API Key rahasia dari local.properties -> BuildConfig -> App
            App(apiKey = BuildConfig.GEMINI_API_KEY)
        }
    }
}