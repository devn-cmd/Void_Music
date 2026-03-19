package com.freestream.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.freestream.data.local.entity.FavoriteTrackEntity
import com.freestream.data.model.Track
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for favorite tracks operations.
 * 
 * Provides operations for managing user's favorite tracks.
 * Favorites are stored with complete track data for offline access.
 */
@Dao
interface FavoriteDao {

    // ===== Read Operations =====
    
    /**
     * Get all favorite tracks ordered by most recently added.
     */
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    suspend fun getAll(): List<FavoriteTrackEntity>

    /**
     * Get all favorite tracks as a Flow for reactive UI updates.
     */
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFlow(): Flow<List<FavoriteTrackEntity>>

    /**
     * Get a specific favorite by track ID.
     */
    @Query("SELECT * FROM favorites WHERE trackId = :trackId")
    suspend fun getById(trackId: String): FavoriteTrackEntity?

    /**
     * Get favorite tracks by source.
     */
    @Query("SELECT * FROM favorites WHERE track.source = :source ORDER BY addedAt DESC")
    suspend fun getBySource(source: String): List<FavoriteTrackEntity>

    /**
     * Get favorite tracks as Track objects.
     */
    @Query("SELECT track FROM favorites ORDER BY addedAt DESC")
    suspend fun getAllTracks(): List<Track>

    /**
     * Get count of favorite tracks.
     */
    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getCount(): Int

    /**
     * Get count as Flow for reactive updates.
     */
    @Query("SELECT COUNT(*) FROM favorites")
    fun getCountFlow(): Flow<Int>

    // ===== Write Operations =====
    
    /**
     * Add a track to favorites.
     * Replaces if already exists.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteTrackEntity)

    /**
     * Add a track to favorites (convenience method).
     */
    suspend fun insert(track: Track) {
        insert(FavoriteTrackEntity.fromTrack(track))
    }

    /**
     * Add multiple tracks to favorites.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(favorites: List<FavoriteTrackEntity>)

    /**
     * Remove a track from favorites.
     */
    @Delete
    suspend fun delete(favorite: FavoriteTrackEntity)

    /**
     * Remove a track from favorites by ID.
     */
    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun delete(trackId: String)

    /**
     * Remove multiple tracks from favorites.
     */
    @Query("DELETE FROM favorites WHERE trackId IN (:trackIds)")
    suspend fun deleteAll(trackIds: List<String>)

    // ===== Utility Operations =====
    
    /**
     * Check if a track is favorited.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: String): Boolean

    /**
     * Check if a track is favorited as Flow.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    fun isFavoriteFlow(trackId: String): Flow<Boolean>

    /**
     * Toggle favorite status of a track.
     * Returns true if track is now favorited, false if removed.
     */
    suspend fun toggleFavorite(track: Track): Boolean {
        return if (isFavorite(track.id)) {
            delete(track.id)
            false
        } else {
            insert(track)
            true
        }
    }

    /**
     * Delete all favorites.
     */
    @Query("DELETE FROM favorites")
    suspend fun deleteAll()

    /**
     * Search favorites by track title or artist.
     */
    @Query("SELECT * FROM favorites WHERE track.title LIKE '%' || :query || '%' OR track.artist LIKE '%' || :query || '%' ORDER BY addedAt DESC")
    suspend fun search(query: String): List<FavoriteTrackEntity>

    /**
     * Get recently added favorites (last N).
     */
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<FavoriteTrackEntity>
}
