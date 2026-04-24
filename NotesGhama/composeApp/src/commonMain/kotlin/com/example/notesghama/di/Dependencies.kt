package com.example.notesghama.di

import com.example.notesghama.db.NotesDatabase
import com.example.notesghama.repository.NoteRemoteDataSource
import com.example.notesghama.repository.NoteRepository
import com.example.notesghama.settings.SettingsManager

object Dependencies {
    private var database: NotesDatabase? = null

    fun initDatabase(driverFactory: DatabaseDriverFactory) {
        if (database == null) {
            database = NotesDatabase(driverFactory.createDriver())
        }
    }

    val repository: NoteRepository by lazy {
        NoteRepository(database!!, NoteRemoteDataSource())
    }

    val settingsManager: SettingsManager by lazy {
        SettingsManager()
    }
}