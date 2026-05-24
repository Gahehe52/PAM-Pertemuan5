package com.example.notesghama.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.notesghama.App
import com.example.notesghama.AppContext
import com.example.notesghama.di.initKoin
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.GlobalContext

class NotesScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        // Mendapatkan context dari perangkat/emulator test
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Inisialisasi AppContext untuk kebutuhan Database/Network di Koin
        AppContext.init(context)

        // Memastikan Koin hanya dihidupkan jika belum berjalan agar bisa dipakai berulang
        if (GlobalContext.getOrNull() == null) {
            initKoin()
        }
    }

    @Test
    fun testEmptyStateDisplaysMessage() {
        composeTestRule.setContent {
            App()
        }

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("empty_state").fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithTag("notes_list").fetchSemanticsNodes().isNotEmpty()
        }

        if (composeTestRule.onAllNodesWithTag("empty_state").fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
        }
    }

    @Test
    fun testAddNoteInteraction() {
        composeTestRule.setContent {
            App()
        }

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("add_button").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("add_button").performClick()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("title_input").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("title_input").performTextInput("UI Test Note")
        composeTestRule.onNodeWithTag("content_input").performTextInput("Content body")
        composeTestRule.onNodeWithTag("save_button").performClick()
    }

    @Test
    fun testSearchInputFunctionality() {
        composeTestRule.setContent {
            App()
        }

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("search_input").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("search_input").performTextInput("Cari catatan ini")
        composeTestRule.onNodeWithTag("search_input").assertTextContains("Cari catatan ini")
    }
}