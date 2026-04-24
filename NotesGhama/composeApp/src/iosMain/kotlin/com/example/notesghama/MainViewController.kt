package com.example.notesghama
import androidx.compose.ui.window.ComposeUIViewController
import com.example.notesghama.di.Dependencies

fun MainViewController() = ComposeUIViewController {
    Dependencies.initDatabase(DatabaseDriverFactory())
    App()
}