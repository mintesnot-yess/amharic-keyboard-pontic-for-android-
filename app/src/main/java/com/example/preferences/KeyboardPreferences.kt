package com.example.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ThemePreset(val displayName: String, val description: String) {
    SYSTEM_DYNAMIC("Dynamic (Material 3)", "Adapts to system wallpaper and dark/light mode"),
    ABYSSINIAN_HERITAGE("Abyssinian Heritage", "Rich Ethiopian emerald, gold and warm crimson"),
    OLED_BLACK("OLED Pitch Black", "True pitch black with high-contrast glowing accents"),
    MIDNIGHT_BLUE("Midnight Sapphire", "Deep dark navy blue with arctic ice highlights"),
    PASTEL_LAVENDER("Pastel Lavender", "Soft purple aesthetics with gentle elegance"),
    CLEAN_LIGHT("Clean Minimal Light", "Crisp paper white with modern modern typography"),
    CYBERPUNK_NEON("Cyberpunk Neon", "High-contrast dark slate with electric magenta and cyan")
}

enum class KeyShapeStyle(val displayName: String, val cornerRadius: Dp) {
    ROUNDED("Rounded (8dp)", 8.dp),
    PILL("Pill / Smooth (16dp)", 16.dp),
    SQUARE("Modern Square (4dp)", 4.dp)
}

enum class KeyFontSize(val displayName: String, val size: TextUnit, val subLabelSize: TextUnit) {
    SMALL("Small", 15.sp, 9.sp),
    NORMAL("Normal", 18.sp, 10.sp),
    LARGE("Large", 21.sp, 12.sp),
    EXTRA_LARGE("Extra Large", 25.sp, 13.sp)
}

enum class CandidateFontSize(val displayName: String, val size: TextUnit) {
    SMALL("Compact (14sp)", 14.sp),
    NORMAL("Default (17sp)", 17.sp),
    LARGE("Prominent (20sp)", 20.sp)
}

enum class ClickEffectStyle(val displayName: String) {
    POPUP_BUBBLE("Magnified Pop-up Bubble"),
    RIPPLE_POPUP("Ripple + Pop-up"),
    SUBTLE("Subtle Press Only")
}

enum class VibrationIntensity(val displayName: String, val durationMs: Long) {
    OFF("Off", 0L),
    SOFT("Soft (10ms)", 10L),
    MEDIUM("Medium (25ms)", 25L),
    STRONG("Strong (45ms)", 45L)
}

data class KeyboardPreferencesData(
    val themePreset: ThemePreset = ThemePreset.SYSTEM_DYNAMIC,
    val keyShape: KeyShapeStyle = KeyShapeStyle.ROUNDED,
    val keyBorders: Boolean = false,
    val keyFontSize: KeyFontSize = KeyFontSize.NORMAL,
    val candidateFontSize: CandidateFontSize = CandidateFontSize.NORMAL,
    val soundOnClick: Boolean = false, // Sound disabled by default as requested!
    val vibrationIntensity: VibrationIntensity = VibrationIntensity.SOFT,
    val showNumberRow: Boolean = false,
    val flickGesturesEnabled: Boolean = true,
    val quickDeleteSwipeEnabled: Boolean = true,
    val spacebarSwipeCursorEnabled: Boolean = true,
    val clickEffect: ClickEffectStyle = ClickEffectStyle.POPUP_BUBBLE,
    val useAmharicWordDivider: Boolean = false,
    val pinnedClips: Set<String> = DEFAULT_PINNED_CLIPS
) {
    companion object {
        val DEFAULT_PINNED_CLIPS = setOf(
            "ሰላም ጤና ይስጥልኝ!",
            "አመሰግናለሁ!",
            "እንደምን አለህ?",
            "መልካም ቀን ይሁንልህ!",
            "እግዚአብሔር ይመስገን",
            "እሺ"
        )
    }
}

object KeyboardPreferences {
    private const val PREFS_NAME = "amharic_keyboard_settings"

    private const val KEY_THEME = "theme_preset"
    private const val KEY_KEY_SHAPE = "key_shape"
    private const val KEY_KEY_BORDERS = "key_borders"
    private const val KEY_KEY_FONT_SIZE = "key_font_size"
    private const val KEY_CANDIDATE_FONT_SIZE = "candidate_font_size"
    private const val KEY_SOUND_ON_CLICK = "sound_on_click"
    private const val KEY_VIBRATION = "vibration_intensity"
    private const val KEY_SHOW_NUMBER_ROW = "show_number_row"
    private const val KEY_FLICK_GESTURES = "flick_gestures"
    private const val KEY_QUICK_DELETE_SWIPE = "quick_delete_swipe"
    private const val KEY_SPACEBAR_CURSOR = "spacebar_cursor"
    private const val KEY_CLICK_EFFECT = "click_effect"
    private const val KEY_WORD_DIVIDER = "word_divider"
    private const val KEY_PINNED_CLIPS = "pinned_clips"

    private var cachedPreferences: KeyboardPreferencesData? = null

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun load(context: Context, forceReload: Boolean = false): KeyboardPreferencesData {
        if (!forceReload && cachedPreferences != null) {
            return cachedPreferences!!
        }

        val prefs = getPrefs(context)
        val themeName = prefs.getString(KEY_THEME, ThemePreset.SYSTEM_DYNAMIC.name) ?: ThemePreset.SYSTEM_DYNAMIC.name
        val shapeName = prefs.getString(KEY_KEY_SHAPE, KeyShapeStyle.ROUNDED.name) ?: KeyShapeStyle.ROUNDED.name
        val fontName = prefs.getString(KEY_KEY_FONT_SIZE, KeyFontSize.NORMAL.name) ?: KeyFontSize.NORMAL.name
        val candFontName = prefs.getString(KEY_CANDIDATE_FONT_SIZE, CandidateFontSize.NORMAL.name) ?: CandidateFontSize.NORMAL.name
        val vibName = prefs.getString(KEY_VIBRATION, VibrationIntensity.SOFT.name) ?: VibrationIntensity.SOFT.name
        val effectName = prefs.getString(KEY_CLICK_EFFECT, ClickEffectStyle.POPUP_BUBBLE.name) ?: ClickEffectStyle.POPUP_BUBBLE.name

        val loaded = KeyboardPreferencesData(
            themePreset = try { ThemePreset.valueOf(themeName) } catch (_: Exception) { ThemePreset.SYSTEM_DYNAMIC },
            keyShape = try { KeyShapeStyle.valueOf(shapeName) } catch (_: Exception) { KeyShapeStyle.ROUNDED },
            keyBorders = prefs.getBoolean(KEY_KEY_BORDERS, false),
            keyFontSize = try { KeyFontSize.valueOf(fontName) } catch (_: Exception) { KeyFontSize.NORMAL },
            candidateFontSize = try { CandidateFontSize.valueOf(candFontName) } catch (_: Exception) { CandidateFontSize.NORMAL },
            soundOnClick = prefs.getBoolean(KEY_SOUND_ON_CLICK, false), // Default FALSE: remove click sound!
            vibrationIntensity = try { VibrationIntensity.valueOf(vibName) } catch (_: Exception) { VibrationIntensity.SOFT },
            showNumberRow = prefs.getBoolean(KEY_SHOW_NUMBER_ROW, false),
            flickGesturesEnabled = prefs.getBoolean(KEY_FLICK_GESTURES, true),
            quickDeleteSwipeEnabled = prefs.getBoolean(KEY_QUICK_DELETE_SWIPE, true),
            spacebarSwipeCursorEnabled = prefs.getBoolean(KEY_SPACEBAR_CURSOR, true),
            clickEffect = try { ClickEffectStyle.valueOf(effectName) } catch (_: Exception) { ClickEffectStyle.POPUP_BUBBLE },
            useAmharicWordDivider = prefs.getBoolean(KEY_WORD_DIVIDER, false),
            pinnedClips = prefs.getStringSet(KEY_PINNED_CLIPS, KeyboardPreferencesData.DEFAULT_PINNED_CLIPS)
                ?: KeyboardPreferencesData.DEFAULT_PINNED_CLIPS
        )
        cachedPreferences = loaded
        return loaded
    }

    fun save(context: Context, data: KeyboardPreferencesData) {
        cachedPreferences = data
        val prefs = getPrefs(context)
        prefs.edit()
            .putString(KEY_THEME, data.themePreset.name)
            .putString(KEY_KEY_SHAPE, data.keyShape.name)
            .putBoolean(KEY_KEY_BORDERS, data.keyBorders)
            .putString(KEY_KEY_FONT_SIZE, data.keyFontSize.name)
            .putString(KEY_CANDIDATE_FONT_SIZE, data.candidateFontSize.name)
            .putBoolean(KEY_SOUND_ON_CLICK, data.soundOnClick)
            .putString(KEY_VIBRATION, data.vibrationIntensity.name)
            .putBoolean(KEY_SHOW_NUMBER_ROW, data.showNumberRow)
            .putBoolean(KEY_FLICK_GESTURES, data.flickGesturesEnabled)
            .putBoolean(KEY_QUICK_DELETE_SWIPE, data.quickDeleteSwipeEnabled)
            .putBoolean(KEY_SPACEBAR_CURSOR, data.spacebarSwipeCursorEnabled)
            .putString(KEY_CLICK_EFFECT, data.clickEffect.name)
            .putBoolean(KEY_WORD_DIVIDER, data.useAmharicWordDivider)
            .putStringSet(KEY_PINNED_CLIPS, data.pinnedClips)
            .apply()
    }
}
