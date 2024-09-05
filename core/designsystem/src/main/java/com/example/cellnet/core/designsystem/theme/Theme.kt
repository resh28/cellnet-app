package com.example.cellnet.core.designsystem.theme

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@VisibleForTesting
private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = Blue40,
    primaryContainer = Blue40,
    secondaryContainer = Black09, //text field color
    error = Pink57, //Error Color
)

@VisibleForTesting
private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = Blue80,
    primaryContainer = Blue80,
    secondaryContainer = Black100, //text field color
    error = Red42, //Error Color
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun CellnetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
        typography = Typography,
        shapes = Shapes
    )
}