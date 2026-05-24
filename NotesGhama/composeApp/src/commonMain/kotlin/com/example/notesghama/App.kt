package com.example.notesghama

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.notesghama.navigation.AppNavigation

@Composable
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}