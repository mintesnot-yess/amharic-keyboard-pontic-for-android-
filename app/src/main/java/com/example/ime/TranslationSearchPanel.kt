package com.example.ime

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AmharicDictionary
import com.example.engine.DictEntry
import com.example.ui.theme.KeyboardColorScheme

@Composable
fun TranslationSearchPanel(
    initialIsSearch: Boolean = false,
    scheme: KeyboardColorScheme,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSearchMode by remember { mutableStateOf(initialIsSearch) }
    var queryText by remember { mutableStateOf("") }

    val searchResults = remember(queryText) {
        AmharicDictionary.search(queryText)
    }

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
            // Header with Mode Switcher & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Translate tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isSearchMode) scheme.accentColor else scheme.keySpecialBackground)
                            .clickable { isSearchMode = false }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GTranslate,
                                contentDescription = null,
                                tint = if (!isSearchMode) scheme.keyTextActiveColor else scheme.toolbarIconColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Translate",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isSearchMode) scheme.keyTextActiveColor else scheme.keyTextColor
                            )
                        }
                    }

                    // Web Search tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSearchMode) scheme.accentColor else scheme.keySpecialBackground)
                            .clickable { isSearchMode = true }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = if (isSearchMode) scheme.keyTextActiveColor else scheme.toolbarIconColor,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Search",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSearchMode) scheme.keyTextActiveColor else scheme.keyTextColor
                            )
                        }
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

            Spacer(modifier = Modifier.height(6.dp))

            // Query Search Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    placeholder = {
                        Text(
                            text = if (isSearchMode) "Type search query..." else "Type English or Amharic (e.g. selam, water)...",
                            fontSize = 12.sp
                        )
                    },
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

                // Launch external web search or Google Translate
                Button(
                    onClick = {
                        val q = queryText.trim()
                        if (q.isNotEmpty()) {
                            val url = if (isSearchMode) {
                                "https://www.google.com/search?q=${Uri.encode(q)}"
                            } else {
                                "https://translate.google.com/?sl=auto&tl=am&text=${Uri.encode(q)}&op=translate"
                            }
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = scheme.keySpecialBackground),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Open Web",
                        tint = scheme.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dictionary Results List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(searchResults) { entry ->
                    DictionaryItemRow(
                        entry = entry,
                        scheme = scheme,
                        onInsertAmharic = { onInsertText(entry.amharic) },
                        onInsertEnglish = { onInsertText(entry.english.split("/")[0].trim()) }
                    )
                }

                if (searchResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No instant match found. Tap the browser button to search online!",
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
private fun DictionaryItemRow(
    entry: DictEntry,
    scheme: KeyboardColorScheme,
    onInsertAmharic: () -> Unit,
    onInsertEnglish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(scheme.keyBackground)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entry.amharic,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = scheme.keyTextColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "(${entry.phonetic})",
                    fontSize = 11.sp,
                    color = scheme.subLabelColor
                )
            }
            Text(
                text = entry.english,
                fontSize = 12.sp,
                color = scheme.keyTextSpecialColor
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(scheme.accentColor)
                    .clickable { onInsertAmharic() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "አማ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = scheme.keyTextActiveColor
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(scheme.keySpecialBackground)
                    .clickable { onInsertEnglish() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = scheme.keyTextColor
                )
            }
        }
    }
}
