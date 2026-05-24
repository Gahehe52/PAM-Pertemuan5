package com.example.notesghama

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.notesghama.di.initKoin
import org.koin.core.context.GlobalContext

fun main() = application {
    if (GlobalContext.getOrNull() == null) {
        initKoin()
    }
    Window(onCloseRequest = ::exitApplication, title = "NotesGhama") {
        App()
    }
}