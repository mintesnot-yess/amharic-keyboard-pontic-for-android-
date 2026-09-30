package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.engine.AmharicTransliterationEngine
import com.example.engine.BackspaceStep
import com.example.engine.CandidateItem
import com.example.engine.EnglishSuggestionEngine
import com.example.ime.KeyAction
import com.example.ime.KeyboardLayout
import com.example.ime.KeyboardOverlay
import com.example.ime.KeyboardPage
import com.example.ime.ShiftState
import com.example.ime.TransliterationMode
import com.example.preferences.KeyboardPreferences
import com.example.preferences.KeyboardPreferencesData
import com.example.ui.theme.KeyboardThemes
import com.example.ui.theme.MyApplicationTheme

/**
 * System-wide InputMethodService providing native QWERTY keyboard with
 * real-time phonetic Amharic (Ge'ez) transliteration, themes, smart clipboard,
 * and advanced customization.
 */
class AmharicInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val viewModelStore: ViewModelStore
        get() = store

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private val engine = AmharicTransliterationEngine()

    // Keyboard observable state
    private var currentPage by mutableStateOf(KeyboardPage.QWERTY)
    private var currentShift by mutableStateOf(ShiftState.OFF)
    private var currentMode by mutableStateOf(TransliterationMode.AMHARIC)
    private var currentOverlay by mutableStateOf(KeyboardOverlay.NONE)
    private var isComposing by mutableStateOf(false)
    private var composingChar by mutableStateOf("")
    private var rawBuffer by mutableStateOf("")
    private var candidates by mutableStateOf<List<CandidateItem>>(emptyList())
    private var englishWordBuffer by mutableStateOf("")
    private var englishSuggestions by mutableStateOf<List<String>>(emptyList())
    private var preferences by mutableStateOf(KeyboardPreferencesData())
    private var recentClips by mutableStateOf<List<String>>(emptyList())

    private var audioManager: AudioManager? = null
    private var vibrator: Vibrator? = null
    private var clipboardManager: ClipboardManager? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        preferences = KeyboardPreferences.load(this)
    }

    override fun onCreateInputView(): View {
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        preferences = KeyboardPreferences.load(this)

        return ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyApplicationTheme {
                    val scheme = KeyboardThemes.getScheme(preferences.themePreset)
                    KeyboardLayout(
                        page = currentPage,
                        shiftState = currentShift,
                        mode = currentMode,
                        activeOverlay = currentOverlay,
                        isComposing = isComposing,
                        composingChar = composingChar,
                        rawBuffer = rawBuffer,
                        candidates = candidates,
                        englishSuggestions = englishSuggestions,
                        recentClips = recentClips,
                        pinnedClips = preferences.pinnedClips,
                        preferences = preferences,
                        scheme = scheme,
                        onKeyAction = { action -> handleKeyAction(action) },
                        onSelectCandidate = { candidate -> handleCandidateSelection(candidate) },
                        onTogglePinClip = { clip ->
                            val updated = if (preferences.pinnedClips.contains(clip)) {
                                preferences.pinnedClips - clip
                            } else {
                                preferences.pinnedClips + clip
                            }
                            preferences = preferences.copy(pinnedClips = updated)
                            KeyboardPreferences.save(this@AmharicInputMethodService, preferences)
                        },
                        onAddCustomClip = { clip ->
                            val updated = preferences.pinnedClips + clip
                            preferences = preferences.copy(pinnedClips = updated)
                            KeyboardPreferences.save(this@AmharicInputMethodService, preferences)
                        },
                        onClearRecentClips = {
                            recentClips = emptyList()
                        }
                    )
                }
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        preferences = KeyboardPreferences.load(this)
        resetTransliterationState()
        currentPage = KeyboardPage.QWERTY
        currentShift = ShiftState.OFF
        currentOverlay = KeyboardOverlay.NONE
        // Performance optimization: Do NOT invoke clipboard IPC on every keyboard popup.
        // Recent clips are loaded lazily on demand when KeyAction.OpenClipboard is tapped.
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        commitAnyPending()
        super.onFinishInputView(finishingInput)
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        super.onDestroy()
    }

    private fun loadRecentClips() {
        try {
            val clip = clipboardManager?.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0)?.text?.toString()
                if (!text.isNullOrBlank() && !recentClips.contains(text)) {
                    recentClips = (listOf(text) + recentClips).take(10)
                }
            }
        } catch (_: Exception) {}
    }

    private fun handleKeyAction(action: KeyAction) {
        playFeedback()

        when (action) {
            is KeyAction.Text -> {
                handleTextInput(action.char)
            }
            is KeyAction.Backspace -> {
                handleBackspace()
            }
            is KeyAction.DeleteWords -> {
                handleDeleteWords(action.count)
            }
            is KeyAction.Space -> {
                handleSpace()
            }
            is KeyAction.Enter -> {
                handleEnter()
            }
            is KeyAction.Shift -> {
                currentShift = currentShift.next()
            }
            is KeyAction.SwitchPage -> {
                currentPage = action.target
                currentOverlay = KeyboardOverlay.NONE
            }
            is KeyAction.ToggleMode -> {
                commitAnyPending()
                currentMode = if (currentMode == TransliterationMode.AMHARIC) {
                    TransliterationMode.ENGLISH
                } else {
                    TransliterationMode.AMHARIC
                }
            }
            is KeyAction.SwitchIme -> {
                commitAnyPending()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showInputMethodPicker()
            }
            is KeyAction.HideKeyboard -> {
                commitAnyPending()
                requestHideSelf(0)
            }
            is KeyAction.MoveCursor -> {
                handleMoveCursor(action.offset)
            }
            is KeyAction.OpenClipboard -> {
                loadRecentClips()
                currentOverlay = KeyboardOverlay.CLIPBOARD
            }
            is KeyAction.OpenTranslation -> {
                currentOverlay = KeyboardOverlay.TRANSLATION
            }
            is KeyAction.OpenSearch -> {
                currentOverlay = KeyboardOverlay.SEARCH
            }
            is KeyAction.CloseOverlay -> {
                currentOverlay = KeyboardOverlay.NONE
            }
            is KeyAction.PasteText -> {
                commitAnyPending()
                currentInputConnection?.commitText(action.text, 1)
                currentOverlay = KeyboardOverlay.NONE
            }
            is KeyAction.OpenSettings -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("OPEN_TAB", 1) // Open Customizer tab
                }
                startActivity(intent)
            }
            is KeyAction.ToggleNumberRow -> {
                preferences = preferences.copy(showNumberRow = !preferences.showNumberRow)
                KeyboardPreferences.save(this, preferences)
            }
        }
    }

    private fun handleTextInput(text: String) {
        val ic = currentInputConnection ?: return

        if (currentMode == TransliterationMode.AMHARIC && text.length == 1 && text[0].isLetter()) {
            englishWordBuffer = ""
            englishSuggestions = emptyList()
            val ch = text[0]
            val step = engine.onCharacterInput(ch)

            if (step.committedText.isNotEmpty()) {
                ic.commitText(step.committedText, 1)
            }

            if (step.composingText.isNotEmpty()) {
                ic.setComposingText(step.composingText, 1)
                isComposing = true
                composingChar = step.composingText
                rawBuffer = step.rawBuffer
                candidates = step.candidates
            } else {
                ic.finishComposingText()
                isComposing = false
                composingChar = ""
                rawBuffer = ""
                candidates = emptyList()
            }
        } else {
            // Direct text input
            commitAnyPending()
            ic.commitText(text, 1)

            if (currentMode == TransliterationMode.ENGLISH) {
                if (text.length == 1 && text[0].isLetter()) {
                    englishWordBuffer += text
                    englishSuggestions = EnglishSuggestionEngine.getSuggestions(englishWordBuffer)
                } else {
                    englishWordBuffer = ""
                    englishSuggestions = emptyList()
                }
            }
        }

        // If shift was ONCE, revert to OFF
        if (currentShift == ShiftState.ONCE) {
            currentShift = ShiftState.OFF
        }
    }

    private fun handleBackspace() {
        val ic = currentInputConnection ?: return

        if (currentMode == TransliterationMode.AMHARIC && isComposing) {
            when (val result = engine.onBackspace()) {
                is BackspaceStep.UpdatedComposing -> {
                    ic.setComposingText(result.composingText, 1)
                    isComposing = true
                    composingChar = result.composingText
                    rawBuffer = result.rawBuffer
                    candidates = result.candidates
                }
                is BackspaceStep.ClearedComposing -> {
                    ic.setComposingText("", 0)
                    ic.finishComposingText()
                    isComposing = false
                    composingChar = ""
                    rawBuffer = ""
                    candidates = emptyList()
                }
                is BackspaceStep.PassToEditor -> {
                    val selected = ic.getSelectedText(0)
                    if (!selected.isNullOrEmpty()) {
                        ic.commitText("", 1)
                    } else {
                        val deleted = ic.deleteSurroundingText(1, 0)
                        if (!deleted) {
                            sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL)
                        }
                    }
                }
            }
        } else {
            if (currentMode == TransliterationMode.ENGLISH) {
                if (englishWordBuffer.isNotEmpty()) {
                    englishWordBuffer = englishWordBuffer.dropLast(1)
                    englishSuggestions = EnglishSuggestionEngine.getSuggestions(englishWordBuffer)
                } else {
                    englishSuggestions = emptyList()
                }
            }
            val selected = ic.getSelectedText(0)
            if (!selected.isNullOrEmpty()) {
                ic.commitText("", 1)
            } else {
                val deleted = ic.deleteSurroundingText(1, 0)
                if (!deleted) {
                    sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL)
                }
            }
        }
    }

    private fun handleDeleteWords(count: Int) {
        val ic = currentInputConnection ?: return
        commitAnyPending()

        // Get text before cursor
        val before = ic.getTextBeforeCursor(200, 0)?.toString() ?: ""
        if (before.isEmpty()) {
            ic.deleteSurroundingText(1, 0)
            return
        }

        // Split text into words to calculate deletion length
        val trimmed = before.trimEnd()
        val spacesAtEnd = before.length - trimmed.length
        val words = trimmed.split(Regex("\\s+"))

        val deleteWordCount = count.coerceAtMost(words.size)
        val wordsToDelete = words.takeLast(deleteWordCount)
        val deleteLength = wordsToDelete.sumOf { it.length } + (wordsToDelete.size - 1).coerceAtLeast(0) + spacesAtEnd

        ic.deleteSurroundingText(deleteLength.coerceAtLeast(1), 0)
    }

    private fun handleMoveCursor(offset: Int) {
        val ic = currentInputConnection ?: return
        commitAnyPending()

        if (offset > 0) {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT)
        } else if (offset < 0) {
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT)
        }
    }

    private fun handleSpace() {
        val ic = currentInputConnection ?: return
        commitAnyPending()
        englishWordBuffer = ""
        englishSuggestions = emptyList()

        if (preferences.useAmharicWordDivider && currentMode == TransliterationMode.AMHARIC) {
            ic.commitText("፡", 1)
        } else {
            ic.commitText(" ", 1)
        }
    }

    private fun handleEnter() {
        val ic = currentInputConnection ?: return
        commitAnyPending()
        englishWordBuffer = ""
        englishSuggestions = emptyList()

        val editorInfo = currentInputEditorInfo
        val imeAction = editorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
        if (imeAction != null && imeAction != EditorInfo.IME_ACTION_NONE && imeAction != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(imeAction)
        } else {
            ic.commitText("\n", 1)
        }
    }

    private fun handleCandidateSelection(candidate: String) {
        val ic = currentInputConnection ?: return
        if (currentMode == TransliterationMode.ENGLISH) {
            if (englishWordBuffer.isNotEmpty()) {
                ic.deleteSurroundingText(englishWordBuffer.length, 0)
            }
            ic.commitText("$candidate ", 1)
            englishWordBuffer = ""
            englishSuggestions = emptyList()
        } else {
            ic.commitText(candidate, 1)
            resetTransliterationState()
        }
    }

    private fun commitAnyPending() {
        val ic = currentInputConnection ?: return
        val pending = engine.commitAndReset()
        if (pending.isNotEmpty()) {
            ic.commitText(pending, 1)
        }
        ic.finishComposingText()
        resetTransliterationState()
    }

    private fun resetTransliterationState() {
        engine.reset()
        isComposing = false
        composingChar = ""
        rawBuffer = ""
        candidates = emptyList()
        englishWordBuffer = ""
        englishSuggestions = emptyList()
    }

    private fun playFeedback() {
        // "remove the click sound" - Only play audio if explicitly enabled by user
        if (preferences.soundOnClick) {
            try {
                audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.5f)
            } catch (_: Exception) {}
        }

        // Haptic feedback
        val durationMs = preferences.vibrationIntensity.durationMs
        if (durationMs > 0) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            } catch (_: Exception) {}
        }
    }
}
