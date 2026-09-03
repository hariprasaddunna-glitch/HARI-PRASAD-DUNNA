package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SleekPurpleLight,
    onPrimary = SleekPurpleDark,
    primaryContainer = SleekPurple,
    onPrimaryContainer = Color.White,
    secondary = SleekBlueLight,
    onSecondary = SleekBlue,
    background = Color(0xFF1D1B20),
    surface = Color(0xFF2B2930),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF938F99)
)

private val LightColorScheme = lightColorScheme(
    primary = SleekPurple,
    onPrimary = Color.White,
    primaryContainer = SleekPurpleLight,
    onPrimaryContainer = SleekPurpleDark,
    secondary = SleekBlue,
    secondaryContainer = SleekBlueLight,
    onSecondaryContainer = SleekBlue,
    tertiary = SleekActionCard,
    background = SurfaceCanvas,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SleekSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    outlineVariant = SleekDivider
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

