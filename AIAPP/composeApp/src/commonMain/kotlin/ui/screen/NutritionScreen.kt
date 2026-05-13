package ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.viewmodel.ChatMessage
import ui.viewmodel.NutritionViewModel

val PrimaryColor = Color(0xFF40513B)
val SecondaryColor = Color(0xFF628141)
val BackgroundColor = Color(0xFFF9F9F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(viewModel: NutritionViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }

    var attachedImageBase64 by remember { mutableStateOf<String?>(null) }
    var attachedImageMime by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NutriScan AI", color = Color.White, fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor),
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Chat", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundColor)
        ) {

            if (uiState.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                if (uiState.isLoading) {
                    item { TypingIndicator() }
                }

                items(uiState.messages.reversed(), key = { it.id }) { message ->
                    ChatBubble(message = message)
                }
            }

            if (attachedImageBase64 != null) {
                Surface(
                    color = SecondaryColor.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Foto makanan siap dianalisis", fontSize = 12.sp, modifier = Modifier.weight(1f), color = PrimaryColor)
                        IconButton(onClick = {
                            attachedImageBase64 = null
                            attachedImageMime = null
                        }) {
                            Icon(Icons.Default.DeleteOutline, "Hapus lampiran", tint = PrimaryColor)
                        }
                    }
                }
            }

            Surface(
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        attachedImageMime = "image/jpeg"
                        attachedImageBase64 = "BASE64_PLACEHOLDER_UNTUK_FOTO_MAKANAN"
                    }) {
                        Icon(Icons.Default.AddBox, contentDescription = "Lampirkan Foto Makanan", tint = PrimaryColor)
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tanya kalori atau resep sehat...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SecondaryColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            viewModel.sendMessage(inputText, attachedImageBase64, attachedImageMime)
                            inputText = ""
                            attachedImageBase64 = null
                            attachedImageMime = null
                        },
                        enabled = (inputText.isNotBlank() || attachedImageBase64 != null) && !uiState.isLoading && !uiState.isStreaming
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Kirim", tint = if ((inputText.isNotBlank() || attachedImageBase64 != null) && !uiState.isLoading && !uiState.isStreaming) PrimaryColor else Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) PrimaryColor else Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (message.hasImage) {
                    Text(
                        text = "[Lampiran Foto Makanan]",
                        color = SecondaryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Text(
                    text = message.text,
                    color = if (message.isUser) Color.White else Color(0xFF333333),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Sedang menganalisis", fontSize = 12.sp, color = SecondaryColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        val infiniteTransition = rememberInfiniteTransition()
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 200)
                ),
                label = "typingIndicatorAlpha"
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(alpha)
                    .background(PrimaryColor, CircleShape)
            )
        }
    }
}