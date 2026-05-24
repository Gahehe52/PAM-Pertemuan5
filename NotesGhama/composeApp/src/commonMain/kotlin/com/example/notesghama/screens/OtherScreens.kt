package com.example.notesghama.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesghama.settings.SettingsManager
import com.example.notesghama.DeviceInfo
import com.example.notesghama.BatteryInfo
import com.example.notesghama.viewmodel.NotesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: NotesViewModel,
    drawerState: DrawerState, // Tambahan parameter
    scope: CoroutineScope     // Tambahan parameter
) {
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catatan Favorit") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Buka Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Belum ada catatan favorit.", color = MaterialTheme.colorScheme.secondary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
                items(favorites) { note ->
                    NoteCardItem(note, navController, viewModel)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsManager = koinInject<SettingsManager>()
    val deviceInfo = koinInject<DeviceInfo>()
    val batteryInfo = koinInject<BatteryInfo>()

    val coroutineScope = rememberCoroutineScope()
    val theme by settingsManager.themeFlow.collectAsState(initial = "system")
    val sortAscending by settingsManager.sortOrderFlow.collectAsState(initial = false)

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Pengaturan Aplikasi", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        // Info Perangkat & Baterai
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Informasi Perangkat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sistem Operasi : ${deviceInfo.getOsName()}", style = MaterialTheme.typography.bodyMedium)
                Text("Model HP : ${deviceInfo.getDeviceModel()}", style = MaterialTheme.typography.bodyMedium)
                Text("Status Baterai : ${batteryInfo.getBatteryLevel()}%", style = MaterialTheme.typography.bodyMedium)
            }
        }

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