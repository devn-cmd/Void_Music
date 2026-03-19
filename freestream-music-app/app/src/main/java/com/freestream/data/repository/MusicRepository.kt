package com.freestream.data.repository

import android.util.Log
import com.freestream.data.local.datastore.SettingsDataStore
import com.freestream.data.model.Playlist
import com.freestream.data.model.SourceType
import com.freestream.data.model.Track
import com.freestream.data.remote.source.ArchiveOrgSource
import com.freestream.data.remote.source.CcMixterSource
import com.freestream.data.remote.source.FreesoundSource
import com.freestream.data.remote.source.JamendoSource
import com.freestream.data.remote.source.Result
import com.freestream.data.local.database.FavoriteDao
import com.freestream.data.local.database.HistoryDao
import com.freestream.data.local.database.PlaylistDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that aggregates all music sources and local data.
 * 
 * This is the main data access layer for the app. It:
 * - Performs parallel searches across all active sources
 * - Manages playlists, favorites, and history
 * - Provides a unified interface for all data operations
 * 
 * Uses the Repository pattern to abstract data sources from ViewModels.
 */
@Singleton
class MusicRepository @Inject constructor(
    private val jamendoSource: JamendoSource,
    private val freesoundSource: FreesoundSource,
    private val archiveSource: ArchiveOrgSource,
    private val ccmixterSource: CcMixterSource,
    private val playlistDao: PlaylistDao,
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao,
    private val settingsDataStore: SettingsDataStore
) {
    companion object {
        private const val TAG = "MusicRepository"
    }

    /**
     * All available music sources.
     */
    private val allSources = listOf(
        jamendoSource,
        freesoundSource,
        archiveSource,
        ccmixterSource
    )

    // ===== Search Operations =====
    
    /**
     * Search across all active music sources in parallel.
     * 
     * Results are:
     * 1. Merged from all sources
     * 2. Deduplicated by stream URL
     * 3. Sorted by relevance (exact matches first, then by source priority)
     * 
     * @param query Search query string
     * @return List of tracks from all sources
     */
    suspend fun search(query: String): List<Track> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        
        Log.d(TAG, "Searching for: $query")
        
        // Get active sources from settings
        val activeSourceTypes = settingsDataStore.activeSources.first()
        val activeSources = allSources.filter { activeSourceTypes.contains(it.sourceType) }
        
        Log.d(TAG, "Active sources: ${activeSources.map { it.sourceName }}")
        
        // Fire all searches in parallel
        val deferredResults = coroutineScope {
            activeSources.map { source ->
                async {
                    try {
                        when (val result = source.search(query, 20)) {
                            is Result.Success -> {
                                Log.d(TAG, "${source.sourceName} returned ${result.data.size} results")
                                result.data
                            }
                            is Result.Error -> {
                                Log.e(TAG, "${source.sourceName} error: ${result.message}")
                                emptyList()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "${source.sourceName} exception", e)
                        emptyList()
                    }
                }
            }
        }
        
        // Merge results
        val allTracks = deferredResults.awaitAll().flatten()
        
        // Deduplicate by stream URL
        val uniqueTracks = allTracks.distinctBy { it.streamUrl }
        
        // Sort: exact title matches first, then by source priority
        val sortedTracks = uniqueTracks.sortedWith(
            compareByDescending<Track> { 
                it.title.contains(query, ignoreCase = true) 
            }.thenBy { 
                it.source.priority
            }
        )
        
        Log.d(TAG, "Total unique results: ${sortedTracks.size}")
        sortedTracks
    }

    /**
     * Get trending tracks from the primary source (Jamendo).
     * 
     * @param limit Maximum number of results
     * @return List of trending tracks
     */
    suspend fun getTrending(limit: Int = 20): List<Track> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching trending tracks")
        
        when (val result = jamendoSource.getTrending(limit)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e(TAG, "Failed to get trending: ${result.message}")
                emptyList()
            }
        }
    }

    /**
     * Get recent tracks from the primary source (Jamendo).
     * 
     * @param limit Maximum number of results
     * @return List of recent tracks
     */
    suspend fun getRecent(limit: Int = 20): List<Track> = withContext(Dispatchers.IO) {
        when (val result = jamendoSource.getRecent(limit)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e(TAG, "Failed to get recent: ${result.message}")
                emptyList()
            }
        }
    }

    // ===== Source Management =====
    
    /**
     * Flow of active source types.
     */
    val activeSources: Flow<Set<SourceType>> = settingsDataStore.activeSources

    /**
     * Toggle a source on/off.
     */
    suspend fun toggleSource(sourceType: SourceType) {
        settingsDataStore.toggleSource(sourceType)
    }

    /**
     * Set active sources.
     */
    suspend fun setActiveSources(sources: Set<SourceType>) {
        settingsDataStore.setActiveSources(sources)
    }

    /**
     * Check if a source is available.
     */
    suspend fun isSourceAvailable(sourceType: SourceType): Boolean {
        return when (sourceType) {
            SourceType.JAMENDO -> jamendoSource.isAvailable()
            SourceType.FREESOUND -> freesoundSource.isAvailable()
            SourceType.ARCHIVE -> archiveSource.isAvailable()
            SourceType.CCMIXTER -> ccmixterSource.isAvailable()
        }
    }

    // ===== Playlist Operations =====
    
    /**
     * Get all playlists.
     */
    fun getPlaylists(): Flow<List<Playlist>> = 
        playlistDao.getAllFlow().map { entities ->
            entities.map { it.toPlaylist() }
        }

    /**
     * Get a specific playlist.
     */
    suspend fun getPlaylist(playlistId: String): Playlist? = 
        playlistDao.getById(playlistId)?.toPlaylist()

    /**
     * Create a new playlist.
     * 
     * @param name Playlist name
     * @param description Optional description
     * @return ID of the created playlist
     */
    suspend fun createPlaylist(name: String, description: String? = null): String {
        val entity = com.freestream.data.local.entity.PlaylistEntity(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            description = description,
            trackIds = emptyList()
        )
        playlistDao.insert(entity)
        return entity.id
    }

    /**
     * Delete a playlist.
     */
    suspend fun deletePlaylist(playlistId: String) {
        playlistDao.deleteById(playlistId)
    }

    /**
     * Rename a playlist.
     */
    suspend fun renamePlaylist(playlistId: String, newName: String) {
        playlistDao.rename(playlistId, newName)
    }

    /**
     * Add a track to a playlist.
     */
    suspend fun addToPlaylist(playlistId: String, trackId: String) {
        playlistDao.addTrack(playlistId, trackId)
    }

    /**
     * Remove a track from a playlist.
     */
    suspend fun removeFromPlaylist(playlistId: String, trackId: String) {
        playlistDao.removeTrack(playlistId, trackId)
    }

    /**
     * Toggle playlist favorite status.
     */
    suspend fun togglePlaylistFavorite(playlistId: String) {
        playlistDao.toggleFavorite(playlistId)
    }

    // ===== Favorites Operations =====
    
    /**
     * Get all favorite tracks.
     */
    fun getFavorites(): Flow<List<Track>> = 
        favoriteDao.getAllFlow().map { entities ->
            entities.map { it.track }
        }

    /**
     * Check if a track is favorited.
     */
    fun isFavorite(trackId: String): Flow<Boolean> = 
        favoriteDao.isFavoriteFlow(trackId)

    /**
     * Toggle favorite status of a track.
     */
    suspend fun toggleFavorite(track: Track): Boolean {
        return favoriteDao.toggleFavorite(track)
    }

    /**
     * Add a track to favorites.
     */
    suspend fun addToFavorites(track: Track) {
        favoriteDao.insert(track)
    }

    /**
     * Remove a track from favorites.
     */
    suspend fun removeFromFavorites(trackId: String) {
        favoriteDao.delete(trackId)
    }

    // ===== History Operations =====
    
    /**
     * Get playback history.
     */
    fun getHistory(limit: Int = 100): Flow<List<com.freestream.data.local.entity.HistoryEntryEntity>> = 
        historyDao.getRecentFlow(limit)

    /**
     * Add a history entry.
     */
    suspend fun addToHistory(trackId: String, completionPercentage: Int, playDurationMs: Long) {
        historyDao.insert(
            com.freestream.data.local.entity.HistoryEntryEntity(
                trackId = trackId,
                completionPercentage = completionPercentage,
                playDurationMs = playDurationMs
            )
        )
    }

    /**
     * Clear playback history.
     */
    suspend fun clearHistory() {
        historyDao.deleteAll()
    }

    // ===== Utility =====
    
    /**
     * Get attribution text for a track.
     */
    fun getAttributionText(track: Track): String {
        return when (track.source) {
            SourceType.JAMENDO -> jamendoSource.getAttributionText(track)
            SourceType.FREESOUND -> freesoundSource.getAttributionText(track)
            SourceType.ARCHIVE -> archiveSource.getAttributionText(track)
            SourceType.CCMIXTER -> ccmixterSource.getAttributionText(track)
        }
    }

    /**
     * Get license URL for a track.
     */
    fun getLicenseUrl(track: Track): String? {
        return when (track.source) {
            SourceType.JAMENDO -> jamendoSource.getLicenseUrl(track)
            SourceType.FREESOUND -> freesoundSource.getLicenseUrl(track)
            SourceType.ARCHIVE -> archiveSource.getLicenseUrl(track)
            SourceType.CCMIXTER -> ccmixterSource.getLicenseUrl(track)
        }
    }

    // ===== Extension Functions =====
    
    /**
     * Convert PlaylistEntity to Playlist domain model.
     */
    private fun com.freestream.data.local.entity.PlaylistEntity.toPlaylist(): Playlist {
        return Playlist(
            id = id,
            name = name,
            description = description,
            trackIds = trackIds,
            tracks = emptyList(), // Would need to fetch full tracks
            coverArtUrl = coverArtUrl,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isFavorite = isFavorite
        )
    }
}
