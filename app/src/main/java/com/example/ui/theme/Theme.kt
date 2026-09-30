package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PidayabPrimary,
    onPrimary = Color(0xFF00382F),
    primaryContainer = Color(0xFF005144),
    onPrimaryContainer = Color(0xFF73F8D4),
    secondary = PidayabAccent,
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF633F00),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = PidayabTertiary,
    background = PidayabDarkBg,
    onBackground = TextPrimaryDark,
    surface = PidayabDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = PidayabDarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = PidayabDarkBorder,
    error = RiskVeryHigh,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PidayabPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF73F8D4),
    onPrimaryContainer = Color(0xFF00201A),
    secondary = Color(0xFF8B5000),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDB3),
    onSecondaryContainer = Color(0xFF2C1600),
    tertiary = Color(0xFFA04026),
    background = PidayabLightBg,
    onBackground = TextPrimaryLight,
    surface = PidayabLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = PidayabLightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = PidayabLightBorder,
    error = RiskVeryHigh,
    onError = Color.White
)

@Composable
fun PidayabTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward-compatible alias for any callers
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    PidayabTheme(darkTheme = darkTheme, content = content)
}
