package com.freestream.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.freestream.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for playlist operations.
 * 
 * Provides CRUD operations for user-created playlists.
 * All operations are suspend functions for coroutine compatibility.
 */
@Dao
interface PlaylistDao {

    // ===== Read Operations =====
    
    /**
     * Get all playlists ordered by most recently updated.
     */
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    suspend fun getAll(): List<PlaylistEntity>

    /**
     * Get all playlists as a Flow for reactive UI updates.
     */
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllFlow(): Flow<List<PlaylistEntity>>

    /**
     * Get a specific playlist by ID.
     */
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getById(playlistId: String): PlaylistEntity?

    /**
     * Get favorite playlists.
     */
    @Query("SELECT * FROM playlists WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    suspend fun getFavorites(): List<PlaylistEntity>

    /**
     * Search playlists by name.
     */
    @Query("SELECT * FROM playlists WHERE name LIKE '%' || :query || '%' ORDER BY name")
    suspend fun searchByName(query: String): List<PlaylistEntity>

    /**
     * Get playlist count.
     */
    @Query("SELECT COUNT(*) FROM playlists")
    suspend fun getCount(): Int

    // ===== Write Operations =====
    
    /**
     * Insert a new playlist.
     * Replaces if ID already exists.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: PlaylistEntity)

    /**
     * Insert multiple playlists.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlists: List<PlaylistEntity>)

    /**
     * Update an existing playlist.
     */
    @Update
    suspend fun update(playlist: PlaylistEntity)

    /**
     * Delete a playlist.
     */
    @Delete
    suspend fun delete(playlist: PlaylistEntity)

    /**
     * Delete a playlist by ID.
     */
    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deleteById(playlistId: String)

    // ===== Track Management =====
    
    /**
     * Add a track to a playlist.
     * Appends to the end of the track list.
     */
    @Transaction
    suspend fun addTrack(playlistId: String, trackId: String) {
        val playlist = getById(playlistId) ?: return
        val updatedTrackIds = playlist.trackIds.toMutableList().apply { add(trackId) }
        update(playlist.copy(
            trackIds = updatedTrackIds,
            updatedAt = System.currentTimeMillis()
        ))
    }

    /**
     * Remove a track from a playlist.
     */
    @Transaction
    suspend fun removeTrack(playlistId: String, trackId: String) {
        val playlist = getById(playlistId) ?: return
        val updatedTrackIds = playlist.trackIds.filter { it != trackId }
        update(playlist.copy(
            trackIds = updatedTrackIds,
            updatedAt = System.currentTimeMillis()
        ))
    }

    /**
     * Reorder tracks in a playlist.
     */
    @Transaction
    suspend fun reorderTracks(playlistId: String, newOrder: List<String>) {
        val playlist = getById(playlistId) ?: return
        update(playlist.copy(
            trackIds = newOrder,
            updatedAt = System.currentTimeMillis()
        ))
    }

    /**
     * Move a track to a new position in the playlist.
     */
    @Transaction
    suspend fun moveTrack(playlistId: String, fromIndex: Int, toIndex: Int) {
        val playlist = getById(playlistId) ?: return
        val mutableList = playlist.trackIds.toMutableList()
        
        if (fromIndex in mutableList.indices && toIndex in mutableList.indices) {
            val track = mutableList.removeAt(fromIndex)
            mutableList.add(toIndex, track)
            update(playlist.copy(
                trackIds = mutableList,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }

    // ===== Utility Operations =====
    
    /**
     * Toggle favorite status of a playlist.
     */
    @Transaction
    suspend fun toggleFavorite(playlistId: String) {
        val playlist = getById(playlistId) ?: return
        update(playlist.copy(
            isFavorite = !playlist.isFavorite,
            updatedAt = System.currentTimeMillis()
        ))
    }

    /**
     * Rename a playlist.
     */
    @Transaction
    suspend fun rename(playlistId: String, newName: String) {
        val playlist = getById(playlistId) ?: return
        update(playlist.copy(
            name = newName,
            updatedAt = System.currentTimeMillis()
        ))
    }

    /**
     * Update playlist description.
     */
    @Transaction
    suspend fun updateDescription(playlistId: String, newDescription: String?) {
        val playlist = getById(playlistId) ?: return
        update(playlist.copy(
            description = newDescription,
            updatedAt = System.currentTimeMillis()
        ))
    }

    /**
     * Delete all playlists.
     */
    @Query("DELETE FROM playlists")
    suspend fun deleteAll()

    /**
     * Check if a playlist exists.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM playlists WHERE id = :playlistId)")
    suspend fun exists(playlistId: String): Boolean

    /**
     * Check if a track is in a playlist.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM playlists WHERE id = :playlistId AND trackIds LIKE '%' || :trackId || '%')")
    suspend fun containsTrack(playlistId: String, trackId: String): Boolean
}
