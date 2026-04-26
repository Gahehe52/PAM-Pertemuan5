package com.example.notesghama.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import com.example.notesghama.db.DatabaseDriverFactory
import com.example.notesghama.db.NotesDatabase
import com.example.notesghama.repository.NoteRemoteDataSource
import com.example.notesghama.repository.NoteRepository
import com.example.notesghama.settings.SettingsManager
import com.example.notesghama.DeviceInfo
import com.example.notesghama.NetworkMonitor
import com.example.notesghama.BatteryInfo
import com.example.notesghama.viewmodel.NotesViewModel

expect val platformModule: Module

val sharedModule = module {
    single { NoteRemoteDataSource() }
    single { SettingsManager() }
    single {
        val driverFactory = get<DatabaseDriverFactory>()
        NotesDatabase(driverFactory.createDriver())
    }
    single { NoteRepository(get(), get()) }

    // Platform Features
    factory { DeviceInfo() }
    factory { NetworkMonitor() }
    factory { BatteryInfo() }

    // ViewModel
    factory { NotesViewModel(get(), get(), get()) }
}

fun initKoin() = startKoin {
    modules(platformModule, sharedModule)
}