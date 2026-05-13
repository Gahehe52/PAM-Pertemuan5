package com.ghama.notes.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ghama.notes.domain.Note

@Composable
fun NotesScreen(
    uiState: NotesUiState,
    onAddNote: (String, String) -> Unit,
    onDeleteNote: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().testTag(TestTags.TITLE_INPUT)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth().testTag(TestTags.CONTENT_INPUT)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                onAddNote(title, content)
                title = ""
                content = ""
            },
            modifier = Modifier.fillMaxWidth().testTag(TestTags.ADD_BUTTON)
        ) {
            Text("Add Note")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is NotesUiState.Loading -> {
                CircularProgressIndicator()
            }
            is NotesUiState.Error -> {
                Text(text = "Error: ${uiState.message}")
            }
            is NotesUiState.Success -> {
                val notes = uiState.notes
                if (notes.isEmpty()) {
                    Text(
                        text = "No notes available",
                        modifier = Modifier.testTag(TestTags.EMPTY_STATE)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().testTag(TestTags.NOTES_LIST)) {
                        items(notes) { note ->
                            NoteItem(note = note, onDeleteClick = { onDeleteNote(note.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag(TestTags.NOTE_ITEM),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = note.title, style = MaterialTheme.typography.h6)
                Text(text = note.content, style = MaterialTheme.typography.body1)
            }
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.testTag(TestTags.DELETE_BUTTON + note.id)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Note")
            }
        }
    }
}