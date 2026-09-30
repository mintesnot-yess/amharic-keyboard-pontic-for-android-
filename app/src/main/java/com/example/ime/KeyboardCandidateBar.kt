package com.example.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.engine.CandidateItem
import com.example.preferences.CandidateFontSize
import com.example.ui.theme.KeyboardColorScheme

@Composable
fun KeyboardCandidateBar(
    isComposing: Boolean,
    composingChar: String,
    rawBuffer: String,
    candidates: List<CandidateItem>,
    englishSuggestions: List<String> = emptyList(),
    mode: TransliterationMode,
    scheme: KeyboardColorScheme,
    fontSize: CandidateFontSize = CandidateFontSize.NORMAL,
    onSelectCandidate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // In Amharic mode with no active composition, or English mode with no suggestions, hide bar height to save vertical screen real estate
    val hasContent = (mode == TransliterationMode.AMHARIC && isComposing && candidates.isNotEmpty()) ||
            (mode == TransliterationMode.ENGLISH && englishSuggestions.isNotEmpty())

    if (!hasContent) {
        // As requested: "on top no need in amharic suggestion words in"
        // Return without rendering canned phrase clutter
        return
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        color = scheme.candidateBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (mode == TransliterationMode.AMHARIC && isComposing && candidates.isNotEmpty()) {
                // Active composing indicator badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(scheme.candidateBadgeBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[$rawBuffer] $composingChar",
                        fontSize = fontSize.size,
                        fontWeight = FontWeight.Bold,
                        color = scheme.candidateBadgeTextColor
                    )
                }

                // Family candidates (Ge'ez syllable orders)
                candidates.forEach { candidate ->
                    val isSelected = candidate.geezChar == composingChar
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) scheme.keyActiveBackground
                                else scheme.keyBackground
                            )
                            .clickable { onSelectCandidate(candidate.geezChar) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .testTag("candidate_${candidate.geezChar}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = candidate.geezChar,
                                fontSize = fontSize.size,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) scheme.keyTextActiveColor else scheme.keyTextColor
                            )
                            Text(
                                text = candidate.keystroke,
                                fontSize = 10.sp,
                                color = if (isSelected) scheme.keyTextActiveColor.copy(alpha = 0.8f) else scheme.subLabelColor
                            )
                        }
                    }
                }
            } else if (mode == TransliterationMode.ENGLISH && englishSuggestions.isNotEmpty()) {
                // English auto-complete / predictive suggestions
                englishSuggestions.forEach { word ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(scheme.keyBackground)
                            .clickable { onSelectCandidate(word) }
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                            .testTag("english_suggestion_$word"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = word,
                            fontSize = fontSize.size,
                            fontWeight = FontWeight.SemiBold,
                            color = scheme.keyTextColor
                        )
                    }
                }
            }
        }
    }
}
