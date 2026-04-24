package com.example.notesghama.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesghama.navigation.BottomNavItem
import com.example.notesghama.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavDrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    ModalDrawerSheet {
        Text("Menu Utama", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Home (Notes)") },
            icon = { Icon(Icons.Default.Home, null) },
            selected = false,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate(BottomNavItem.Notes.route)
            },
            modifier = Modifier.padding(8.dp)
        )

        NavigationDrawerItem(
            label = { Text("Settings") },
            icon = { Icon(Icons.Default.Settings, null) },
            selected = false,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate(Screen.Settings.route)
            },
            modifier = Modifier.padding(8.dp)
        )
    }
}