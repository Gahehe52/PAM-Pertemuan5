package com.example.notesghama.repository

import com.example.notesghama.db.NoteEntity
import com.example.notesghama.db.NoteQueries
import com.example.notesghama.db.NotesDatabase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class NoteRepositoryTest {
    private lateinit var mockDatabase: NotesDatabase
    private lateinit var mockQueries: NoteQueries
    private lateinit var mockRemoteDataSource: NoteRemoteDataSource
    private lateinit var repository: NoteRepository

    @Before
    fun setup() {
        mockDatabase = mockk(relaxed = true)
        mockQueries = mockk(relaxed = true)
        mockRemoteDataSource = mockk(relaxed = true)
        every { mockDatabase.noteQueries } returns mockQueries
        repository = NoteRepository(mockDatabase, mockRemoteDataSource)
    }

    @Test
    fun testInsertNote() = runTest {
        repository.insertNote("Test Title", "Test Content")
        verify(exactly = 1) { mockQueries.insertNote(eq("Test Title"), eq("Test Content"), any(), any(), any()) }
        coVerify(exactly = 1) { mockRemoteDataSource.createNote() }
    }

    @Test
    fun testUpdateNote() = runTest {
        repository.updateNote(1L, "Updated Title", "Updated Content")
        verify(exactly = 1) { mockQueries.updateNote(eq("Updated Title"), eq("Updated Content"), any(), eq(1L)) }
        coVerify(exactly = 1) { mockRemoteDataSource.updateNote() }
    }

    @Test
    fun testDeleteNote() = runTest {
        repository.deleteNote(10L)
        verify(exactly = 1) { mockQueries.deleteNote(eq(10L)) }
        coVerify(exactly = 1) { mockRemoteDataSource.deleteNote() }
    }

    @Test
    fun testToggleFavorite() = runTest {
        repository.toggleFavorite(5L)
        verify(exactly = 1) { mockQueries.toggleFavorite(eq(5L)) }
    }

    @Test
    fun testGetNoteById() = runTest {
        val dummyNote = NoteEntity(id = 2L, title = "A", content = "B", isFavorite = 1L, created_at = 0L, updated_at = 0L)
        every { mockQueries.selectById(2L).executeAsOneOrNull() } returns dummyNote

        val result = repository.getNoteById(2L)

        verify(exactly = 1) { mockQueries.selectById(eq(2L)) }
        assertEquals(dummyNote, result)
    }
}