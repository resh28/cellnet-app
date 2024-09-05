package com.example.cellnet.core.designsystem.theme

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.cellnet.core.common.model.AppTheme
import kotlinx.coroutines.flow.StateFlow

@VisibleForTesting
private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = Blue40,
    primaryContainer = Blue40,
    secondaryContainer = Black09, //text field color
    onTertiary = Black90,
    tertiaryContainer = Black20,
    error = Pink57, //Error Color
)

@VisibleForTesting
private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = Blue80,
    primaryContainer = Blue80,
    secondaryContainer = Black100, //text field color
    onTertiary = Black09,
    tertiaryContainer = Black100,
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
    appTheme: StateFlow<AppTheme>,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val applicationTheme = appTheme.collectAsState()

    val colorScheme =
        when(applicationTheme.value) {
            AppTheme.SYSTEM_DEFAULT -> when {
                isSystemInDarkTheme -> DarkColorScheme
                else -> LightColorScheme
            }
            AppTheme.DARK -> DarkColorScheme
            AppTheme.LIGHT -> LightColorScheme
        }


    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
        typography = Typography,
        shapes = Shapes
    )
}