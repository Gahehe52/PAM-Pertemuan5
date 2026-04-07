package com.example.notesghama.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notesghama.components.BottomNavBar
import com.example.notesghama.components.NavDrawerContent
import com.example.notesghama.screens.*
import com.example.notesghama.viewmodel.NotesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Inisialisasi NotesViewModel di tingkat tertinggi navigasi
    val notesViewModel: NotesViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomNavScreen = currentRoute in listOf(
        BottomNavItem.Notes.route,
        BottomNavItem.Favorites.route,
        BottomNavItem.Profile.route
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavDrawerContent(navController, drawerState, scope)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("NotesGhama") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                if (isBottomNavScreen) BottomNavBar(navController, currentRoute)
            },
            floatingActionButton = {
                // FAB hanya muncul di tab Notes
                if (currentRoute == BottomNavItem.Notes.route) {
                    FloatingActionButton(onClick = { navController.navigate(Screen.AddNote.route) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Note")
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Notes.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                // TABS
                composable(BottomNavItem.Notes.route) { NoteListScreen(navController, notesViewModel) }
                // Berikan ViewModel ke FavoritesScreen
                composable(BottomNavItem.Favorites.route) { FavoritesScreen(navController, notesViewModel) }
                composable(BottomNavItem.Profile.route) { ProfileScreen() }

                // SCREENS ARGUMENTS & NAVIGATION
                composable(
                    route = Screen.NoteDetail.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
                    NoteDetailScreen(noteId, navController, notesViewModel)
                }

                composable(Screen.AddNote.route) { AddNoteScreen(navController, notesViewModel) }

                composable(
                    route = Screen.EditNote.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
                    EditNoteScreen(noteId, navController, notesViewModel)
                }

                // Drawer Screens
                composable(Screen.Settings.route) { SettingsScreen(navController) }
            }
        }
    }
}