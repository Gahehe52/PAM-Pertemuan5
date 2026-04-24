package com.example.notesghama.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.notesghama.navigation.BottomNavItem

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem.Notes,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Menggunakan route string langsung lebih aman di KMP terbaru
                        popUpTo(BottomNavItem.Notes.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}