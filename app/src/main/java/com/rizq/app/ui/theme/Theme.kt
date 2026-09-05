package com.rizq.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val CreamPaper = Color(0xFFF7F4EB)
val EmeraldGreen = Color(0xFF0A3A25)
val AntiqueGold = Color(0xFFC5A059)
val CharcoalText = Color(0xFF2C221E)
val SoftCardBg = Color(0xFFFFFFFF)

private val LedgerColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color.White,
    secondary = AntiqueGold,
    background = CreamPaper,
    surface = SoftCardBg,
    onSurface = CharcoalText
)

@androidx.compose.runtime.Composable
fun RizqTheme(content: @androidx.compose.runtime.Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LedgerColorScheme,
        content = content
    )
}
