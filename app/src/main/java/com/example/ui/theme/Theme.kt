package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    secondary = AmberSecondary,
    tertiary = TealTertiary,
    background = MidnightBg,
    surface = CardSlate,
    onPrimary = SoftWhiteText,
    onSecondary = MidnightBg,
    onBackground = SoftWhiteText,
    onSurface = SoftWhiteText,
    error = CrimsonError
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    secondary = AmberSecondary,
    tertiary = TealTertiary,
    background = CreamBg,
    surface = CardLight,
    onPrimary = SoftWhiteText,
    onSecondary = DarkNavyText,
    onBackground = DarkNavyText,
    onSurface = DarkNavyText,
    error = CrimsonError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We force darkTheme to be customizable, defaulting to system but keeping a highly premium look
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
