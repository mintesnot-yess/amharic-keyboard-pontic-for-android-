package com.example.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.preferences.ClickEffectStyle
import com.example.preferences.KeyFontSize
import com.example.preferences.KeyShapeStyle
import com.example.ui.theme.KeyboardColorScheme

@Composable
fun KeyboardKey(
    action: KeyAction,
    label: String,
    subLabel: String? = null,
    flickSymbol: String? = null,
    modifier: Modifier = Modifier,
    isSpecial: Boolean = false,
    isActive: Boolean = false,
    isSpacebar: Boolean = false,
    scheme: KeyboardColorScheme,
    keyShape: KeyShapeStyle = KeyShapeStyle.ROUNDED,
    hasBorder: Boolean = false,
    fontSize: KeyFontSize = KeyFontSize.NORMAL,
    clickEffect: ClickEffectStyle = ClickEffectStyle.POPUP_BUBBLE,
    flickEnabled: Boolean = true,
    quickDeleteEnabled: Boolean = true,
    spacebarSwipeEnabled: Boolean = true,
    onAction: (KeyAction) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var showGeezPopup by remember { mutableStateOf(false) }

    val shape = remember(keyShape.cornerRadius) { RoundedCornerShape(keyShape.cornerRadius) }
    val bubbleShape = remember { RoundedCornerShape(14.dp) }

    val keyBackground = when {
        isPressed -> scheme.keyPressedBackground
        isActive -> scheme.keyActiveBackground
        isSpecial -> scheme.keySpecialBackground
        else -> scheme.keyBackground
    }

    val contentColor = when {
        isPressed -> scheme.keyTextColor
        isActive -> scheme.keyTextActiveColor
        isSpecial -> scheme.keyTextSpecialColor
        else -> scheme.keyTextColor
    }

    // Direct reactive scale without allocating heavy background animation engines
    val scale = if (isPressed && clickEffect == ClickEffectStyle.RIPPLE_POPUP) 0.94f else 1.0f

    // Check Ge'ez orders for Amharic consonant key
    val geezOrders = remember(label) {
        if (label.length == 1 && label[0].isLetter()) {
            FlickAndSymbolRules.getGeezFamily(label)
        } else null
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.5.dp)
            .height(46.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating magnified pop-up preview above the key when pressed
        if (isPressed && clickEffect != ClickEffectStyle.SUBTLE && label.length <= 2 && !isSpecial) {
            Surface(
                modifier = Modifier
                    .offset { IntOffset(0, -115) }
                    .size(width = 56.dp, height = 56.dp)
                    .shadow(8.dp, bubbleShape),
                color = scheme.surfaceContainer,
                shape = bubbleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = scheme.accentColor
                        )
                        if (subLabel != null) {
                            Text(
                                text = subLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = scheme.subLabelColor
                            )
                        }
                    }
                }
            }
        }

        // Main Key Body: Optimized flat clip + background to eliminate popup rendering lag
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .clip(shape)
                .background(keyBackground)
                .then(
                    if (hasBorder && scheme.keyBorderColor != null) {
                        Modifier.border(1.dp, scheme.keyBorderColor, shape)
                    } else Modifier
                )
                .pointerInput(action, flickEnabled, quickDeleteEnabled, spacebarSwipeEnabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            // Guaranteed reliable, instantaneous tap execution for every key
                            onAction(action)
                        },
                        onLongPress = {
                            when {
                                action is KeyAction.Backspace -> {
                                    // Long press backspace triggers word deletion
                                    onAction(KeyAction.DeleteWords(1))
                                }
                                isSpacebar -> {
                                    // Long press spacebar toggles mode
                                    onAction(KeyAction.ToggleMode)
                                }
                                geezOrders != null && geezOrders.isNotEmpty() -> {
                                    showGeezPopup = true
                                }
                                flickSymbol != null -> {
                                    onAction(KeyAction.Text(flickSymbol))
                                }
                                else -> {
                                    onAction(action)
                                }
                            }
                        }
                    )
                }
                .testTag("key_${label.lowercase().replace(" ", "_")}"),
            contentAlignment = Alignment.Center
        ) {
            // Secondary symbol badge on top right of the key (for flick/long press, dates, addresses)
            if (flickEnabled && flickSymbol != null && label.length == 1) {
                Text(
                    text = flickSymbol,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = scheme.subLabelColor.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 3.dp, top = 2.dp)
                )
            }

            when (action) {
                is KeyAction.Backspace -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Backspace",
                        tint = contentColor
                    )
                }
                is KeyAction.Shift -> {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Shift",
                        tint = contentColor
                    )
                }
                is KeyAction.Enter -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enter",
                        tint = contentColor
                    )
                }
                is KeyAction.SwitchIme -> {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Switch Input Method",
                        tint = contentColor
                    )
                }
                is KeyAction.HideKeyboard -> {
                    Icon(
                        imageVector = Icons.Default.KeyboardHide,
                        contentDescription = "Hide Keyboard",
                        tint = contentColor
                    )
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(1.dp)
                    ) {
                        Text(
                            text = label,
                            fontWeight = if (isSpecial) FontWeight.Medium else FontWeight.SemiBold,
                            fontSize = if (label.length > 3) 12.sp else fontSize.size,
                            color = contentColor
                        )
                        if (subLabel != null) {
                            Text(
                                text = subLabel,
                                fontSize = fontSize.subLabelSize,
                                fontWeight = FontWeight.Medium,
                                color = if (isActive) contentColor.copy(alpha = 0.85f) else scheme.subLabelColor
                            )
                        }
                    }
                }
            }
        }

        // Ge'ez Orders Popup for consonant keys
        if (showGeezPopup && geezOrders != null) {
            GeezOrderPopup(
                orders = geezOrders,
                scheme = scheme,
                onSelectOrder = { selectedLetter ->
                    onAction(KeyAction.Text(selectedLetter))
                    showGeezPopup = false
                },
                onDismiss = {
                    showGeezPopup = false
                }
            )
        }
    }
}
