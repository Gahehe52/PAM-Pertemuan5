package com.example.notesghama.di

import org.koin.dsl.module
import com.example.notesghama.db.DatabaseDriverFactory

actual val platformModule = module {
    single { DatabaseDriverFactory() }
}