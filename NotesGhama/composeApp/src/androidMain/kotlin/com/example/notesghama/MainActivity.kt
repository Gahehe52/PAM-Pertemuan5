package com.example.notesghama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notesghama.db.DatabaseDriverFactory // <-- Ini import yang tertinggal
import com.example.notesghama.di.Dependencies

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Init Database
        Dependencies.initDatabase(DatabaseDriverFactory(applicationContext))

        setContent { App() }
    }
}