package com.example.notesghama.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesghama.model.NewsArticle
import com.example.notesghama.viewmodel.NewsState
import com.example.notesghama.viewmodel.NewsViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(navController: NavController, viewModel: NewsViewModel) {
    val state by viewModel.state.collectAsState()
    val refreshing by viewModel.refreshing.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("News Reader") }) }
    ) { p ->
        // Menggunakan PullToRefreshBox (API Terbaru yang jauh lebih ringkas)
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(p).fillMaxSize()
        ) {
            when (val s = state) {
                is NewsState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                is NewsState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Button(onClick = { viewModel.fetch() }) { Text("Coba Lagi") }
                }
                is NewsState.Success -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        itemsIndexed(s.data) { i, a ->
                            NewsCard(a) { navController.navigate("detail/$i") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsCard(article: NewsArticle, onClick: () -> Unit) {
    Card(Modifier.padding(8.dp).fillMaxWidth().clickable { onClick() }) {
        Column {
            article.urlToImage?.let {
                KamelImage(
                    resource = asyncPainterResource(it),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(Modifier.padding(16.dp)) {
                Text(article.title ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(article.description ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 3)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(navController: NavController, index: Int, viewModel: NewsViewModel) {
    val state by viewModel.state.collectAsState()
    val article = (state as? NewsState.Success)?.data?.getOrNull(index)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { p ->
        article?.let { a ->
            Column(Modifier.padding(p)) {
                a.urlToImage?.let {
                    KamelImage(
                        resource = asyncPainterResource(it),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(Modifier.padding(16.dp)) {
                    Text(a.title ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))
                    Text(a.content ?: a.description ?: "")
                }
            }
        }
    }
}