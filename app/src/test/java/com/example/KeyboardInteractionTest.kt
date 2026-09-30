package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.ime.KeyAction
import com.example.ime.KeyboardKey
import com.example.ime.TOP_NUMERIC_ROW_ITEMS
import com.example.preferences.KeyboardPreferences
import com.example.preferences.KeyboardPreferencesData
import com.example.ui.theme.KeyboardThemes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class KeyboardInteractionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSpacebarClickCallsAction() {
        var actionReceived: KeyAction? = null
        val scheme = KeyboardThemes.AbyssinianHeritage

        composeTestRule.setContent {
            KeyboardKey(
                action = KeyAction.Space,
                label = "Space",
                isSpacebar = true,
                scheme = scheme,
                onAction = { action -> actionReceived = action }
            )
        }

        composeTestRule.onNodeWithTag("key_space").performClick()
        assertEquals(KeyAction.Space, actionReceived)
    }

    @Test
    fun testBackspaceClickCallsAction() {
        var actionReceived: KeyAction? = null
        val scheme = KeyboardThemes.AbyssinianHeritage

        composeTestRule.setContent {
            KeyboardKey(
                action = KeyAction.Backspace,
                label = "⌫",
                isSpecial = true,
                scheme = scheme,
                onAction = { action -> actionReceived = action }
            )
        }

        composeTestRule.onNodeWithTag("key_⌫").performClick()
        assertEquals(KeyAction.Backspace, actionReceived)
    }

    @Test
    fun testTopNumericRowHasAddressAndDateSymbols() {
        assertEquals(10, TOP_NUMERIC_ROW_ITEMS.size)
        // Check date separators and address symbols
        assertEquals("#", TOP_NUMERIC_ROW_ITEMS.first { it.digit == "1" }.symbol)
        assertEquals("/", TOP_NUMERIC_ROW_ITEMS.first { it.digit == "2" }.symbol)
        assertEquals("-", TOP_NUMERIC_ROW_ITEMS.first { it.digit == "3" }.symbol)
        assertEquals(":", TOP_NUMERIC_ROW_ITEMS.first { it.digit == "6" }.symbol)
        assertEquals(".", TOP_NUMERIC_ROW_ITEMS.first { it.digit == "7" }.symbol)
        assertEquals(",", TOP_NUMERIC_ROW_ITEMS.first { it.digit == "8" }.symbol)
    }

    @Test
    fun testToggleNumberRowPreference() {
        val defaultPrefs = KeyboardPreferencesData(showNumberRow = false)
        val toggled = defaultPrefs.copy(showNumberRow = !defaultPrefs.showNumberRow)
        assertTrue(toggled.showNumberRow)
    }

    @Test
    fun testEnglishSuggestionEngine() {
        val suggestions = com.example.engine.EnglishSuggestionEngine.getSuggestions("hel")
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.contains("hello") || suggestions.contains("help"))

        val capitalized = com.example.engine.EnglishSuggestionEngine.getSuggestions("Hel")
        assertTrue(capitalized.first()[0].isUpperCase())
    }

    @Test
    fun testClipboardHasNoPresetSuggestions() {
        assertTrue(KeyboardPreferencesData.DEFAULT_PINNED_CLIPS.isEmpty())
    }
}
