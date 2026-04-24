package com.example.notesghama

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.notesghama.di.Dependencies
import com.example.notesghama.navigation.AppNavigation

@Composable
fun App() {
    val theme by Dependencies.settingsManager.themeFlow.collectAsState(initial = "system")
    val isDark = when(theme) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    MaterialTheme(colorScheme = if (isDark) darkColorScheme() else lightColorScheme()) {
        AppNavigation()
    }
}