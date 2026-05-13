package com.ghama.notes.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.ghama.notes.domain.Note
import org.junit.Rule
import org.junit.Test

class NotesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsMessage() {
        // Arrange
        val emptyState = NotesUiState.Success(emptyList())

        // Act
        composeTestRule.setContent {
            NotesScreen(
                uiState = emptyState,
                onAddNote = { _, _ -> },
                onDeleteNote = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithTag(TestTags.EMPTY_STATE).assertIsDisplayed()
    }

    @Test
    fun notesList_showsNotes() {
        // Arrange
        val notes = listOf(Note(id = 1L, title = "Tugas KMP", content = "Selesaikan Praktikum 10"))
        val successState = NotesUiState.Success(notes)

        // Act
        composeTestRule.setContent {
            NotesScreen(
                uiState = successState,
                onAddNote = { _, _ -> },
                onDeleteNote = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Tugas KMP").assertIsDisplayed()
        composeTestRule.onNodeWithText("Selesaikan Praktikum 10").assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.EMPTY_STATE).assertDoesNotExist()
    }

    @Test
    fun addNoteInteraction_callsCallback() {
        // Arrange
        var addedTitle = ""
        var addedContent = ""
        val initialState = NotesUiState.Success(emptyList())

        // Act
        composeTestRule.setContent {
            NotesScreen(
                uiState = initialState,
                onAddNote = { title, content ->
                    addedTitle = title
                    addedContent = content
                },
                onDeleteNote = {}
            )
        }

        composeTestRule.onNodeWithTag(TestTags.TITLE_INPUT).performTextInput("New Title")
        composeTestRule.onNodeWithTag(TestTags.CONTENT_INPUT).performTextInput("New Content")
        composeTestRule.onNodeWithTag(TestTags.ADD_BUTTON).performClick()

        // Assert
        assert(addedTitle == "New Title")
        assert(addedContent == "New Content")
    }

    @Test
    fun deleteNoteInteraction_callsCallback() {
        // Arrange
        var deletedId = -1L
        val notes = listOf(Note(id = 5L, title = "To Delete", content = "Delete this"))
        val state = NotesUiState.Success(notes)

        // Act
        composeTestRule.setContent {
            NotesScreen(
                uiState = state,
                onAddNote = { _, _ -> },
                onDeleteNote = { id -> deletedId = id }
            )
        }

        composeTestRule.onNodeWithTag(TestTags.DELETE_BUTTON + "5").performClick()

        // Assert
        assert(deletedId == 5L)
    }
}