package com.example.notesghama.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.notesghama.db.NoteEntity
import com.example.notesghama.navigation.Screen
import com.example.notesghama.viewmodel.NotesUiState
import com.example.notesghama.viewmodel.NotesViewModel

@Composable
fun NoteListScreen(navController: NavController, viewModel: NotesViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Cari catatan...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true
        )

        // UI States Handler
        when (val state = uiState) {
            is NotesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is NotesUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada catatan. Tambahkan sekarang!", color = MaterialTheme.colorScheme.secondary)
                }
            }
            is NotesUiState.Content -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                    items(state.notes) { note ->
                        NoteCardItem(note, navController, viewModel)
                    }
                }
            }
            is NotesUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun NoteCardItem(note: NoteEntity, navController: NavController, viewModel: NotesViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate(Screen.NoteDetail.createRoute(note.id.toInt())) },
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
            IconButton(onClick = { viewModel.toggleFavorite(note.id) }) {
                Icon(
                    imageVector = if (note.isFavorite == 1L) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorit",
                    tint = if (note.isFavorite == 1L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NoteDetailScreen(noteId: Int, navController: NavController, viewModel: NotesViewModel) {
    var note by remember { mutableStateOf<NoteEntity?>(null) }

    LaunchedEffect(noteId) {
        note = viewModel.getNoteById(noteId.toLong())
    }

    if (note == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = note!!.title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
            IconButton(onClick = {
                viewModel.toggleFavorite(note!!.id)
                // update local state to reflect UI change instantly
                note = note!!.copy(isFavorite = if (note!!.isFavorite == 1L) 0L else 1L)
            }) {
                Icon(
                    imageVector = if (note!!.isFavorite == 1L) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorit",
                    tint = if (note!!.isFavorite == 1L) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(note!!.content, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = { navController.popBackStack() }) { Text("Kembali") }
            Row {
                Button(
                    onClick = {
                        viewModel.deleteNote(note!!.id)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { navController.navigate(Screen.EditNote.createRoute(noteId)) }) { Text("Edit") }
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
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Catatan") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Isi Catatan") }, modifier = Modifier.fillMaxWidth().weight(1f))
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
                enabled = title.isNotBlank()
            ) { Text("Simpan") }
        }
    }
}

@Composable
fun EditNoteScreen(noteId: Int, navController: NavController, viewModel: NotesViewModel) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        val note = viewModel.getNoteById(noteId.toLong())
        if (note != null) {
            title = note.title
            content = note.content
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Edit Catatan", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Isi") }, modifier = Modifier.fillMaxWidth().weight(1f))
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { navController.popBackStack() }) { Text("Batal") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.updateNote(noteId.toLong(), title, content)
                        navController.popBackStack()
                    }
                },
                enabled = title.isNotBlank()
            ) { Text("Simpan Perubahan") }
        }
    }
}