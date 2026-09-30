package com.example.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KeyboardColorScheme

@Composable
fun ClipboardPanel(
    recentClips: List<String>,
    pinnedClips: Set<String>,
    scheme: KeyboardColorScheme,
    onPasteClip: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    onAddCustomClip: (String) -> Unit,
    onClearRecent: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newClipText by remember { mutableStateOf("") }

    val quickSnippets = listOf(
        "ሰላም ጤና ይስጥልኝ!",
        "አመሰግናለሁ!",
        "እንደምን አለህ?",
        "መልካም ቀን!",
        "እግዚአብሔር ይመስገን",
        "እሺ",
        "ይቅርታ",
        "ደህና ሁን"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp),
        color = scheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = scheme.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Smart Clipboard Manager",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = scheme.keyTextColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showAddDialog = !showAddDialog },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add snippet",
                            tint = scheme.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (recentClips.isNotEmpty()) {
                        IconButton(
                            onClick = onClearRecent,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear recent",
                                tint = scheme.toolbarIconColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = scheme.keyTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quick add snippet field
            if (showAddDialog) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newClipText,
                        onValueChange = { newClipText = it },
                        placeholder = { Text("Enter phrase to pin...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = scheme.keyTextColor,
                            unfocusedTextColor = scheme.keyTextColor,
                            focusedBorderColor = scheme.accentColor,
                            unfocusedBorderColor = scheme.toolbarIconColor
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            if (newClipText.isNotBlank()) {
                                onAddCustomClip(newClipText.trim())
                                newClipText = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = scheme.accentColor),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Save", fontSize = 12.sp, color = scheme.keyTextActiveColor)
                    }
                }
            }

            // Quick Expression Chips Row
            Text(
                text = "Quick Expressions (አጫጭር ቃላት):",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = scheme.subLabelColor,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            ) {
                items(quickSnippets) { snippet ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(scheme.keySpecialBackground)
                            .clickable { onPasteClip(snippet) }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = snippet,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = scheme.keyTextColor
                        )
                    }
                }
            }

            // Pinned & Recent Items list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Pinned Items
                if (pinnedClips.isNotEmpty()) {
                    item {
                        Text(
                            text = "Pinned Snippets (የተሰኩ):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = scheme.accentColor
                        )
                    }
                    items(pinnedClips.toList()) { clip ->
                        ClipItemRow(
                            text = clip,
                            isPinned = true,
                            scheme = scheme,
                            onPaste = { onPasteClip(clip) },
                            onTogglePin = { onTogglePin(clip) }
                        )
                    }
                }

                // Recent Items
                if (recentClips.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recently Copied:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = scheme.toolbarIconColor,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    items(recentClips) { clip ->
                        ClipItemRow(
                            text = clip,
                            isPinned = pinnedClips.contains(clip),
                            scheme = scheme,
                            onPaste = { onPasteClip(clip) },
                            onTogglePin = { onTogglePin(clip) }
                        )
                    }
                }

                if (recentClips.isEmpty() && pinnedClips.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items in clipboard history yet. Copy any text or use quick expressions!",
                                fontSize = 12.sp,
                                color = scheme.toolbarIconColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClipItemRow(
    text: String,
    isPinned: Boolean,
    scheme: KeyboardColorScheme,
    onPaste: () -> Unit,
    onTogglePin: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(scheme.keyBackground)
            .clickable { onPaste() }
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .testTag("clip_item_${text.take(10)}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = scheme.keyTextColor,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onTogglePin,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PushPin,
                contentDescription = if (isPinned) "Unpin" else "Pin",
                tint = if (isPinned) scheme.accentColor else scheme.toolbarIconColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
