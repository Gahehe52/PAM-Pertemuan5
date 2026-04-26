package com.example.notesghama

import androidx.compose.ui.window.ComposeUIViewController
import com.example.notesghama.di.initKoin
import org.koin.core.context.GlobalContext

fun MainViewController() = ComposeUIViewController {
    if (GlobalContext.getOrNull() == null) {
        initKoin()
    }
    App()
}