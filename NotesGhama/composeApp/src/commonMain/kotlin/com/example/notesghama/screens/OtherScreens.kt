package com.example.notesghama.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.notesghama.navigation.Screen
import com.example.notesghama.viewmodel.NotesViewModel

@Composable
fun FavoritesScreen(navController: NavController, viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    // Filter HANYA catatan yang isFavorite = true
    val favoriteNotes = notes.filter { it.isFavorite }

    if (favoriteNotes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada catatan favorit.", color = MaterialTheme.colorScheme.secondary)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(favoriteNotes) { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { navController.navigate(Screen.NoteDetail.createRoute(note.id)) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(note.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (note.content.length > 30) note.content.take(30) + "..." else note.content, style = MaterialTheme.typography.bodyMedium)
                        }
                        // Tombol hapus dari favorit
                        IconButton(onClick = { viewModel.toggleFavorite(note.id) }) {
                            Icon(Icons.Default.Favorite, contentDescription = "Favorit", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pengaturan Aplikasi", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = { navController.popBackStack() }) { Text("Kembali") }
    }
}