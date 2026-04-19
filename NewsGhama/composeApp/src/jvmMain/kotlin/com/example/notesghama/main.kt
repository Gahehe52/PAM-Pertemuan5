package com.example.notesghama

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "NotesGhama",
    ) {
        App()
    }
}