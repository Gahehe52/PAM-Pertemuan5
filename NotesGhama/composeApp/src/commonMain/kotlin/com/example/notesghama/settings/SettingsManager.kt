package com.example.notesghama.settings

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager {
    private val settings = Settings()

    private val _themeFlow = MutableStateFlow(settings.getString("app_theme", "system"))
    val themeFlow: Flow<String> = _themeFlow.asStateFlow()

    private val _sortOrderFlow = MutableStateFlow(settings.getBoolean("sort_ascending", false))
    val sortOrderFlow: Flow<Boolean> = _sortOrderFlow.asStateFlow()

    suspend fun setTheme(theme: String) {
        settings.putString("app_theme", theme)
        _themeFlow.value = theme
    }

    suspend fun setSortAscending(isAsc: Boolean) {
        settings.putBoolean("sort_ascending", isAsc)
        _sortOrderFlow.value = isAsc
    }
}