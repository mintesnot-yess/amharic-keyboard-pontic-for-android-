package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AmharicTransliterationEngine
import com.example.engine.BackspaceStep
import com.example.ime.KeyAction
import com.example.ime.KeyboardLayout
import com.example.ime.KeyboardOverlay
import com.example.ime.KeyboardPage
import com.example.ime.ShiftState
import com.example.ime.TransliterationMode
import com.example.preferences.KeyboardPreferences
import com.example.preferences.KeyboardPreferencesData
import com.example.ui.AboutScreen
import com.example.ui.CustomizerScreen
import com.example.ui.PhoneticReferenceScreen
import com.example.ui.theme.KeyboardThemes
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialTab = intent?.getIntExtra("OPEN_TAB", 0) ?: 0
        setContent {
            MyApplicationTheme {
                AmharicKeyboardMainApp(initialTab = initialTab)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmharicKeyboardMainApp(initialTab: Int = 0) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(initialTab) } // 0: Sandbox & Setup, 1: Customizer, 2: Guide, 3: About

    // Central Preferences
    var preferences by remember { mutableStateOf(KeyboardPreferences.load(context)) }

    // Transliteration Engine instance for in-app sandbox
    val sandboxEngine = remember { AmharicTransliterationEngine() }

    // Sandbox state
    var textOutput by remember { mutableStateOf("") }
    var composingChar by remember { mutableStateOf("") }
    var rawBuffer by remember { mutableStateOf("") }
    var isComposing by remember { mutableStateOf(false) }
    var candidates by remember { mutableStateOf(emptyList<com.example.engine.CandidateItem>()) }

    // Virtual in-app keyboard settings
    var showEmbeddedKeyboard by remember { mutableStateOf(true) }
    var sandboxPage by remember { mutableStateOf(KeyboardPage.QWERTY) }
    var sandboxShift by remember { mutableStateOf(ShiftState.OFF) }
    var sandboxMode by remember { mutableStateOf(TransliterationMode.AMHARIC) }
    var sandboxOverlay by remember { mutableStateOf(KeyboardOverlay.NONE) }
    var recentClips by remember { mutableStateOf(listOf("ሰላም ለሁላችሁ!", "እንኳን ደህና መጣችሁ!", "አመሰግናለሁ")) }

    // System IME status
    var isEnabledInSystem by remember { mutableStateOf(false) }
    var isSelectedInSystem by remember { mutableStateOf(false) }

    fun refreshImeStatus() {
        isEnabledInSystem = isImeEnabled(context)
        isSelectedInSystem = isImeSelected(context)
    }

    LaunchedEffect(Unit) {
        refreshImeStatus()
    }

    fun onUpdatePreferences(newPrefs: KeyboardPreferencesData) {
        preferences = newPrefs
        KeyboardPreferences.save(context, newPrefs)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "አ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Phonetic QWERTY to Ge'ez",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelectedInSystem) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSelectedInSystem) Icons.Default.CheckCircle else Icons.Default.Keyboard,
                                contentDescription = null,
                                tint = if (isSelectedInSystem) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSelectedInSystem) "Active" else "Switch IME",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelectedInSystem) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        refreshImeStatus()
                    },
                    icon = { Icon(Icons.Default.Keyboard, contentDescription = "Keyboard") },
                    label = { Text("Keyboard") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Palette, contentDescription = "Customizer") },
                    label = { Text("Themes") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Guide") },
                    label = { Text("Guide") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    label = { Text("About") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> {
                    KeyboardSandboxScreen(
                        isEnabledInSystem = isEnabledInSystem,
                        isSelectedInSystem = isSelectedInSystem,
                        onEnableClicked = {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            context.startActivity(intent)
                        },
                        onSelectClicked = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.showInputMethodPicker()
                        },
                        textOutput = textOutput,
                        onTextOutputChange = { textOutput = it },
                        composingChar = composingChar,
                        rawBuffer = rawBuffer,
                        isComposing = isComposing,
                        candidates = candidates,
                        showEmbeddedKeyboard = showEmbeddedKeyboard,
                        onToggleEmbeddedKeyboard = { showEmbeddedKeyboard = !showEmbeddedKeyboard },
                        keyboardPage = sandboxPage,
                        shiftState = sandboxShift,
                        mode = sandboxMode,
                        activeOverlay = sandboxOverlay,
                        recentClips = recentClips,
                        preferences = preferences,
                        onKeyAction = { action ->
                            when (action) {
                                is KeyAction.Text -> {
                                    if (sandboxMode == TransliterationMode.AMHARIC && action.char.length == 1 && action.char[0].isLetter()) {
                                        val step = sandboxEngine.onCharacterInput(action.char[0])
                                        if (step.committedText.isNotEmpty()) {
                                            textOutput += step.committedText
                                        }
                                        if (step.composingText.isNotEmpty()) {
                                            isComposing = true
                                            composingChar = step.composingText
                                            rawBuffer = step.rawBuffer
                                            candidates = step.candidates
                                        } else {
                                            isComposing = false
                                            composingChar = ""
                                            rawBuffer = ""
                                            candidates = emptyList()
                                        }
                                    } else {
                                        val pending = sandboxEngine.commitAndReset()
                                        if (pending.isNotEmpty()) textOutput += pending
                                        isComposing = false
                                        composingChar = ""
                                        rawBuffer = ""
                                        candidates = emptyList()
                                        textOutput += action.char
                                    }
                                    if (sandboxShift == ShiftState.ONCE) sandboxShift = ShiftState.OFF
                                }
                                is KeyAction.Backspace -> {
                                    if (sandboxMode == TransliterationMode.AMHARIC && isComposing) {
                                        when (val result = sandboxEngine.onBackspace()) {
                                            is BackspaceStep.UpdatedComposing -> {
                                                isComposing = true
                                                composingChar = result.composingText
                                                rawBuffer = result.rawBuffer
                                                candidates = result.candidates
                                            }
                                            is BackspaceStep.ClearedComposing -> {
                                                isComposing = false
                                                composingChar = ""
                                                rawBuffer = ""
                                                candidates = emptyList()
                                            }
                                            is BackspaceStep.PassToEditor -> {
                                                if (textOutput.isNotEmpty()) {
                                                    textOutput = textOutput.dropLast(1)
                                                }
                                            }
                                        }
                                    } else {
                                        if (textOutput.isNotEmpty()) {
                                            textOutput = textOutput.dropLast(1)
                                        }
                                    }
                                }
                                is KeyAction.DeleteWords -> {
                                    val pending = sandboxEngine.commitAndReset()
                                    if (pending.isNotEmpty()) textOutput += pending
                                    isComposing = false
                                    composingChar = ""
                                    rawBuffer = ""
                                    candidates = emptyList()

                                    val words = textOutput.trimEnd().split(Regex("\\s+"))
                                    val count = action.count.coerceAtMost(words.size)
                                    val toKeep = words.dropLast(count)
                                    textOutput = if (toKeep.isEmpty()) "" else toKeep.joinToString(" ") + " "
                                }
                                is KeyAction.Space -> {
                                    val pending = sandboxEngine.commitAndReset()
                                    if (pending.isNotEmpty()) textOutput += pending
                                    isComposing = false
                                    composingChar = ""
                                    rawBuffer = ""
                                    candidates = emptyList()
                                    textOutput += if (preferences.useAmharicWordDivider && sandboxMode == TransliterationMode.AMHARIC) "፡" else " "
                                }
                                is KeyAction.Enter -> {
                                    val pending = sandboxEngine.commitAndReset()
                                    if (pending.isNotEmpty()) textOutput += pending
                                    isComposing = false
                                    composingChar = ""
                                    rawBuffer = ""
                                    candidates = emptyList()
                                    textOutput += "\n"
                                }
                                is KeyAction.Shift -> {
                                    sandboxShift = sandboxShift.next()
                                }
                                is KeyAction.SwitchPage -> {
                                    sandboxPage = action.target
                                    sandboxOverlay = KeyboardOverlay.NONE
                                }
                                is KeyAction.ToggleMode -> {
                                    val pending = sandboxEngine.commitAndReset()
                                    if (pending.isNotEmpty()) textOutput += pending
                                    isComposing = false
                                    composingChar = ""
                                    rawBuffer = ""
                                    candidates = emptyList()
                                    sandboxMode = if (sandboxMode == TransliterationMode.AMHARIC) TransliterationMode.ENGLISH else TransliterationMode.AMHARIC
                                }
                                is KeyAction.SwitchIme -> {
                                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                    imm?.showInputMethodPicker()
                                }
                                is KeyAction.HideKeyboard -> {
                                    showEmbeddedKeyboard = false
                                }
                                is KeyAction.MoveCursor -> {
                                    // Sandbox cursor indicator
                                }
                                is KeyAction.OpenClipboard -> {
                                    sandboxOverlay = KeyboardOverlay.CLIPBOARD
                                }
                                is KeyAction.OpenTranslation -> {
                                    sandboxOverlay = KeyboardOverlay.TRANSLATION
                                }
                                is KeyAction.OpenSearch -> {
                                    sandboxOverlay = KeyboardOverlay.SEARCH
                                }
                                is KeyAction.CloseOverlay -> {
                                    sandboxOverlay = KeyboardOverlay.NONE
                                }
                                is KeyAction.PasteText -> {
                                    val pending = sandboxEngine.commitAndReset()
                                    if (pending.isNotEmpty()) textOutput += pending
                                    isComposing = false
                                    composingChar = ""
                                    rawBuffer = ""
                                    candidates = emptyList()
                                    textOutput += action.text
                                    sandboxOverlay = KeyboardOverlay.NONE
                                }
                                is KeyAction.OpenSettings -> {
                                    selectedTab = 1
                                }
                                is KeyAction.ToggleNumberRow -> {
                                    onUpdatePreferences(preferences.copy(showNumberRow = !preferences.showNumberRow))
                                }
                            }
                        },
                        onSelectCandidate = { candidate ->
                            textOutput += candidate
                            sandboxEngine.reset()
                            isComposing = false
                            composingChar = ""
                            rawBuffer = ""
                            candidates = emptyList()
                        },
                        onClearText = {
                            textOutput = ""
                            sandboxEngine.reset()
                            isComposing = false
                            composingChar = ""
                            rawBuffer = ""
                            candidates = emptyList()
                        },
                        onTogglePinClip = { clip ->
                            val updated = if (preferences.pinnedClips.contains(clip)) {
                                preferences.pinnedClips - clip
                            } else {
                                preferences.pinnedClips + clip
                            }
                            onUpdatePreferences(preferences.copy(pinnedClips = updated))
                        },
                        onAddCustomClip = { clip ->
                            val updated = preferences.pinnedClips + clip
                            onUpdatePreferences(preferences.copy(pinnedClips = updated))
                        },
                        onClearRecentClips = {
                            recentClips = emptyList()
                        }
                    )
                }
                1 -> {
                    CustomizerScreen(
                        preferences = preferences,
                        onPreferencesChange = { onUpdatePreferences(it) }
                    )
                }
                2 -> {
                    PhoneticReferenceScreen(
                        onExampleClicked = { exampleKey ->
                            selectedTab = 0
                            val transliterated = sandboxEngine.transliterateString(exampleKey)
                            textOutput = if (textOutput.isEmpty()) transliterated else "$textOutput $transliterated"
                        }
                    )
                }
                3 -> {
                    AboutScreen()
                }
            }
        }
    }
}

@Composable
fun KeyboardSandboxScreen(
    isEnabledInSystem: Boolean,
    isSelectedInSystem: Boolean,
    onEnableClicked: () -> Unit,
    onSelectClicked: () -> Unit,
    textOutput: String,
    onTextOutputChange: (String) -> Unit,
    composingChar: String,
    rawBuffer: String,
    isComposing: Boolean,
    candidates: List<com.example.engine.CandidateItem>,
    showEmbeddedKeyboard: Boolean,
    onToggleEmbeddedKeyboard: () -> Unit,
    keyboardPage: KeyboardPage,
    shiftState: ShiftState,
    mode: TransliterationMode,
    activeOverlay: KeyboardOverlay,
    recentClips: List<String>,
    preferences: KeyboardPreferencesData,
    onKeyAction: (KeyAction) -> Unit,
    onSelectCandidate: (String) -> Unit,
    onClearText: () -> Unit,
    onTogglePinClip: (String) -> Unit,
    onAddCustomClip: (String) -> Unit,
    onClearRecentClips: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scheme = KeyboardThemes.getScheme(preferences.themePreset)

    Column(modifier = Modifier.fillMaxSize()) {
        // Upper test and activation content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Activation Banner if not yet enabled or active
            if (!isEnabledInSystem || !isSelectedInSystem) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!isEnabledInSystem) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (!isEnabledInSystem) Icons.Default.Warning else Icons.Default.Keyboard,
                                contentDescription = null,
                                tint = if (!isEnabledInSystem) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (!isEnabledInSystem) "Keyboard is not enabled yet" else "Not currently active keyboard",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (!isEnabledInSystem)
                                "Enable Amharic Keyboard in Android System Settings to type across WhatsApp, Telegram, Google, etc."
                            else
                                "Switch your current input method to Amharic Keyboard to use it in other apps.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!isEnabledInSystem) {
                                Button(
                                    onClick = onEnableClicked,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("1. Enable in Settings")
                                }
                            }
                            Button(
                                onClick = onSelectClicked,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isEnabledInSystem) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("2. Switch Keyboard")
                            }
                        }
                    }
                }
            }

            // Real-Time Transliteration Live Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("output_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interactive Typing Sandbox",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (textOutput.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        val clip = ClipData.newPlainText("Amharic Text", textOutput)
                                        clipboard?.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy text",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = onClearText,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear text",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Text Output Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp)
                    ) {
                        if (textOutput.isEmpty() && !isComposing) {
                            Text(
                                text = "Type below on the keyboard (e.g. selam, amesegenalehu, bet)...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline)
                            )
                        } else {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = textOutput,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                                if (isComposing) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = composingChar,
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Mode: ${if (mode == TransliterationMode.AMHARIC) "አማርኛ" else "English"}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                            if (isComposing) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Composing: \"$composingChar\"",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }

                        // Toggle keyboard visibility
                        Text(
                            text = if (showEmbeddedKeyboard) "Hide Keyboard" else "Show Keyboard",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .clickable { onToggleEmbeddedKeyboard() }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Real-time System Input Field (To test with system IME)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "System Input Field (Tap to pop up OS Keyboard)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    var systemInputText by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = systemInputText,
                        onValueChange = { systemInputText = it },
                        placeholder = { Text("Tap here to test system keyboard popup...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("system_test_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        // Embedded Interactive Jetpack Compose Keyboard
        AnimatedVisibility(visible = showEmbeddedKeyboard) {
            KeyboardLayout(
                page = keyboardPage,
                shiftState = shiftState,
                mode = mode,
                activeOverlay = activeOverlay,
                isComposing = isComposing,
                composingChar = composingChar,
                rawBuffer = rawBuffer,
                candidates = candidates,
                recentClips = recentClips,
                pinnedClips = preferences.pinnedClips,
                preferences = preferences,
                scheme = scheme,
                onKeyAction = onKeyAction,
                onSelectCandidate = onSelectCandidate,
                onTogglePinClip = onTogglePinClip,
                onAddCustomClip = onAddCustomClip,
                onClearRecentClips = onClearRecentClips
            )
        }
    }
}

private fun isImeEnabled(context: Context): Boolean {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return false
    val enabledList = imm.enabledInputMethodList
    return enabledList.any { it.packageName == context.packageName }
}

private fun isImeSelected(context: Context): Boolean {
    val currentIme = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.DEFAULT_INPUT_METHOD
    ) ?: return false
    return currentIme.contains(context.packageName)
}
