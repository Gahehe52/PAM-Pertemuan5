package com.example.notesghama
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.notesghama.di.Dependencies

fun main() = application {
    Dependencies.initDatabase(DatabaseDriverFactory())
    Window(onCloseRequest = ::exitApplication, title = "NotesGhama") {
        App()
    }
}