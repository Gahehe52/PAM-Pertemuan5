package com.example.notesghama.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.notesghama.navigation.Screen
import com.example.notesghama.viewmodel.NotesViewModel

@Composable
fun NoteListScreen(navController: NavController, viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    if (notes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada catatan. Tambahkan sekarang!", color = MaterialTheme.colorScheme.secondary)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(notes) { note ->
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
                            Text(
                                text = if (note.content.length > 50) note.content.take(50) + "..." else note.content,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        // Tombol Ikon Favorit (Hati Penuh/Kosong)
                        IconButton(onClick = { viewModel.toggleFavorite(note.id) }) {
                            Icon(
                                imageVector = if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorit",
                                tint = if (note.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteDetailScreen(noteId: Int, navController: NavController, viewModel: NotesViewModel) {
    // Gunakan collectAsState agar UI Note Detail otomatis ter-update saat tombol favorit ditekan
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val note = notes.find { it.id == noteId }

    if (note == null) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Catatan tidak ditemukan")
            Button(onClick = { navController.popBackStack() }) { Text("Kembali") }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            // Tombol Favorit di dalam detail
            IconButton(onClick = { viewModel.toggleFavorite(note.id) }) {
                Icon(
                    imageVector = if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorit",
                    tint = if (note.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(note.content, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.weight(1f)) // Mendorong tombol ke bagian bawah

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = { navController.popBackStack() }) {
                Text("Kembali")
            }
            Row {
                Button(
                    onClick = {
                        viewModel.deleteNote(noteId)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { navController.navigate(Screen.EditNote.createRoute(noteId)) }) {
                    Text("Edit")
                }
            }
        }
    }
}

@Composable
fun AddNoteScreen(navController: NavController, viewModel: NotesViewModel) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tambah Catatan", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul Catatan") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Isi Catatan") },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { navController.popBackStack() }) { Text("Batal") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addNote(title, content)
                        navController.popBackStack()
                    }
                },
                enabled = title.isNotBlank() // Tombol mati jika judul kosong
            ) {
                Text("Simpan")
            }
        }
    }
}

@Composable
fun EditNoteScreen(noteId: Int, navController: NavController, viewModel: NotesViewModel) {
    val note = viewModel.getNoteById(noteId)

    if (note == null) {
        Text("Catatan tidak ditemukan")
        return
    }

    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Edit Catatan", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Isi") },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { navController.popBackStack() }) { Text("Batal") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.updateNote(noteId, title, content)
                        navController.popBackStack()
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}