package com.example.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.ui.theme.KeyboardColorScheme

@Composable
fun GeezOrderPopup(
    orders: List<String>,
    scheme: KeyboardColorScheme,
    onSelectOrder: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Popup(
        alignment = Alignment.TopCenter,
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp)),
            color = scheme.surfaceContainer,
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ge'ez Orders (የፊደል ረድፎች)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = scheme.accentColor,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    orders.forEachIndexed { index, letter ->
                        val orderName = when (index) {
                            0 -> "1ኛ"
                            1 -> "2ኛ"
                            2 -> "3ኛ"
                            3 -> "4ኛ"
                            4 -> "5ኛ"
                            5 -> "6ኛ"
                            6 -> "7ኛ"
                            else -> "8ኛ"
                        }

                        Box(
                            modifier = Modifier
                                .size(width = 38.dp, height = 48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(scheme.keyBackground)
                                .clickable {
                                    onSelectOrder(letter)
                                    onDismiss()
                                }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = letter,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = scheme.keyTextColor
                                )
                                Text(
                                    text = orderName,
                                    fontSize = 9.sp,
                                    color = scheme.subLabelColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
