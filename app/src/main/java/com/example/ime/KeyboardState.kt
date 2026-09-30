package com.example.ime

/**
 * State for shift / uppercase.
 */
enum class ShiftState {
    OFF,
    ONCE,
    LOCKED;

    fun next(): ShiftState = when (this) {
        OFF -> ONCE
        ONCE -> LOCKED
        LOCKED -> OFF
    }
}

/**
 * Page layout mode for keyboard.
 */
enum class KeyboardPage {
    QWERTY,
    SYMBOLS,
    MORE_SYMBOLS,
    GEEZ_PUNCTUATION,
    CLIPBOARD,
    TRANSLATE_SEARCH
}

/**
 * Typing language mode.
 */
enum class TransliterationMode {
    AMHARIC,
    ENGLISH
}

/**
 * Active panel/overlay on top of the keyboard.
 */
enum class KeyboardOverlay {
    NONE,
    CLIPBOARD,
    TRANSLATION,
    SEARCH
}

/**
 * Sealed class representing keyboard actions.
 */
sealed class KeyAction {
    data class Text(val char: String) : KeyAction()
    data object Backspace : KeyAction()
    data class DeleteWords(val count: Int = 1) : KeyAction()
    data object Shift : KeyAction()
    data object Enter : KeyAction()
    data object Space : KeyAction()
    data class SwitchPage(val target: KeyboardPage) : KeyAction()
    data object ToggleMode : KeyAction() // Toggle between Amharic and English
    data object SwitchIme : KeyAction()  // Switch to next system keyboard
    data object HideKeyboard : KeyAction()
    data class MoveCursor(val offset: Int) : KeyAction()
    data object OpenClipboard : KeyAction()
    data object OpenTranslation : KeyAction()
    data object OpenSearch : KeyAction()
    data object OpenSettings : KeyAction()
    data object ToggleNumberRow : KeyAction()
    data class PasteText(val text: String) : KeyAction()
    data object CloseOverlay : KeyAction()
}
