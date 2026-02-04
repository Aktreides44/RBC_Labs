package com.example.rbclabs.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RBCColorScheme = lightColorScheme(
    primary = RBCRed,
    onPrimary = Color.White,

    secondary = RBCRed,
    onSecondary = Color.White,

    background = White,
    onBackground = Black,

    surface = White,
    onSurface = Black,

    error = RBCRed,
    onError = Color.White
)

@Composable
fun RBCLabsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RBCColorScheme,
        typography = Typography,
        content = content
    )
}
