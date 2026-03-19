package com.freestream.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.freestream.data.model.Track

/**
 * Room Entity for storing favorite tracks.
 * 
 * Stores the complete Track object so favorites work offline
 * and persist even if the remote source becomes unavailable.
 * 
 * @property trackId Composite track ID (primary key)
 * @property track Complete track data (embedded)
 * @property addedAt Timestamp when track was added to favorites
 */
@Entity(tableName = "favorites")
data class FavoriteTrackEntity(
    
    @PrimaryKey
    val trackId: String,
    
    /**
     * Complete track data embedded in this entity.
     * This allows favorites to work offline.
     */
    @Embedded
    val track: Track,
    
    val addedAt: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * Create a FavoriteEntity from a Track.
         */
        fun fromTrack(track: Track): FavoriteTrackEntity {
            return FavoriteTrackEntity(
                trackId = track.id,
                track = track
            )
        }
    }
}
