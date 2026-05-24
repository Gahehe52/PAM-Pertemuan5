package com.example.notesghama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notesghama.di.initKoin
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Init Context untuk NetworkMonitor & Battery
        AppContext.init(this)

        // Cek agar Koin tidak diinisialisasi 2 kali saat re-create activity
        if (GlobalContext.getOrNull() == null) {
            initKoin()
        }

        setContent { App() }
    }
}