package com.example.ime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.engine.CandidateItem
import com.example.preferences.KeyboardPreferencesData
import com.example.ui.theme.KeyboardColorScheme

// Pre-allocated static lists for keyboard layouts to eliminate popup lag and garbage collection
data class NumericKeyItem(val digit: String, val symbol: String)

val TOP_NUMERIC_ROW_ITEMS = listOf(
    NumericKeyItem("1", "#"), // Apt / Unit / Address #
    NumericKeyItem("2", "/"), // Date slash (YYYY/MM/DD)
    NumericKeyItem("3", "-"), // Date/Phone hyphen
    NumericKeyItem("4", "@"), // Address / Email
    NumericKeyItem("5", "%"),
    NumericKeyItem("6", ":"), // Time / Date colon
    NumericKeyItem("7", "."), // Date dot (DD.MM.YYYY)
    NumericKeyItem("8", ","), // Address comma (Street, City)
    NumericKeyItem("9", "("),
    NumericKeyItem("0", ")")
)

val QWERTY_ROW_1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
val QWERTY_ROW_2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
val QWERTY_ROW_3 = listOf("z", "x", "c", "v", "b", "n", "m")

// Sub-labels for English QWERTY keys showing their primary Amharic mapping
val AMHARIC_KEY_SUB_LABELS = mapOf(
    "q" to "ቅ", "w" to "ው", "e" to "እ", "r" to "ር", "t" to "ት",
    "y" to "ይ", "u" to "ኡ", "i" to "ኢ", "o" to "ኦ", "p" to "ፕ",
    "a" to "አ", "s" to "ስ", "d" to "ድ", "f" to "ፍ", "g" to "ግ",
    "h" to "ህ", "j" to "ጅ", "k" to "ክ", "l" to "ል",
    "z" to "ዝ", "x" to "ሽ", "c" to "ች", "v" to "ቭ", "b" to "ብ",
    "n" to "ን", "m" to "ም"
)

@Composable
fun KeyboardLayout(
    page: KeyboardPage,
    shiftState: ShiftState,
    mode: TransliterationMode,
    activeOverlay: KeyboardOverlay = KeyboardOverlay.NONE,
    isComposing: Boolean,
    composingChar: String,
    rawBuffer: String,
    candidates: List<CandidateItem>,
    recentClips: List<String> = emptyList(),
    pinnedClips: Set<String> = KeyboardPreferencesData.DEFAULT_PINNED_CLIPS,
    preferences: KeyboardPreferencesData = KeyboardPreferencesData(),
    scheme: KeyboardColorScheme,
    onKeyAction: (KeyAction) -> Unit,
    onSelectCandidate: (String) -> Unit,
    onTogglePinClip: (String) -> Unit = {},
    onAddCustomClip: (String) -> Unit = {},
    onClearRecentClips: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("keyboard_layout"),
        color = scheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .navigationBarsPadding()
        ) {
            // 1. Keyboard Top Toolbar (Language, Clipboard, Translate, Search, Ge'ez, Settings)
            KeyboardToolbar(
                mode = mode,
                activeOverlay = activeOverlay,
                isNumberRowActive = preferences.showNumberRow,
                scheme = scheme,
                onAction = onKeyAction
            )

            // 2. Candidate Strip (when no full overlay is active)
            if (activeOverlay == KeyboardOverlay.NONE) {
                KeyboardCandidateBar(
                    isComposing = isComposing,
                    composingChar = composingChar,
                    rawBuffer = rawBuffer,
                    candidates = candidates,
                    mode = mode,
                    scheme = scheme,
                    fontSize = preferences.candidateFontSize,
                    onSelectCandidate = onSelectCandidate
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // 3. Main Content: Either an active overlay OR the keyboard keys
            when (activeOverlay) {
                KeyboardOverlay.CLIPBOARD -> {
                    ClipboardPanel(
                        recentClips = recentClips,
                        pinnedClips = pinnedClips,
                        scheme = scheme,
                        onPasteClip = { onKeyAction(KeyAction.PasteText(it)) },
                        onTogglePin = onTogglePinClip,
                        onAddCustomClip = onAddCustomClip,
                        onClearRecent = onClearRecentClips,
                        onClose = { onKeyAction(KeyAction.CloseOverlay) }
                    )
                }
                KeyboardOverlay.TRANSLATION -> {
                    TranslationSearchPanel(
                        initialIsSearch = false,
                        scheme = scheme,
                        onInsertText = { onKeyAction(KeyAction.PasteText(it)) },
                        onClose = { onKeyAction(KeyAction.CloseOverlay) }
                    )
                }
                KeyboardOverlay.SEARCH -> {
                    TranslationSearchPanel(
                        initialIsSearch = true,
                        scheme = scheme,
                        onInsertText = { onKeyAction(KeyAction.PasteText(it)) },
                        onClose = { onKeyAction(KeyAction.CloseOverlay) }
                    )
                }
                KeyboardOverlay.NONE -> {
                    // Regular Key Pages
                    when (page) {
                        KeyboardPage.QWERTY -> QwertyPage(
                            shiftState = shiftState,
                            mode = mode,
                            preferences = preferences,
                            scheme = scheme,
                            onKeyAction = onKeyAction
                        )
                        KeyboardPage.SYMBOLS -> SymbolsPage(
                            mode = mode,
                            preferences = preferences,
                            scheme = scheme,
                            onKeyAction = onKeyAction
                        )
                        KeyboardPage.MORE_SYMBOLS -> MoreSymbolsPage(
                            preferences = preferences,
                            scheme = scheme,
                            onKeyAction = onKeyAction
                        )
                        KeyboardPage.GEEZ_PUNCTUATION -> GeezPage(
                            preferences = preferences,
                            scheme = scheme,
                            onKeyAction = onKeyAction
                        )
                        else -> QwertyPage(
                            shiftState = shiftState,
                            mode = mode,
                            preferences = preferences,
                            scheme = scheme,
                            onKeyAction = onKeyAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QwertyPage(
    shiftState: ShiftState,
    mode: TransliterationMode,
    preferences: KeyboardPreferencesData,
    scheme: KeyboardColorScheme,
    onKeyAction: (KeyAction) -> Unit
) {
    val isShifted = shiftState != ShiftState.OFF

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
    ) {
        // Optional Dedicated Number Row for Addresses and Dates
        if (preferences.showNumberRow) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 1.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TOP_NUMERIC_ROW_ITEMS.forEach { item ->
                    KeyboardKey(
                        action = KeyAction.Text(item.digit),
                        label = item.digit,
                        subLabel = item.symbol,
                        flickSymbol = item.symbol,
                        scheme = scheme,
                        keyShape = preferences.keyShape,
                        hasBorder = preferences.keyBorders,
                        fontSize = preferences.keyFontSize,
                        clickEffect = preferences.clickEffect,
                        flickEnabled = preferences.flickGesturesEnabled,
                        modifier = Modifier.weight(1f),
                        onAction = onKeyAction
                    )
                }
            }
        }

        // Row 1 (Q - P)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            QWERTY_ROW_1.forEach { k ->
                val displayChar = if (isShifted) k.uppercase() else k
                val sub = if (mode == TransliterationMode.AMHARIC) AMHARIC_KEY_SUB_LABELS[k] else null
                val flick = FlickAndSymbolRules.FLICK_UP_MAPPINGS[k]
                KeyboardKey(
                    action = KeyAction.Text(displayChar),
                    label = displayChar,
                    subLabel = sub,
                    flickSymbol = flick,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    flickEnabled = preferences.flickGesturesEnabled,
                    modifier = Modifier.weight(1f),
                    onAction = onKeyAction
                )
            }
        }

        // Row 2 (A - L)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QWERTY_ROW_2.forEach { k ->
                val displayChar = if (isShifted) k.uppercase() else k
                val sub = if (mode == TransliterationMode.AMHARIC) AMHARIC_KEY_SUB_LABELS[k] else null
                val flick = FlickAndSymbolRules.FLICK_UP_MAPPINGS[k]
                KeyboardKey(
                    action = KeyAction.Text(displayChar),
                    label = displayChar,
                    subLabel = sub,
                    flickSymbol = flick,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    flickEnabled = preferences.flickGesturesEnabled,
                    modifier = Modifier.weight(1f),
                    onAction = onKeyAction
                )
            }
        }

        // Row 3 (Shift, Z - M, Backspace)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            KeyboardKey(
                action = KeyAction.Shift,
                label = if (shiftState == ShiftState.LOCKED) "⇪" else "⇧",
                isSpecial = true,
                isActive = shiftState != ShiftState.OFF,
                scheme = scheme,
                keyShape = preferences.keyShape,
                hasBorder = preferences.keyBorders,
                fontSize = preferences.keyFontSize,
                clickEffect = preferences.clickEffect,
                modifier = Modifier.weight(1.4f),
                onAction = onKeyAction
            )

            QWERTY_ROW_3.forEach { k ->
                val displayChar = if (isShifted) k.uppercase() else k
                val sub = if (mode == TransliterationMode.AMHARIC) AMHARIC_KEY_SUB_LABELS[k] else null
                val flick = FlickAndSymbolRules.FLICK_UP_MAPPINGS[k]
                KeyboardKey(
                    action = KeyAction.Text(displayChar),
                    label = displayChar,
                    subLabel = sub,
                    flickSymbol = flick,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    flickEnabled = preferences.flickGesturesEnabled,
                    modifier = Modifier.weight(1f),
                    onAction = onKeyAction
                )
            }

            KeyboardKey(
                action = KeyAction.Backspace,
                label = "⌫",
                isSpecial = true,
                scheme = scheme,
                keyShape = preferences.keyShape,
                hasBorder = preferences.keyBorders,
                fontSize = preferences.keyFontSize,
                clickEffect = preferences.clickEffect,
                quickDeleteEnabled = preferences.quickDeleteSwipeEnabled,
                modifier = Modifier.weight(1.4f),
                onAction = onKeyAction
            )
        }

        // Row 4 (Bottom controls)
        // CRITICAL REQUIREMENT: "add comma full stop etc.... when change the langueg to english"
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // ?123 key
            KeyboardKey(
                action = KeyAction.SwitchPage(KeyboardPage.SYMBOLS),
                label = "?123",
                isSpecial = true,
                scheme = scheme,
                keyShape = preferences.keyShape,
                hasBorder = preferences.keyBorders,
                fontSize = preferences.keyFontSize,
                clickEffect = preferences.clickEffect,
                modifier = Modifier.weight(1.3f),
                onAction = onKeyAction
            )

            if (mode == TransliterationMode.ENGLISH) {
                // In English mode: DIRECT COMMA KEY on the left of spacebar!
                KeyboardKey(
                    action = KeyAction.Text(","),
                    label = ",",
                    isSpecial = true,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    modifier = Modifier.weight(0.9f),
                    onAction = onKeyAction
                )
            } else {
                // In Amharic mode: Ge'ez Numerals button
                KeyboardKey(
                    action = KeyAction.SwitchPage(KeyboardPage.GEEZ_PUNCTUATION),
                    label = "፩፪፫",
                    isSpecial = true,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    modifier = Modifier.weight(0.9f),
                    onAction = onKeyAction
                )
            }

            // Mode Toggle (Amharic <-> English)
            KeyboardKey(
                action = KeyAction.ToggleMode,
                label = if (mode == TransliterationMode.AMHARIC) "አማ" else "EN",
                isSpecial = true,
                isActive = mode == TransliterationMode.AMHARIC,
                scheme = scheme,
                keyShape = preferences.keyShape,
                hasBorder = preferences.keyBorders,
                fontSize = preferences.keyFontSize,
                clickEffect = preferences.clickEffect,
                modifier = Modifier.weight(1.0f),
                onAction = onKeyAction
            )

            // Spacebar (with swipe to move cursor)
            KeyboardKey(
                action = KeyAction.Space,
                label = if (mode == TransliterationMode.AMHARIC) "ክፍተት" else "Space",
                isSpacebar = true,
                scheme = scheme,
                keyShape = preferences.keyShape,
                hasBorder = preferences.keyBorders,
                fontSize = preferences.keyFontSize,
                clickEffect = preferences.clickEffect,
                spacebarSwipeEnabled = preferences.spacebarSwipeCursorEnabled,
                modifier = Modifier.weight(3.3f),
                onAction = onKeyAction
            )

            if (mode == TransliterationMode.ENGLISH) {
                // In English mode: DIRECT FULL STOP (PERIOD) KEY on the right of spacebar!
                KeyboardKey(
                    action = KeyAction.Text("."),
                    label = ".",
                    isSpecial = true,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    modifier = Modifier.weight(0.9f),
                    onAction = onKeyAction
                )

                // Additional quick English punctuation: ? or !
                KeyboardKey(
                    action = KeyAction.Text("?"),
                    label = "?",
                    subLabel = "!",
                    isSpecial = true,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    modifier = Modifier.weight(0.9f),
                    onAction = onKeyAction
                )
            } else {
                // In Amharic mode: Quick Amharic punctuation key (፡ / ።)
                KeyboardKey(
                    action = KeyAction.Text("፡"),
                    label = "፡",
                    subLabel = "።",
                    isSpecial = true,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    modifier = Modifier.weight(0.9f),
                    onAction = onKeyAction
                )

                // Quick Netela Serez (፣)
                KeyboardKey(
                    action = KeyAction.Text("፣"),
                    label = "፣",
                    subLabel = "፤",
                    isSpecial = true,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    modifier = Modifier.weight(0.9f),
                    onAction = onKeyAction
                )
            }

            // Enter key
            KeyboardKey(
                action = KeyAction.Enter,
                label = "↵",
                isSpecial = true,
                isActive = true,
                scheme = scheme,
                keyShape = preferences.keyShape,
                hasBorder = preferences.keyBorders,
                fontSize = preferences.keyFontSize,
                clickEffect = preferences.clickEffect,
                modifier = Modifier.weight(1.3f),
                onAction = onKeyAction
            )
        }
    }
}

@Composable
private fun SymbolsPage(
    mode: TransliterationMode,
    preferences: KeyboardPreferencesData,
    scheme: KeyboardColorScheme,
    onKeyAction: (KeyAction) -> Unit
) {
    val row1 = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val row2 = listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/")
    val row3 = listOf("*", "\"", "'", ":", ";", "!", "?")

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            row1.forEach { k ->
                KeyboardKey(
                    action = KeyAction.Text(k),
                    label = k,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    modifier = Modifier.weight(1f),
                    onAction = onKeyAction
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            row2.forEach { k ->
                KeyboardKey(
                    action = KeyAction.Text(k),
                    label = k,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    modifier = Modifier.weight(1f),
                    onAction = onKeyAction
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            KeyboardKey(
                action = KeyAction.SwitchPage(KeyboardPage.MORE_SYMBOLS),
                label = "=\\<",
                isSpecial = true,
                scheme = scheme,
                keyShape = preferences.keyShape,
                hasBorder = preferences.keyBorders,
                fontSize = preferences.keyFontSize,
                clickEffect = preferences.clickEffect,
                modifier = Modifier.weight(1.5f),
                onAction = onKeyAction
            )
            row3.forEach { k ->
                KeyboardKey(
                    action = KeyAction.Text(k),
                    label = k,
                    scheme = scheme,
                    keyShape = preferences.keyShape,
                    hasBorder = preferences.keyBorders,
                    fontSize = preferences.keyFontSize,
                    clickEffect = preferences.clickEffect,
                    modifier = Modifier.weight(1f),
                    onAction = onKeyAction
                )
            }
            KeyboardKey(
                action = KeyAction.Backspace,
                label = "⌫",
                isSpecial = true,
                scheme = scheme,
                keyShape = preferences.keyShape,
                hasBorder = preferences.keyBorders,
                fontSize = preferences.keyFontSize,
                clickEffect = preferences.clickEffect,
                quickDeleteEnabled = preferences.quickDeleteSwipeEnabled,
                modifier = Modifier.weight(1.5f),
                onAction = onKeyAction
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            KeyboardKey(
                action = KeyAction.SwitchPage(KeyboardPage.QWERTY),
                label = "ABC",
                isSpecial = true,
                scheme = scheme,
                keyShape = preferences.keyShape,
                hasBorder = preferences.keyBorders,
                fontSize = preferences.keyFontSize,
                clickEffect = preferences.clickEffect,
                modifier = Modifier.weight(1.4f),
                onAction = onKeyAction
            )
            KeyboardKey(action = KeyAction.Text(","), label = ",", scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(0.9f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.Text("፡"), label = "፡", isSpecial = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(0.9f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.Space, label = "Space", isSpacebar = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, spacebarSwipeEnabled = preferences.spacebarSwipeCursorEnabled, modifier = Modifier.weight(3.2f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.Text("."), label = ".", scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(0.9f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.Text("።"), label = "።", isSpecial = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(0.9f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.Enter, label = "↵", isSpecial = true, isActive = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1.4f), onAction = onKeyAction)
        }
    }
}

@Composable
private fun MoreSymbolsPage(
    preferences: KeyboardPreferencesData,
    scheme: KeyboardColorScheme,
    onKeyAction: (KeyAction) -> Unit
) {
    val row1 = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    val row2 = listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\")
    val row3 = listOf("%", "©", "®", "™", "[", "]", "<", ">")

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            row1.forEach { k -> KeyboardKey(action = KeyAction.Text(k), label = k, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1f), onAction = onKeyAction) }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            row2.forEach { k -> KeyboardKey(action = KeyAction.Text(k), label = k, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1f), onAction = onKeyAction) }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            KeyboardKey(action = KeyAction.SwitchPage(KeyboardPage.SYMBOLS), label = "?123", isSpecial = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1.5f), onAction = onKeyAction)
            row3.forEach { k -> KeyboardKey(action = KeyAction.Text(k), label = k, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1f), onAction = onKeyAction) }
            KeyboardKey(action = KeyAction.Backspace, label = "⌫", isSpecial = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, quickDeleteEnabled = preferences.quickDeleteSwipeEnabled, modifier = Modifier.weight(1.5f), onAction = onKeyAction)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            KeyboardKey(action = KeyAction.SwitchPage(KeyboardPage.QWERTY), label = "ABC", isSpecial = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1.5f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.Space, label = "Space", isSpacebar = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, spacebarSwipeEnabled = preferences.spacebarSwipeCursorEnabled, modifier = Modifier.weight(4.5f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.Enter, label = "↵", isSpecial = true, isActive = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1.5f), onAction = onKeyAction)
        }
    }
}

@Composable
private fun GeezPage(
    preferences: KeyboardPreferencesData,
    scheme: KeyboardColorScheme,
    onKeyAction: (KeyAction) -> Unit
) {
    val numbers1To10 = listOf("፩", "፪", "፫", "፬", "፭", "፮", "፯", "፰", "፱", "፲")
    val numbersTens = listOf("፳", "፴", "፵", "፶", "፷", "፸", "፹", "፺", "፻", "፼")
    val punctuation = listOf("፡", "።", "፣", "፤", "፥", "፦", "፧", "፠")

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            numbers1To10.forEach { k -> KeyboardKey(action = KeyAction.Text(k), label = k, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1f), onAction = onKeyAction) }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            numbersTens.forEach { k -> KeyboardKey(action = KeyAction.Text(k), label = k, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1f), onAction = onKeyAction) }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            punctuation.forEach { k -> KeyboardKey(action = KeyAction.Text(k), label = k, isSpecial = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1f), onAction = onKeyAction) }
            KeyboardKey(action = KeyAction.Backspace, label = "⌫", isSpecial = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, quickDeleteEnabled = preferences.quickDeleteSwipeEnabled, modifier = Modifier.weight(1.5f), onAction = onKeyAction)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            KeyboardKey(action = KeyAction.SwitchPage(KeyboardPage.QWERTY), label = "ABC", isSpecial = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1.5f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.SwitchPage(KeyboardPage.SYMBOLS), label = "123", isSpecial = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1.2f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.Space, label = "Space", isSpacebar = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, spacebarSwipeEnabled = preferences.spacebarSwipeCursorEnabled, modifier = Modifier.weight(4f), onAction = onKeyAction)
            KeyboardKey(action = KeyAction.Enter, label = "↵", isSpecial = true, isActive = true, scheme = scheme, keyShape = preferences.keyShape, hasBorder = preferences.keyBorders, fontSize = preferences.keyFontSize, modifier = Modifier.weight(1.5f), onAction = onKeyAction)
        }
    }
}
