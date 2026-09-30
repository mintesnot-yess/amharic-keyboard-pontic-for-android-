package com.example

import com.example.engine.AmharicDictionary
import com.example.ime.FlickAndSymbolRules
import com.example.preferences.CandidateFontSize
import com.example.preferences.KeyFontSize
import com.example.preferences.KeyShapeStyle
import com.example.preferences.KeyboardPreferencesData
import com.example.preferences.ThemePreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NewFeaturesTest {

    @Test
    fun testDictionarySearch() {
        val selamResults = AmharicDictionary.search("selam")
        assertTrue(selamResults.isNotEmpty())
        assertTrue(selamResults.any { it.amharic == "ሰላም" })

        val waterResults = AmharicDictionary.search("water")
        assertTrue(waterResults.isNotEmpty())
        assertTrue(waterResults.any { it.amharic == "ውሃ" })

        val amharicQuery = AmharicDictionary.search("አመሰግናለሁ")
        assertTrue(amharicQuery.isNotEmpty())
        assertTrue(amharicQuery.any { it.english.contains("Thank you") })
    }

    @Test
    fun testFlickAndSymbolRules() {
        assertEquals("1", FlickAndSymbolRules.FLICK_UP_MAPPINGS["q"])
        assertEquals("0", FlickAndSymbolRules.FLICK_UP_MAPPINGS["p"])
        assertEquals("@", FlickAndSymbolRules.FLICK_UP_MAPPINGS["a"])
        assertEquals("!", FlickAndSymbolRules.FLICK_UP_MAPPINGS["z"])
        assertEquals("?", FlickAndSymbolRules.FLICK_UP_MAPPINGS["x"])

        val sFamily = FlickAndSymbolRules.getGeezFamily("s")
        assertNotNull(sFamily)
        assertEquals(8, sFamily!!.size)
        assertEquals("ሰ", sFamily[0])
        assertEquals("ስ", sFamily[5])
        assertEquals("ሷ", sFamily[7])
    }

    @Test
    fun testSoundDisabledByDefault() {
        val defaultPrefs = KeyboardPreferencesData()
        assertFalse("Sound click should be false by default", defaultPrefs.soundOnClick)
        assertEquals(ThemePreset.SYSTEM_DYNAMIC, defaultPrefs.themePreset)
        assertEquals(KeyShapeStyle.ROUNDED, defaultPrefs.keyShape)
        assertEquals(KeyFontSize.NORMAL, defaultPrefs.keyFontSize)
        assertEquals(CandidateFontSize.NORMAL, defaultPrefs.candidateFontSize)
        assertTrue(defaultPrefs.flickGesturesEnabled)
        assertTrue(defaultPrefs.quickDeleteSwipeEnabled)
    }

    @Test
    fun testKeyActions() {
        val spaceAction: com.example.ime.KeyAction = com.example.ime.KeyAction.Space
        val backspaceAction: com.example.ime.KeyAction = com.example.ime.KeyAction.Backspace
        val deleteWordsAction: com.example.ime.KeyAction = com.example.ime.KeyAction.DeleteWords(2)

        assertTrue(spaceAction is com.example.ime.KeyAction.Space)
        assertTrue(backspaceAction is com.example.ime.KeyAction.Backspace)
        assertEquals(2, (deleteWordsAction as com.example.ime.KeyAction.DeleteWords).count)
    }
}
