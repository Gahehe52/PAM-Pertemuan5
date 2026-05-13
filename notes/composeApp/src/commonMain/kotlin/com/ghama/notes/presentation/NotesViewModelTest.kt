package com.ghama.notes.presentation

import app.cash.turbine.test
import com.ghama.notes.domain.Note
import com.ghama.notes.domain.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private lateinit var mockRepo: NoteRepository
    private lateinit var viewModel: NotesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepo = mockk()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state emits loading then success`() = runTest {
        // Arrange
        val testNote = Note(id = 1L, title = "Test", content = "Content")
        coEvery { mockRepo.getAllNotes() } returns flowOf(listOf(testNote))

        // Act
        viewModel = NotesViewModel(mockRepo)

        // Assert (Turbine Flow Test)
        viewModel.uiState.test {
            assertIs<NotesUiState.Loading>(awaitItem())

            val successState = awaitItem()
            assertIs<NotesUiState.Success>(successState)
            assertEquals(1, successState.notes.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `repository error emits Error state`() = runTest {
        // Arrange
        coEvery { mockRepo.getAllNotes() } returns flow { throw Exception("Database error") }

        // Act
        viewModel = NotesViewModel(mockRepo)

        // Assert
        viewModel.uiState.test {
            assertIs<NotesUiState.Loading>(awaitItem())

            val errorState = awaitItem()
            assertIs<NotesUiState.Error>(errorState)
            assertEquals("Database error", errorState.message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addNote calls repository insertNote when title is not blank`() = runTest {
        // Arrange
        coEvery { mockRepo.getAllNotes() } returns flowOf(emptyList())
        coEvery { mockRepo.insertNote(any()) } just runs
        viewModel = NotesViewModel(mockRepo)

        // Act
        viewModel.addNote("New Title", "New Content")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            mockRepo.insertNote(match { it.title == "New Title" && it.content == "New Content" })
        }
    }

    @Test
    fun `addNote does not call repository when title is blank`() = runTest {
        // Arrange
        coEvery { mockRepo.getAllNotes() } returns flowOf(emptyList())
        viewModel = NotesViewModel(mockRepo)

        // Act
        viewModel.addNote("", "Some content")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 0) { mockRepo.insertNote(any()) }
    }

    @Test
    fun `deleteNote calls repository deleteNote`() = runTest {
        // Arrange
        coEvery { mockRepo.getAllNotes() } returns flowOf(emptyList())
        coEvery { mockRepo.deleteNote(any()) } just runs
        viewModel = NotesViewModel(mockRepo)

        // Act
        viewModel.deleteNote(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { mockRepo.deleteNote(1L) }
    }
}