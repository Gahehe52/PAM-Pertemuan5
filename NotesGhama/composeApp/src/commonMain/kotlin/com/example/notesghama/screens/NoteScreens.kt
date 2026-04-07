package com.example.notesghama.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesghama.navigation.Screen

@Composable
fun NoteListScreen(navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(5) { index ->
            val noteId = index + 1
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { navController.navigate(Screen.NoteDetail.createRoute(noteId)) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Catatan #$noteId", style = MaterialTheme.typography.titleMedium)
                    Text("Klik untuk melihat detail catatan ini...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun NoteDetailScreen(noteId: Int, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Detail Catatan", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Menampilkan isi lengkap catatan ID: $noteId")

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            OutlinedButton(onClick = { navController.popBackStack() }) {
                Text("Kembali")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { navController.navigate(Screen.EditNote.createRoute(noteId)) }) {
                Text("Edit Catatan")
            }
        }
    }
}

@Composable
fun AddNoteScreen(navController: NavController) {
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tambah Catatan Baru", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Isi Catatan") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Simpan Catatan")
        }
    }
}

@Composable
fun EditNoteScreen(noteId: Int, navController: NavController) {
    var text by remember { mutableStateOf("Isi lama dari catatan ID: $noteId") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Edit Catatan #$noteId", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Edit Isi") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Simpan Perubahan")
        }
    }
}