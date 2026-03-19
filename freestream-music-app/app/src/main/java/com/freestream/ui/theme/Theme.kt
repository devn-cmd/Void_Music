package com.freestream.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Theme definition for FreeStream Music App.
 * 
 * Uses a Spotify-inspired dark theme with green accents.
 * The app is designed to primarily use dark theme for
 * optimal music listening experience.
 */

/**
 * Dark color scheme for the app.
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = SpotifyGreen,
    onPrimary = Color.Black,
    primaryContainer = SpotifyGreenDark,
    onPrimaryContainer = Color.White,
    
    // Secondary colors
    secondary = SpotifyGreenLight,
    onSecondary = Color.Black,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = Color.White,
    
    // Tertiary colors
    tertiary = InfoBlue,
    onTertiary = Color.White,
    tertiaryContainer = InfoBlue.copy(alpha = 0.2f),
    onTertiaryContainer = InfoBlue,
    
    // Background colors
    background = BackgroundDark,
    onBackground = TextPrimary,
    
    // Surface colors
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    surfaceTint = SpotifyGreen,
    
    // Error colors
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
    
    // Outline
    outline = DividerColor,
    outlineVariant = SurfaceVariantDark,
    
    // Inverse colors
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundDark,
    inversePrimary = SpotifyGreenLight,
    
    // Scrim
    scrim = ScrimColor
)

/**
 * Light color scheme (not typically used but provided for completeness).
 */
private val LightColorScheme = darkColorScheme(
    // Using dark scheme even for "light" to maintain app identity
    // Can be customized if light theme is desired
    primary = SpotifyGreen,
    onPrimary = Color.Black,
    primaryContainer = SpotifyGreenDark,
    onPrimaryContainer = Color.White,
    secondary = SpotifyGreenLight,
    onSecondary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White
)

/**
 * Main theme composable for FreeStream.
 * 
 * @param darkTheme Whether to use dark theme (default: true)
 * @param content Content to be themed
 */
@Composable
fun FreeStreamTheme(
    darkTheme: Boolean = true, // Default to dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

/**
 * Get the appropriate content color for a background.
 * 
 * @param backgroundColor The background color
 * @return The appropriate content color (black or white)
 */
fun contentColorFor(backgroundColor: Color): Color {
    return when (backgroundColor) {
        SpotifyGreen -> Color.Black
        SpotifyGreenLight -> Color.Black
        SpotifyGreenDark -> Color.White
        BackgroundDark -> TextPrimary
        SurfaceDark -> TextPrimary
        SurfaceVariantDark -> TextPrimary
        else -> TextPrimary
    }
}
