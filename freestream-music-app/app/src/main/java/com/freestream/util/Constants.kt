package com.freestream.util

/**
 * Constants used throughout the FreeStream application.
 * 
 * Centralizes configuration values, timeouts, and default settings.
 */
object Constants {
    
    // ===== App Information =====
    
    const val APP_NAME = "FreeStream"
    const val APP_VERSION = "1.0.0"
    
    // ===== Network Timeouts =====
    
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
    
    // ===== Search Configuration =====
    
    const val DEFAULT_SEARCH_LIMIT = 20
    const val MAX_SEARCH_LIMIT = 50
    const val SEARCH_DEBOUNCE_MS = 300L
    
    // ===== Playback Configuration =====
    
    const val DEFAULT_VOLUME = 0.8f
    const val VOLUME_MAX = 1.0f
    const val VOLUME_MIN = 0.0f
    
    /**
     * Position update interval in milliseconds.
     * How often the player updates the current position.
     */
    const val POSITION_UPDATE_INTERVAL_MS = 1000L
    
    /**
     * Minimum duration to count as "played" in history (10 seconds).
     */
    const val MIN_PLAY_DURATION_MS = 10_000L
    
    /**
     * Completion percentage to count as "fully played".
     */
    const val FULL_COMPLETION_PERCENTAGE = 90
    
    // ===== History Configuration =====
    
    const val MAX_HISTORY_ENTRIES = 500
    const val DEFAULT_HISTORY_LIMIT = 100
    
    // ===== Playlist Configuration =====
    
    const val MAX_PLAYLIST_NAME_LENGTH = 50
    const val MAX_PLAYLIST_DESCRIPTION_LENGTH = 200
    
    // ===== Cache Configuration =====
    
    const val IMAGE_CACHE_SIZE_MB = 100
    const val HTTP_CACHE_SIZE_MB = 50
    
    // ===== Notification =====
    
    const val NOTIFICATION_CHANNEL_ID = "freestream_playback"
    const val NOTIFICATION_CHANNEL_NAME = "Playback"
    const val NOTIFICATION_ID = 1
    
    // ===== Deep Links =====
    
    const val DEEP_LINK_SCHEME = "freestream"
    const val DEEP_LINK_HOST = "app"
    
    // ===== API Base URLs (for reference) =====
    
    object ApiUrls {
        const val JAMENDO_BASE = "https://api.jamendo.com/v3.0/"
        const val FREESOUND_BASE = "https://freesound.org/apiv2/"
        const val ARCHIVE_BASE = "https://archive.org/"
        const val CCMIXTER_BASE = "http://ccmixter.org/api/"
    }
    
    // ===== License URLs =====
    
    object LicenseUrls {
        const val CC0 = "https://creativecommons.org/publicdomain/zero/1.0/"
        const val CC_BY = "https://creativecommons.org/licenses/by/4.0/"
        const val CC_BY_NC = "https://creativecommons.org/licenses/by-nc/4.0/"
        const val CC_BY_SA = "https://creativecommons.org/licenses/by-sa/4.0/"
        const val CC_BY_ND = "https://creativecommons.org/licenses/by-nd/4.0/"
        const val PUBLIC_DOMAIN = "https://creativecommons.org/publicdomain/mark/1.0/"
    }
    
    // ===== Error Messages =====
    
    object ErrorMessages {
        const val NETWORK_ERROR = "Network error. Please check your connection."
        const val NO_RESULTS = "No results found. Try a different search."
        const val PLAYBACK_ERROR = "Unable to play this track. Try another one."
        const val SOURCE_UNAVAILABLE = "This music source is currently unavailable."
        const val API_KEYS_NOT_CONFIGURED = "API keys not configured. Please add your keys in ApiKeys.kt"
    }
}
