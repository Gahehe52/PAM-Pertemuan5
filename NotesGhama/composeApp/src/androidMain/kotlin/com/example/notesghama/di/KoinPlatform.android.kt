package com.example.notesghama.di

import org.koin.dsl.module
import com.example.notesghama.db.DatabaseDriverFactory
import com.example.notesghama.AppContext

actual val platformModule = module {
    single { DatabaseDriverFactory(AppContext.get()) }
}