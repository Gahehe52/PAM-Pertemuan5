package com.example.notesghama

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.notesghama.navigation.AppNavigation
import com.example.notesghama.settings.SettingsManager
import org.koin.compose.koinInject

@Composable
fun App() {
    // Inject SettingsManager via Koin
    val settingsManager = koinInject<SettingsManager>()
    val theme by settingsManager.themeFlow.collectAsState(initial = "system")

    val isDark = when(theme) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    MaterialTheme(colorScheme = if (isDark) darkColorScheme() else lightColorScheme()) {
        AppNavigation()
    }
}