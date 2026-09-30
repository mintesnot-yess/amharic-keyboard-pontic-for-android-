package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.preferences.ThemePreset

data class KeyboardColorScheme(
    val background: Color,
    val surfaceContainer: Color,
    val keyBackground: Color,
    val keyPressedBackground: Color,
    val keySpecialBackground: Color,
    val keyActiveBackground: Color,
    val keyBorderColor: Color?,
    val keyTextColor: Color,
    val keyTextSpecialColor: Color,
    val keyTextActiveColor: Color,
    val subLabelColor: Color,
    val candidateBackground: Color,
    val candidateBadgeBackground: Color,
    val candidateBadgeTextColor: Color,
    val accentColor: Color,
    val toolbarBackground: Color,
    val toolbarIconColor: Color
)

object KeyboardThemes {
    val AbyssinianHeritage = KeyboardColorScheme(
        background = Color(0xFF101B14),
        surfaceContainer = Color(0xFF16251C),
        keyBackground = Color(0xFF203529),
        keyPressedBackground = Color(0xFF2C4A39),
        keySpecialBackground = Color(0xFF1B2C22),
        keyActiveBackground = Color(0xFFD62828), // Warm Crimson active
        keyBorderColor = Color(0xFF2E4D3B),
        keyTextColor = Color(0xFFF1F8F3),
        keyTextSpecialColor = Color(0xFFFFC72C), // Gold
        keyTextActiveColor = Color(0xFFFFFFFF),
        subLabelColor = Color(0xFFFFD166),      // Soft gold
        candidateBackground = Color(0xFF1A2D22),
        candidateBadgeBackground = Color(0xFF007A33), // Emerald
        candidateBadgeTextColor = Color(0xFFFFFFFF),
        accentColor = Color(0xFFFFC72C),
        toolbarBackground = Color(0xFF132219),
        toolbarIconColor = Color(0xFFFFD166)
    )

    val OledBlack = KeyboardColorScheme(
        background = Color(0xFF000000),
        surfaceContainer = Color(0xFF0A0A0A),
        keyBackground = Color(0xFF171717),
        keyPressedBackground = Color(0xFF2A2A2A),
        keySpecialBackground = Color(0xFF222222),
        keyActiveBackground = Color(0xFF00E5FF),
        keyBorderColor = Color(0xFF262626),
        keyTextColor = Color(0xFFFFFFFF),
        keyTextSpecialColor = Color(0xFFE0E0E0),
        keyTextActiveColor = Color(0xFF000000),
        subLabelColor = Color(0xFF00E5FF),
        candidateBackground = Color(0xFF121212),
        candidateBadgeBackground = Color(0xFF00E5FF),
        candidateBadgeTextColor = Color(0xFF000000),
        accentColor = Color(0xFF00E5FF),
        toolbarBackground = Color(0xFF0D0D0D),
        toolbarIconColor = Color(0xFF00E5FF)
    )

    val MidnightBlue = KeyboardColorScheme(
        background = Color(0xFF0A1128),
        surfaceContainer = Color(0xFF111D40),
        keyBackground = Color(0xFF1C2B54),
        keyPressedBackground = Color(0xFF283D75),
        keySpecialBackground = Color(0xFF162347),
        keyActiveBackground = Color(0xFF48CAE4),
        keyBorderColor = Color(0xFF25396E),
        keyTextColor = Color(0xFFF0F8FF),
        keyTextSpecialColor = Color(0xFF90E0EF),
        keyTextActiveColor = Color(0xFF0A1128),
        subLabelColor = Color(0xFF48CAE4),
        candidateBackground = Color(0xFF142145),
        candidateBadgeBackground = Color(0xFF0096C7),
        candidateBadgeTextColor = Color(0xFFFFFFFF),
        accentColor = Color(0xFF48CAE4),
        toolbarBackground = Color(0xFF0E1736),
        toolbarIconColor = Color(0xFF90E0EF)
    )

    val PastelLavender = KeyboardColorScheme(
        background = Color(0xFFF6F0FC),
        surfaceContainer = Color(0xFFEDE4F7),
        keyBackground = Color(0xFFFFFFFF),
        keyPressedBackground = Color(0xFFE4D5F5),
        keySpecialBackground = Color(0xFFE2D3F5),
        keyActiveBackground = Color(0xFF8B5CF6),
        keyBorderColor = Color(0xFFDDD0F0),
        keyTextColor = Color(0xFF2E1065),
        keyTextSpecialColor = Color(0xFF4C1D95),
        keyTextActiveColor = Color(0xFFFFFFFF),
        subLabelColor = Color(0xFF7C3AED),
        candidateBackground = Color(0xFFEDE2F8),
        candidateBadgeBackground = Color(0xFF8B5CF6),
        candidateBadgeTextColor = Color(0xFFFFFFFF),
        accentColor = Color(0xFF7C3AED),
        toolbarBackground = Color(0xFFE8DBF6),
        toolbarIconColor = Color(0xFF6D28D9)
    )

    val CleanLight = KeyboardColorScheme(
        background = Color(0xFFF8F9FA),
        surfaceContainer = Color(0xFFEEF0F2),
        keyBackground = Color(0xFFFFFFFF),
        keyPressedBackground = Color(0xFFDEE2E6),
        keySpecialBackground = Color(0xFFE9ECEF),
        keyActiveBackground = Color(0xFF0D6EFD),
        keyBorderColor = Color(0xFFCED4DA),
        keyTextColor = Color(0xFF212529),
        keyTextSpecialColor = Color(0xFF495057),
        keyTextActiveColor = Color(0xFFFFFFFF),
        subLabelColor = Color(0xFF0D6EFD),
        candidateBackground = Color(0xFFF1F3F5),
        candidateBadgeBackground = Color(0xFF0D6EFD),
        candidateBadgeTextColor = Color(0xFFFFFFFF),
        accentColor = Color(0xFF0D6EFD),
        toolbarBackground = Color(0xFFE9ECEF),
        toolbarIconColor = Color(0xFF343A40)
    )

    val CyberpunkNeon = KeyboardColorScheme(
        background = Color(0xFF0F0E17),
        surfaceContainer = Color(0xFF1A1829),
        keyBackground = Color(0xFF24223A),
        keyPressedBackground = Color(0xFF3B375C),
        keySpecialBackground = Color(0xFF1E1B30),
        keyActiveBackground = Color(0xFFFF007F),
        keyBorderColor = Color(0xFF3D385F),
        keyTextColor = Color(0xFFFFFFFE),
        keyTextSpecialColor = Color(0xFF00F5D4),
        keyTextActiveColor = Color(0xFFFFFFFF),
        subLabelColor = Color(0xFF00F5D4),
        candidateBackground = Color(0xFF181528),
        candidateBadgeBackground = Color(0xFFFF007F),
        candidateBadgeTextColor = Color(0xFFFFFFFF),
        accentColor = Color(0xFFFF007F),
        toolbarBackground = Color(0xFF151324),
        toolbarIconColor = Color(0xFF00F5D4)
    )

    @Composable
    fun getScheme(preset: ThemePreset): KeyboardColorScheme {
        val m3 = MaterialTheme.colorScheme
        return when (preset) {
            ThemePreset.SYSTEM_DYNAMIC -> androidx.compose.runtime.remember(m3) { fromMaterial3(m3) }
            ThemePreset.ABYSSINIAN_HERITAGE -> AbyssinianHeritage
            ThemePreset.OLED_BLACK -> OledBlack
            ThemePreset.MIDNIGHT_BLUE -> MidnightBlue
            ThemePreset.PASTEL_LAVENDER -> PastelLavender
            ThemePreset.CLEAN_LIGHT -> CleanLight
            ThemePreset.CYBERPUNK_NEON -> CyberpunkNeon
        }
    }

    private fun fromMaterial3(m3: ColorScheme): KeyboardColorScheme {
        return KeyboardColorScheme(
            background = m3.surfaceContainer,
            surfaceContainer = m3.surfaceContainer,
            keyBackground = m3.surface,
            keyPressedBackground = m3.primaryContainer,
            keySpecialBackground = m3.surfaceVariant,
            keyActiveBackground = m3.primary,
            keyBorderColor = m3.outlineVariant,
            keyTextColor = m3.onSurface,
            keyTextSpecialColor = m3.onSurfaceVariant,
            keyTextActiveColor = m3.onPrimary,
            subLabelColor = m3.primary,
            candidateBackground = m3.surfaceContainerHighest,
            candidateBadgeBackground = m3.primaryContainer,
            candidateBadgeTextColor = m3.onPrimaryContainer,
            accentColor = m3.primary,
            toolbarBackground = m3.surfaceContainerHigh,
            toolbarIconColor = m3.onSurfaceVariant
        )
    }
}
