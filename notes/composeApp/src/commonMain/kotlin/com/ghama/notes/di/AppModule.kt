package com.ghama.notes.di

import com.ghama.notes.domain.NoteRepository
import com.ghama.notes.domain.NoteRepositoryImpl
import com.ghama.notes.presentation.NotesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single<NoteRepository> { NoteRepositoryImpl() }
}

val viewModelModule = module {
    viewModel { NotesViewModel(get()) }
}

val allModules = listOf(dataModule, viewModelModule)