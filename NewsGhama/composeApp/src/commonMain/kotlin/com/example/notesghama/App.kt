package com.example.notesghama

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notesghama.repository.NewsRepository
import com.example.notesghama.screens.NewsDetailScreen
import com.example.notesghama.screens.NewsListScreen
import com.example.notesghama.viewmodel.NewsViewModel

val CustomPrimaryColor = Color(0xFF7B8CB6)

private val LightColorScheme = lightColorScheme(
    primary = CustomPrimaryColor,
    primaryContainer = CustomPrimaryColor,
    onPrimaryContainer = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = CustomPrimaryColor,
    primaryContainer = CustomPrimaryColor,
    onPrimaryContainer = Color.White
)

@Composable
fun App() {
    val colors = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme

    MaterialTheme(colorScheme = colors) {
        val navController = rememberNavController()
        val repo = remember { NewsRepository() }
        val vm: NewsViewModel = viewModel { NewsViewModel(repo) }

        NavHost(navController, "list") {
            composable("list") { NewsListScreen(navController, vm) }
            composable(
                "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { b ->
                NewsDetailScreen(navController, b.arguments?.getInt("id") ?: 0, vm)
            }
        }
    }
}