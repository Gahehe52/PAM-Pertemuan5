package com.example.notesghama.viewmodel

import app.cash.turbine.test
import com.example.notesghama.NetworkMonitor
import com.example.notesghama.db.NoteEntity
import com.example.notesghama.repository.NoteRepository
import com.example.notesghama.settings.SettingsManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {
    private lateinit var mockRepository: NoteRepository
    private lateinit var mockSettingsManager: SettingsManager
    private lateinit var mockNetworkMonitor: NetworkMonitor
    private lateinit var viewModel: NotesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        mockSettingsManager = mockk(relaxed = true)
        mockNetworkMonitor = mockk(relaxed = true)

        val mockNotes = listOf(NoteEntity(1L, "Sample", "Sample Content", 0L, 0L, 0L))
        every { mockSettingsManager.sortOrderFlow } returns MutableStateFlow(false)
        every { mockNetworkMonitor.isConnected } returns MutableStateFlow(true)
        coEvery { mockRepository.getNotes(any(), any()) } returns flowOf(mockNotes)
        coEvery { mockRepository.getFavorites() } returns flowOf(emptyList())

        viewModel = NotesViewModel(mockRepository, mockSettingsManager, mockNetworkMonitor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testUiStateEmitsLoadingThenContent() = runTest {
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertIs<NotesUiState.Loading>(loadingState)

            val contentState = awaitItem()
            assertIs<NotesUiState.Content>(contentState)
            assertEquals(1, contentState.notes.size)
            assertEquals("Sample", contentState.notes[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testSearchQueryUpdatesProperly() = runTest {
        viewModel.searchQuery.test {
            assertEquals("", awaitItem())

            viewModel.updateSearchQuery("hello")
            assertEquals("hello", awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testAddNoteCallsRepository() = runTest {
        coEvery { mockRepository.insertNote(any(), any()) } just Runs
        viewModel.addNote("Title", "Content")
        advanceUntilIdle()
        coVerify(exactly = 1) { mockRepository.insertNote(eq("Title"), eq("Content")) }
    }

    @Test
    fun testDeleteNoteCallsRepository() = runTest {
        coEvery { mockRepository.deleteNote(any()) } just Runs
        viewModel.deleteNote(12L)
        advanceUntilIdle()
        coVerify(exactly = 1) { mockRepository.deleteNote(eq(12L)) }
    }

    @Test
    fun testToggleFavoriteCallsRepository() = runTest {
        coEvery { mockRepository.toggleFavorite(any()) } just Runs
        viewModel.toggleFavorite(8L)
        advanceUntilIdle()
        coVerify(exactly = 1) { mockRepository.toggleFavorite(eq(8L)) }
    }
}