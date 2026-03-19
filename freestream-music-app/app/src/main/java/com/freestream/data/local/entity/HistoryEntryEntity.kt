package com.freestream.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for storing playback history.
 * 
 * Tracks what the user has listened to and how much they listened.
 * Used for "Recently Played" and recommendations.
 * 
 * @property id Auto-generated primary key
 * @property trackId Composite track ID that was played
 * @property playedAt Timestamp when track was played
 * @property completionPercentage How much of the track was played (0-100)
 * @property playDurationMs Actual playback duration in milliseconds
 */
@Entity(tableName = "history")
data class HistoryEntryEntity(
    
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val trackId: String,
    
    val playedAt: Long = System.currentTimeMillis(),
    
    /**
     * Completion percentage (0-100).
     * 100 = fully played, 50 = half played, etc.
     */
    val completionPercentage: Int = 0,
    
    /**
     * Actual time spent listening in milliseconds.
     * May differ from track duration if user skipped.
     */
    val playDurationMs: Long = 0
) {
    /**
     * Whether the track was fully played (>= 90%).
     */
    val wasFullyPlayed: Boolean
        get() = completionPercentage >= 90
    
    /**
     * Whether the track was skipped early (< 10%).
     */
    val wasSkipped: Boolean
        get() = completionPercentage < 10
    
    companion object {
        /**
         * Minimum completion percentage to count as "played".
         */
        const val MIN_COMPLETION_FOR_PLAYED = 10
        
        /**
         * Completion percentage to count as "fully played".
         */
        const val FULL_COMPLETION_THRESHOLD = 90
    }
}
