package com.ghama.notes

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ghama.notes.di.allModules
import com.ghama.notes.presentation.NotesScreen
import com.ghama.notes.presentation.NotesViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    // Memulai Koin di level Compose (sesuai best practice KMP agar cross-platform)
    KoinApplication(application = {
        modules(allModules)
    }) {
        MaterialTheme {
            // Inject ViewModel menggunakan Koin
            val viewModel = koinInject<NotesViewModel>()

            // Collect State dari ViewModel
            val uiState by viewModel.uiState.collectAsState()

            // Panggil Screen
            NotesScreen(
                uiState = uiState,
                onAddNote = { title, content ->
                    viewModel.addNote(title, content)
                },
                onDeleteNote = { id ->
                    viewModel.deleteNote(id)
                }
            )
        }
    }
}