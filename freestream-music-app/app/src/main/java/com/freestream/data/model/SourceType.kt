package com.freestream.data.model

import androidx.compose.ui.graphics.Color

/**
 * Enum representing the four music sources integrated into FreeStream.
 * 
 * Each source has unique characteristics:
 * - JAMENDO: Primary source, 400K+ CC-licensed tracks, most reliable
 * - FREESOUND: Sound effects and samples, 120-second previews
 * - ARCHIVE: Internet Archive audio, live concerts and vintage recordings
 * - CCMIXTER: Remix culture, acapellas, collaborative music
 */
enum class SourceType {
    JAMENDO,
    FREESOUND,
    ARCHIVE,
    CCMIXTER;

    /**
     * Human-readable display name for UI presentation.
     */
    val displayName: String
        get() = when (this) {
            JAMENDO -> "Jamendo"
            FREESOUND -> "Freesound"
            ARCHIVE -> "Archive"
            CCMIXTER -> "ccMixter"
        }

    /**
     * Color associated with this source for visual identification in UI.
     * Used for badges, chips, and source indicators.
     */
    val color: Color
        get() = when (this) {
            JAMENDO -> Color(0xFF1DB954)      // Spotify Green - modern music
            FREESOUND -> Color(0xFFFF6B00)    // Orange - sound effects
            ARCHIVE -> Color(0xFF9B59B6)      // Purple - vintage/classic
            CCMIXTER -> Color(0xFF3498DB)     // Blue - remix culture
        }

    /**
     * Description of the source for tooltips and help text.
     */
    val description: String
        get() = when (this) {
            JAMENDO -> "Modern music from independent artists"
            FREESOUND -> "Sound effects and audio samples"
            ARCHIVE -> "Live concerts and vintage recordings"
            CCMIXTER -> "Remixes, acapellas, and samples"
        }

    /**
     * Whether this source requires authentication.
     */
    val requiresAuth: Boolean
        get() = when (this) {
            JAMENDO -> true      // Requires Client ID
            FREESOUND -> true    // Requires API Key
            ARCHIVE -> false     // Open API
            CCMIXTER -> false    // Open API
        }

    /**
     * Priority order for sorting results (lower = higher priority).
     * Jamendo is primary source, followed by others.
     */
    val priority: Int
        get() = when (this) {
            JAMENDO -> 0
            FREESOUND -> 1
            ARCHIVE -> 2
            CCMIXTER -> 3
        }
}
