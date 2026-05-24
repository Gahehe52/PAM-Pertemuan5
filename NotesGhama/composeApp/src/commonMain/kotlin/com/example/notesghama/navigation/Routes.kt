package com.example.notesghama.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object NoteList : Screen("note_list")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object AddNote : Screen("add_note")

    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }

    object EditNote : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: Int) = "edit_note/$noteId"
    }
}

// Menu untuk Bottom Nav dan Drawer
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(Screen.NoteList.route, Icons.Default.Home, "Beranda")
    object Favorites : BottomNavItem(Screen.Favorites.route, Icons.Default.Favorite, "Favorit")
    object Profile : BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profil")
}