package com.gayatripatel.marketplace.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1B6B4A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA8F2C8),
    onPrimaryContainer = Color(0xFF002113),
    secondary = Color(0xFF4E6356),
    secondaryContainer = Color(0xFFD0E8D7),
    onSecondaryContainer = Color(0xFF0B1F15),
    background = Color(0xFFF6FBF4),
    surface = Color(0xFFF6FBF4),
    surfaceContainerLow = Color(0xFFF0F5EE),
    surfaceContainer = Color(0xFFEAEFE9),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8CD5AD),
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF005234),
    onPrimaryContainer = Color(0xFFA8F2C8),
    secondary = Color(0xFFB4CCBB),
    secondaryContainer = Color(0xFF364B3F),
    onSecondaryContainer = Color(0xFFD0E8D7),
    background = Color(0xFF0F1511),
    surface = Color(0xFF0F1511),
    surfaceContainerLow = Color(0xFF171D19),
    surfaceContainer = Color(0xFF1B211D),
)

/**
 * Brand theme. Dynamic colour is intentionally off so the brand stays consistent
 * and screenshot tests are deterministic on every device.
 */
@Composable
fun MarketplaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
