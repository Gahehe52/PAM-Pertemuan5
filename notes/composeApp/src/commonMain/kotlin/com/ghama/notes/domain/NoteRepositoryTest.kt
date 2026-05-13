package com.ghama.notes.domain

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoteRepositoryTest {

    private lateinit var repository: NoteRepositoryImpl

    @BeforeTest
    fun setup() {
        // Arrange
        repository = NoteRepositoryImpl()
    }

    @Test
    fun `getAllNotes initially emits empty list`() = runTest {
        // Act & Assert (Menggunakan Turbine)
        repository.getAllNotes().test {
            val initialState = awaitItem()
            assertTrue(initialState.isEmpty(), "Initial notes should be empty")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertNote adds note to the flow`() = runTest {
        // Arrange
        val note = Note(title = "Test Title", content = "Test Content")

        // Act & Assert (Menggunakan Turbine)
        repository.getAllNotes().test {
            awaitItem() // Skip inisialisasi list kosong

            repository.insertNote(note)

            val updatedList = awaitItem()
            assertEquals(1, updatedList.size)
            assertEquals("Test Title", updatedList.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNote removes note from the flow`() = runTest {
        // Arrange
        val note = Note(title = "Title to delete", content = "Content")
        repository.insertNote(note)

        // Act & Assert
        repository.getAllNotes().test {
            val initialList = awaitItem()
            assertEquals(1, initialList.size)

            val noteId = initialList.first().id
            repository.deleteNote(noteId)

            val deletedList = awaitItem()
            assertTrue(deletedList.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `inserting multiple notes increases list size correctly`() = runTest {
        // Arrange
        val note1 = Note(title = "1", content = "A")
        val note2 = Note(title = "2", content = "B")

        // Act & Assert
        repository.getAllNotes().test {
            awaitItem()

            repository.insertNote(note1)
            assertEquals(1, awaitItem().size)

            repository.insertNote(note2)
            val finalItems = awaitItem()
            assertEquals(2, finalItems.size)
            assertEquals("2", finalItems.last().title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleting non-existent note does not crash and leaves list unchanged`() = runTest {
        // Arrange
        val note = Note(title = "Keep", content = "Me")
        repository.insertNote(note)

        // Act & Assert
        repository.getAllNotes().test {
            val initialList = awaitItem()
            assertEquals(1, initialList.size)

            // Act: Hapus ID yang tidak pernah di-insert
            repository.deleteNote(999L)

            // Assert: Turbine mendeteksi tidak ada event baru jika state tidak berubah
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}