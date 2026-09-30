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

val DEFAULT_QUICK_PHRASES = listOf("ሰላም", "ጤና ይስጥልኝ", "አመሰግናለሁ", "አዎ", "አይ", "እንዴት ነህ", "መልካም ቀን", "፡", "።", "፣")
val DEFAULT_ENGLISH_QUICK_PHRASES = listOf("Hello", "Thanks", "Yes", "No", "How are you", "Good day", "Please", "Okay")

@Composable
fun KeyboardCandidateBar(
    isComposing: Boolean,
    composingChar: String,
    rawBuffer: String,
    candidates: List<CandidateItem>,
    mode: TransliterationMode,
    scheme: KeyboardColorScheme,
    fontSize: CandidateFontSize = CandidateFontSize.NORMAL,
    onSelectCandidate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp),
        color = scheme.candidateBackground,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 3.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isComposing && candidates.isNotEmpty()) {
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

                // Family candidates
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
            } else {
                // Mode indicator chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (mode == TransliterationMode.AMHARIC) scheme.candidateBadgeBackground
                            else scheme.keySpecialBackground
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (mode == TransliterationMode.AMHARIC) "አማ (Phonetic)" else "EN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (mode == TransliterationMode.AMHARIC) scheme.candidateBadgeTextColor
                        else scheme.keyTextColor
                    )
                }

                val phrases = if (mode == TransliterationMode.AMHARIC) DEFAULT_QUICK_PHRASES else DEFAULT_ENGLISH_QUICK_PHRASES

                phrases.forEach { phrase ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(scheme.keyBackground)
                            .clickable { onSelectCandidate(phrase) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("quick_phrase_$phrase"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = phrase,
                            fontSize = fontSize.size,
                            fontWeight = FontWeight.Medium,
                            color = scheme.keyTextColor
                        )
                    }
                }
            }
        }
    }
}
