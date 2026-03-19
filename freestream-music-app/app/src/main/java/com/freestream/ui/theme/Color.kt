package com.freestream.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palette for FreeStream Music App.
 * 
 * Uses a Spotify-inspired dark theme with vibrant accent colors.
 * Each music source has its own identifying color.
 */

// ===== Primary Colors =====

/**
 * Primary brand color - Spotify Green.
 * Used for primary actions, buttons, and highlights.
 */
val SpotifyGreen = Color(0xFF1DB954)

/**
 * Light variant of primary color.
 */
val SpotifyGreenLight = Color(0xFF1ED760)

/**
 * Dark variant of primary color.
 */
val SpotifyGreenDark = Color(0xFF169C45)

// ===== Background Colors =====

/**
 * Main background color - Almost black.
 */
val BackgroundDark = Color(0xFF121212)

/**
 * Surface color for cards and elevated elements.
 */
val SurfaceDark = Color(0xFF181818)

/**
 * Variant surface color for dividers and subtle backgrounds.
 */
val SurfaceVariantDark = Color(0xFF282828)

/**
 * Lighter surface for hover states.
 */
val SurfaceLightDark = Color(0xFF2A2A2A)

// ===== Text Colors =====

/**
 * Primary text color - White.
 */
val TextPrimary = Color(0xFFFFFFFF)

/**
 * Secondary text color - Light gray.
 */
val TextSecondary = Color(0xFFB3B3B3)

/**
 * Tertiary text color - Darker gray for disabled/hint text.
 */
val TextTertiary = Color(0xFF6A6A6A)

// ===== Accent Colors (Source Identifiers) =====

/**
 * Jamendo source color - Green.
 */
val JamendoColor = Color(0xFF1DB954)

/**
 * Freesound source color - Orange.
 */
val FreesoundColor = Color(0xFFFF6B00)

/**
 * Archive.org source color - Purple.
 */
val ArchiveColor = Color(0xFF9B59B6)

/**
 * ccMixter source color - Blue.
 */
val CcMixterColor = Color(0xFF3498DB)

// ===== Semantic Colors =====

/**
 * Error color - Red.
 */
val ErrorRed = Color(0xFFE22134)

/**
 * Warning color - Yellow/Orange.
 */
val WarningYellow = Color(0xFFFFB800)

/**
 * Success color - Green.
 */
val SuccessGreen = Color(0xFF4CAF50)

/**
 * Info color - Blue.
 */
val InfoBlue = Color(0xFF2196F3)

// ===== Player Colors =====

/**
 * Progress bar background.
 */
val ProgressBackground = Color(0xFF4D4D4D)

/**
 * Progress bar fill.
 */
val ProgressFill = Color(0xFFFFFFFF)

/**
 * Progress bar buffer.
 */
val ProgressBuffer = Color(0xFF7C7C7C)

// ===== Gradient Colors =====

/**
 * Top gradient color for player background.
 */
val PlayerGradientTop = Color(0xFF404040)

/**
 * Bottom gradient color for player background.
 */
val PlayerGradientBottom = Color(0xFF121212)

// ===== Utility Colors =====

/**
 * Transparent overlay for scrims.
 */
val ScrimColor = Color(0x99000000)

/**
 * Ripple effect color.
 */
val RippleColor = Color(0x33FFFFFF)

/**
 * Divider color.
 */
val DividerColor = Color(0x1FFFFFFF)

/**
 * Get source color by source type.
 * 
 * @param sourceType String identifier for the source
 * @return Color associated with the source
 */
fun getSourceColor(sourceType: String): Color {
    return when (sourceType.uppercase()) {
        "JAMENDO" -> JamendoColor
        "FREESOUND" -> FreesoundColor
        "ARCHIVE" -> ArchiveColor
        "CCMIXTER" -> CcMixterColor
        else -> SpotifyGreen
    }
}
