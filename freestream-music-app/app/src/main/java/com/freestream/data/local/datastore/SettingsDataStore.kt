package com.freestream.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.freestream.data.model.SourceType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore for user preferences in FreeStream.
 * 
 * Stores app settings that persist across sessions:
 * - Active music sources (which sources to search)
 * - Playback preferences (shuffle, repeat)
 * - UI preferences
 * - Volume settings
 * 
 * Uses Jetpack DataStore for type-safe, asynchronous storage.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_NAME)

    // ===== Preference Keys =====
    
    private object PreferencesKeys {
        // Active sources
        val ACTIVE_SOURCES = stringSetPreferencesKey("active_sources")
        
        // Playback settings
        val SHUFFLE_ENABLED = booleanPreferencesKey("shuffle_enabled")
        val REPEAT_MODE = stringPreferencesKey("repeat_mode")
        val LAST_VOLUME = floatPreferencesKey("last_volume")
        
        // UI settings
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        val SHOW_MINI_PLAYER = booleanPreferencesKey("show_mini_player")
        
        // Last played
        val LAST_PLAYED_TRACK_ID = stringPreferencesKey("last_played_track_id")
        val LAST_PLAYLIST_ID = stringPreferencesKey("last_playlist_id")
        
        // Search history
        val RECENT_SEARCHES = stringSetPreferencesKey("recent_searches")
        
        // Notifications
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    // ===== Active Sources =====
    
    /**
     * Flow of active music sources.
     * Defaults to all sources if not set.
     */
    val activeSources: Flow<Set<SourceType>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sourceNames = preferences[PreferencesKeys.ACTIVE_SOURCES]
            sourceNames?.mapNotNull { name ->
                try {
                    SourceType.valueOf(name)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }?.toSet() ?: SourceType.values().toSet() // Default to all sources
        }

    /**
     * Save active sources.
     */
    suspend fun setActiveSources(sources: Set<SourceType>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTIVE_SOURCES] = sources.map { it.name }.toSet()
        }
    }

    /**
     * Toggle a source on/off.
     */
    suspend fun toggleSource(source: SourceType) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.ACTIVE_SOURCES]
                ?.mapNotNull { name ->
                    try {
                        SourceType.valueOf(name)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }?.toMutableSet() ?: SourceType.values().toMutableSet()
            
            if (current.contains(source)) {
                current.remove(source)
            } else {
                current.add(source)
            }
            
            preferences[PreferencesKeys.ACTIVE_SOURCES] = current.map { it.name }.toSet()
        }
    }

    // ===== Playback Settings =====
    
    /**
     * Flow of shuffle enabled state.
     */
    val shuffleEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.SHUFFLE_ENABLED] ?: false }

    /**
     * Set shuffle enabled state.
     */
    suspend fun setShuffleEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHUFFLE_ENABLED] = enabled }
    }

    /**
     * Flow of repeat mode.
     */
    val repeatMode: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.REPEAT_MODE] ?: "OFF" }

    /**
     * Set repeat mode.
     */
    suspend fun setRepeatMode(mode: String) {
        context.dataStore.edit { it[PreferencesKeys.REPEAT_MODE] = mode }
    }

    /**
     * Flow of last volume level (0.0 to 1.0).
     */
    val lastVolume: Flow<Float> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.LAST_VOLUME] ?: 0.8f }

    /**
     * Set last volume level.
     */
    suspend fun setLastVolume(volume: Float) {
        context.dataStore.edit { it[PreferencesKeys.LAST_VOLUME] = volume.coerceIn(0f, 1f) }
    }

    // ===== UI Settings =====
    
    /**
     * Flow of dark theme enabled state.
     */
    val darkThemeEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.DARK_THEME_ENABLED] ?: true } // Default to dark theme

    /**
     * Set dark theme enabled.
     */
    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DARK_THEME_ENABLED] = enabled }
    }

    /**
     * Flow of mini player visibility.
     */
    val showMiniPlayer: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.SHOW_MINI_PLAYER] ?: true }

    /**
     * Set mini player visibility.
     */
    suspend fun setShowMiniPlayer(show: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_MINI_PLAYER] = show }
    }

    // ===== Last Played =====
    
    /**
     * Flow of last played track ID.
     */
    val lastPlayedTrackId: Flow<String?> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.LAST_PLAYED_TRACK_ID] }

    /**
     * Set last played track ID.
     */
    suspend fun setLastPlayedTrackId(trackId: String?) {
        context.dataStore.edit { preferences ->
            if (trackId != null) {
                preferences[PreferencesKeys.LAST_PLAYED_TRACK_ID] = trackId
            } else {
                preferences.remove(PreferencesKeys.LAST_PLAYED_TRACK_ID)
            }
        }
    }

    /**
     * Flow of last playlist ID.
     */
    val lastPlaylistId: Flow<String?> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.LAST_PLAYLIST_ID] }

    /**
     * Set last playlist ID.
     */
    suspend fun setLastPlaylistId(playlistId: String?) {
        context.dataStore.edit { preferences ->
            if (playlistId != null) {
                preferences[PreferencesKeys.LAST_PLAYLIST_ID] = playlistId
            } else {
                preferences.remove(PreferencesKeys.LAST_PLAYLIST_ID)
            }
        }
    }

    // ===== Search History =====
    
    /**
     * Flow of recent searches.
     */
    val recentSearches: Flow<Set<String>> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.RECENT_SEARCHES] ?: emptySet() }

    /**
     * Add a search to recent searches.
     * Keeps only the last 10 searches.
     */
    suspend fun addRecentSearch(query: String) {
        if (query.isBlank()) return
        
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.RECENT_SEARCHES]?.toMutableSet() ?: mutableSetOf()
            current.add(query)
            // Keep only last 10
            if (current.size > 10) {
                val iterator = current.iterator()
                repeat(current.size - 10) { iterator.remove() }
            }
            preferences[PreferencesKeys.RECENT_SEARCHES] = current
        }
    }

    /**
     * Clear recent searches.
     */
    suspend fun clearRecentSearches() {
        context.dataStore.edit { it.remove(PreferencesKeys.RECENT_SEARCHES) }
    }

    // ===== Notifications =====
    
    /**
     * Flow of notifications enabled state.
     */
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true }

    /**
     * Set notifications enabled.
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    // ===== Clear All =====
    
    /**
     * Clear all preferences.
     * Use with caution - resets all user settings.
     */
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    companion object {
        /**
         * Name of the preferences DataStore file.
         */
        private const val SETTINGS_NAME = "freestream_settings"
    }
}
