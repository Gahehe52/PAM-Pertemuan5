import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// 1. Sealed class untuk routes
sealed class ScreenLatihan2(val route: String) {
    object NoteList : ScreenLatihan2("note_list")
    object NoteDetail : ScreenLatihan2("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }
}

@Composable
fun AppNavigationLatihan2() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ScreenLatihan2.NoteList.route
    ) {
        composable(ScreenLatihan2.NoteList.route) {
            NoteListScreen(
                onNoteClick = { noteId ->
                    // Navigate dengan noteId
                    navController.navigate(ScreenLatihan2.NoteDetail.createRoute(noteId))
                }
            )
        }
        
        composable(
            route = ScreenLatihan2.NoteDetail.route,
            // Setup navArgument
            arguments = listOf(
                navArgument("noteId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // Mengambil argument
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
            NoteDetailScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun NoteListScreen(onNoteClick: (Int) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(5) { index ->
            val id = index + 1
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    // Clickable list item
                    .clickable { onNoteClick(id) }
            ) {
                Text(
                    text = "Catatan #$id", 
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun NoteDetailScreen(noteId: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Menampilkan noteId
        Text("Detail Catatan", style = MaterialTheme.typography.headlineMedium)
        Text("Anda sedang melihat catatan dengan ID: $noteId", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Kembali")
        }
    }
}