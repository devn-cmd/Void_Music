package com.freestream.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.freestream.data.local.entity.HistoryEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for playback history operations.
 * 
 * Provides operations for tracking what the user has listened to.
 * History entries include completion percentage for analytics.
 */
@Dao
interface HistoryDao {

    // ===== Read Operations =====
    
    /**
     * Get all history entries ordered by most recently played.
     */
    @Query("SELECT * FROM history ORDER BY playedAt DESC")
    suspend fun getAll(): List<HistoryEntryEntity>

    /**
     * Get all history entries as a Flow for reactive UI updates.
     */
    @Query("SELECT * FROM history ORDER BY playedAt DESC")
    fun getAllFlow(): Flow<List<HistoryEntryEntity>>

    /**
     * Get recent history entries (last N).
     */
    @Query("SELECT * FROM history ORDER BY playedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<HistoryEntryEntity>

    /**
     * Get recent history as Flow.
     */
    @Query("SELECT * FROM history ORDER BY playedAt DESC LIMIT :limit")
    fun getRecentFlow(limit: Int): Flow<List<HistoryEntryEntity>>

    /**
     * Get history for a specific track.
     */
    @Query("SELECT * FROM history WHERE trackId = :trackId ORDER BY playedAt DESC")
    suspend fun getForTrack(trackId: String): List<HistoryEntryEntity>

    /**
     * Get play count for a track.
     */
    @Query("SELECT COUNT(*) FROM history WHERE trackId = :trackId")
    suspend fun getPlayCount(trackId: String): Int

    /**
     * Get total history count.
     */
    @Query("SELECT COUNT(*) FROM history")
    suspend fun getCount(): Int

    /**
     * Get count as Flow.
     */
    @Query("SELECT COUNT(*) FROM history")
    fun getCountFlow(): Flow<Int>

    /**
     * Get unique tracks in history (most recent play per track).
     */
    @Query("SELECT * FROM history GROUP BY trackId HAVING MAX(playedAt) ORDER BY playedAt DESC LIMIT :limit")
    suspend fun getUniqueTracks(limit: Int): List<HistoryEntryEntity>

    // ===== Write Operations =====
    
    /**
     * Insert a new history entry.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntryEntity)

    /**
     * Insert multiple history entries.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<HistoryEntryEntity>)

    /**
     * Delete a history entry.
     */
    @Delete
    suspend fun delete(entry: HistoryEntryEntity)

    /**
     * Delete history entry by ID.
     */
    @Query("DELETE FROM history WHERE id = :entryId")
    suspend fun deleteById(entryId: Long)

    /**
     * Delete history for a specific track.
     */
    @Query("DELETE FROM history WHERE trackId = :trackId")
    suspend fun deleteForTrack(trackId: String)

    // ===== Utility Operations =====
    
    /**
     * Delete all history entries.
     */
    @Query("DELETE FROM history")
    suspend fun deleteAll()

    /**
     * Delete history older than specified timestamp.
     */
    @Query("DELETE FROM history WHERE playedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    /**
     * Keep only the most recent N entries, delete the rest.
     */
    @Query("DELETE FROM history WHERE id NOT IN (SELECT id FROM history ORDER BY playedAt DESC LIMIT :keepCount)")
    suspend fun trimToSize(keepCount: Int)

    /**
     * Check if a track has been played.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM history WHERE trackId = :trackId)")
    suspend fun hasBeenPlayed(trackId: String): Boolean

    /**
     * Get total listening time in milliseconds.
     */
    @Query("SELECT SUM(playDurationMs) FROM history")
    suspend fun getTotalListeningTime(): Long?

    /**
     * Get average completion percentage.
     */
    @Query("SELECT AVG(completionPercentage) FROM history")
    suspend fun getAverageCompletion(): Float?

    /**
     * Get most played tracks (by play count).
     */
    @Query("SELECT trackId, COUNT(*) as playCount FROM history GROUP BY trackId ORDER BY playCount DESC LIMIT :limit")
    suspend fun getMostPlayed(limit: Int): List<TrackPlayCount>

    /**
     * Data class for most played query results.
     */
    data class TrackPlayCount(
        val trackId: String,
        val playCount: Int
    )
}
