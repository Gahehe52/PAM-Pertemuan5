package com.example.notesghama.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notesghama.components.BottomNavBar
import com.example.notesghama.components.NavDrawerContent
import com.example.notesghama.screens.*
import com.example.notesghama.viewmodel.NotesViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val viewModel = koinInject<NotesViewModel>()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavDrawerContent(navController = navController, drawerState = drawerState, scope = scope)
        }
    ) {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomNav = currentRoute == Screen.NoteList.route ||
                        currentRoute == Screen.Favorites.route ||
                        currentRoute == Screen.Profile.route

                if (showBottomNav) {
                    BottomNavBar(
                        navController = navController,
                        currentRoute = currentRoute ?: ""
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.NoteList.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.NoteList.route) {
                    NoteListScreen(navController, viewModel, drawerState, scope)
                }
                composable(Screen.Favorites.route) {
                    FavoritesScreen(navController, viewModel, drawerState, scope)
                }

                // DIPERBAIKI: Kembali menggunakan ProfileScreen bawaan Anda
                composable(Screen.Profile.route) {
                    ProfileScreen()
                }

                composable(Screen.Settings.route) { SettingsScreen(navController) }
                composable(Screen.AddNote.route) { AddNoteScreen(navController, viewModel) }

                composable(Screen.NoteDetail.route) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                    if (noteId != null) {
                        NoteDetailScreen(noteId, navController, viewModel)
                    }
                }
                composable(Screen.EditNote.route) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                    if (noteId != null) {
                        EditNoteScreen(noteId, navController, viewModel)
                    }
                }
            }
        }
    }
}