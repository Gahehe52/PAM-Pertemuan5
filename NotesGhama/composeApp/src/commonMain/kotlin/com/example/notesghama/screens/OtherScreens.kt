package com.example.notesghama.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesghama.di.Dependencies
import com.example.notesghama.viewmodel.NotesViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(navController: NavController, viewModel: NotesViewModel) {
    // Menggunakan collectAsState biasa dan import 'getValue' secara otomatis
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())

    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada catatan favorit.", color = MaterialTheme.colorScheme.secondary)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(favorites) { note ->
                NoteCardItem(note, navController, viewModel)
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsManager = Dependencies.settingsManager
    val coroutineScope = rememberCoroutineScope()

    val theme by settingsManager.themeFlow.collectAsState(initial = "system")
    val sortAscending by settingsManager.sortOrderFlow.collectAsState(initial = false)

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Pengaturan Aplikasi", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = theme == "dark",
                onCheckedChange = { isDark ->
                    coroutineScope.launch {
                        settingsManager.setTheme(if (isDark) "dark" else "light")
                    }
                }
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Urutkan: Dari Terlama", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = sortAscending,
                onCheckedChange = { isAsc ->
                    coroutineScope.launch {
                        settingsManager.setSortAscending(isAsc)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.End)) {
            Text("Kembali")
        }
    }
}