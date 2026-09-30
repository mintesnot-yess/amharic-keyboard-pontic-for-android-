package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.KeyAction
import com.example.ime.KeyboardKey
import com.example.preferences.CandidateFontSize
import com.example.preferences.ClickEffectStyle
import com.example.preferences.KeyFontSize
import com.example.preferences.KeyShapeStyle
import com.example.preferences.KeyboardPreferencesData
import com.example.preferences.ThemePreset
import com.example.preferences.VibrationIntensity
import com.example.ui.theme.KeyboardThemes

@Composable
fun CustomizerScreen(
    preferences: KeyboardPreferencesData,
    onPreferencesChange: (KeyboardPreferencesData) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentScheme = KeyboardThemes.getScheme(preferences.themePreset)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Interactive Key Preview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preview_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = currentScheme.background),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Key Preview",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = currentScheme.accentColor
                        )
                        Text(
                            text = preferences.themePreset.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = currentScheme.subLabelColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mini row of interactive keys
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            Triple("s", "ስ", "#"),
                            Triple("e", "እ", "3"),
                            Triple("l", "ል", "="),
                            Triple("a", "አ", "@"),
                            Triple("m", "ም", ":")
                        ).forEach { (char, amharic, flick) ->
                            KeyboardKey(
                                action = KeyAction.Text(char),
                                label = char,
                                subLabel = amharic,
                                flickSymbol = flick,
                                scheme = currentScheme,
                                keyShape = preferences.keyShape,
                                hasBorder = preferences.keyBorders,
                                fontSize = preferences.keyFontSize,
                                clickEffect = preferences.clickEffect,
                                flickEnabled = preferences.flickGesturesEnabled,
                                modifier = Modifier.weight(1f),
                                onAction = {}
                            )
                        }

                        // Backspace preview
                        KeyboardKey(
                            action = KeyAction.Backspace,
                            label = "⌫",
                            isSpecial = true,
                            scheme = currentScheme,
                            keyShape = preferences.keyShape,
                            hasBorder = preferences.keyBorders,
                            fontSize = preferences.keyFontSize,
                            clickEffect = preferences.clickEffect,
                            quickDeleteEnabled = preferences.quickDeleteSwipeEnabled,
                            modifier = Modifier.weight(1.3f),
                            onAction = {}
                        )
                    }
                }
            }
        }

        // Section 1: Themes & Color Palette
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Keyboard Theme & Aesthetics",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Theme selector cards
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemePreset.entries.forEach { preset ->
                            val isSelected = preferences.themePreset == preset
                            val scheme = KeyboardThemes.getScheme(preset)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable {
                                        onPreferencesChange(preferences.copy(themePreset = preset))
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("theme_preset_${preset.name.lowercase()}"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Theme color palette preview circles
                                    Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(scheme.background)
                                                .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(scheme.keyBackground)
                                                .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(scheme.accentColor)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = preset.displayName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = preset.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Font Sizes
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Font Size Customization",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Key Label Font Size:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KeyFontSize.entries.forEach { sizeOption ->
                            FilterChip(
                                selected = preferences.keyFontSize == sizeOption,
                                onClick = { onPreferencesChange(preferences.copy(keyFontSize = sizeOption)) },
                                label = { Text(sizeOption.displayName) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Candidate Bar Font Size:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CandidateFontSize.entries.forEach { candOption ->
                            FilterChip(
                                selected = preferences.candidateFontSize == candOption,
                                onClick = { onPreferencesChange(preferences.copy(candidateFontSize = candOption)) },
                                label = { Text(candOption.displayName) }
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Key Shapes & Visual Styling
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Key Shapes & Press Effects",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Key Corner Shape:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KeyShapeStyle.entries.forEach { shapeOption ->
                            FilterChip(
                                selected = preferences.keyShape == shapeOption,
                                onClick = { onPreferencesChange(preferences.copy(keyShape = shapeOption)) },
                                label = { Text(shapeOption.displayName) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Key Borders Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Key Borders", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Show high-contrast key outline borders", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = preferences.keyBorders,
                            onCheckedChange = { onPreferencesChange(preferences.copy(keyBorders = it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Key Press Click Effect:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClickEffectStyle.entries.forEach { effect ->
                            FilterChip(
                                selected = preferences.clickEffect == effect,
                                onClick = { onPreferencesChange(preferences.copy(clickEffect = effect)) },
                                label = { Text(effect.displayName) }
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Sound & Haptics (Click Sound removed by default)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (preferences.soundOnClick) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Audio & Haptic Feedback",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sound on click (Default disabled as requested: "remove the click sound")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Key Click Sound", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Disabled by default for silent typing", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = preferences.soundOnClick,
                            onCheckedChange = { onPreferencesChange(preferences.copy(soundOnClick = it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Vibration Intensity:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VibrationIntensity.entries.forEach { intensity ->
                            FilterChip(
                                selected = preferences.vibrationIntensity == intensity,
                                onClick = { onPreferencesChange(preferences.copy(vibrationIntensity = intensity)) },
                                label = { Text(intensity.displayName) }
                            )
                        }
                    }
                }
            }
        }

        // Section 5: Gestures & Smart Typing
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Gesture,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gestures & Advanced Controls",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Flick gestures
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Flick Gestures for Symbols", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Swipe up or hold keys to type numbers & symbols", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = preferences.flickGesturesEnabled,
                            onCheckedChange = { onPreferencesChange(preferences.copy(flickGesturesEnabled = it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick delete swipe
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Quick Delete Swipe", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Swipe left from backspace key to quickly delete words", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = preferences.quickDeleteSwipeEnabled,
                            onCheckedChange = { onPreferencesChange(preferences.copy(quickDeleteSwipeEnabled = it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Spacebar swipe to move cursor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Spacebar Cursor Navigation", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Slide finger across spacebar to move cursor left or right", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = preferences.spacebarSwipeCursorEnabled,
                            onCheckedChange = { onPreferencesChange(preferences.copy(spacebarSwipeCursorEnabled = it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dedicated Number Row for Addresses & Dates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Top Numeric Row (Addresses & Dates)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Dedicated top row (1-0) with fast access to date separators (/, -, .) and address signs (#, @) for rapid typing", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = preferences.showNumberRow,
                            onCheckedChange = { onPreferencesChange(preferences.copy(showNumberRow = it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ge'ez word divider (፡)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ge'ez Word Divider (፡) for Space", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Output traditional '፡' instead of standard space", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = preferences.useAmharicWordDivider,
                            onCheckedChange = { onPreferencesChange(preferences.copy(useAmharicWordDivider = it)) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
