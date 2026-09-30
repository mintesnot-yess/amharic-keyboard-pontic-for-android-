package com.example.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KeyboardColorScheme

@Composable
fun KeyboardToolbar(
    mode: TransliterationMode,
    activeOverlay: KeyboardOverlay,
    isNumberRowActive: Boolean = false,
    scheme: KeyboardColorScheme,
    onAction: (KeyAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp),
        color = scheme.toolbarBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Language badge toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (mode == TransliterationMode.AMHARIC) scheme.accentColor else scheme.keySpecialBackground)
                    .clickable { onAction(KeyAction.ToggleMode) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("toolbar_mode_toggle"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (mode == TransliterationMode.AMHARIC) "አማ (Phonetic)" else "ENGLISH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (mode == TransliterationMode.AMHARIC) scheme.keyTextActiveColor else scheme.keyTextColor
                )
            }

            // Right icons: Clipboard, Translate, Search, Ge'ez, Close/Settings
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clipboard Button
                ToolbarIconButton(
                    icon = Icons.Default.Assignment,
                    contentDescription = "Clipboard Manager",
                    isActive = activeOverlay == KeyboardOverlay.CLIPBOARD,
                    scheme = scheme,
                    onClick = {
                        if (activeOverlay == KeyboardOverlay.CLIPBOARD) {
                            onAction(KeyAction.CloseOverlay)
                        } else {
                            onAction(KeyAction.OpenClipboard)
                        }
                    }
                )

                // Translate Button
                ToolbarIconButton(
                    icon = Icons.Default.GTranslate,
                    contentDescription = "Translate",
                    isActive = activeOverlay == KeyboardOverlay.TRANSLATION,
                    scheme = scheme,
                    onClick = {
                        if (activeOverlay == KeyboardOverlay.TRANSLATION) {
                            onAction(KeyAction.CloseOverlay)
                        } else {
                            onAction(KeyAction.OpenTranslation)
                        }
                    }
                )

                // Search Button
                ToolbarIconButton(
                    icon = Icons.Default.Search,
                    contentDescription = "Search",
                    isActive = activeOverlay == KeyboardOverlay.SEARCH,
                    scheme = scheme,
                    onClick = {
                        if (activeOverlay == KeyboardOverlay.SEARCH) {
                            onAction(KeyAction.CloseOverlay)
                        } else {
                            onAction(KeyAction.OpenSearch)
                        }
                    }
                )

                // Quick Number Row Toggle (123) for rapid address and date entry
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isNumberRowActive) scheme.accentColor else scheme.keySpecialBackground)
                        .clickable { onAction(KeyAction.ToggleNumberRow) }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("toolbar_number_row_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "123",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNumberRowActive) scheme.keyTextActiveColor else scheme.toolbarIconColor
                    )
                }

                // Ge'ez Numbers shortcut
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(scheme.keySpecialBackground)
                        .clickable { onAction(KeyAction.SwitchPage(KeyboardPage.GEEZ_PUNCTUATION)) }
                        .padding(horizontal = 7.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "፩፪፫",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = scheme.toolbarIconColor
                    )
                }

                // If overlay is open, show Close button; otherwise Settings button
                if (activeOverlay != KeyboardOverlay.NONE) {
                    ToolbarIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close Overlay",
                        isActive = false,
                        scheme = scheme,
                        onClick = { onAction(KeyAction.CloseOverlay) }
                    )
                } else {
                    ToolbarIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings",
                        isActive = false,
                        scheme = scheme,
                        onClick = { onAction(KeyAction.OpenSettings) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolbarIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isActive: Boolean,
    scheme: KeyboardColorScheme,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) scheme.accentColor else scheme.keySpecialBackground)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) scheme.keyTextActiveColor else scheme.toolbarIconColor,
            modifier = Modifier.size(17.dp)
        )
    }
}
