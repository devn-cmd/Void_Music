package com.freestream.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.freestream.data.local.database.Converters

/**
 * Room Entity for storing user-created playlists.
 * 
 * Playlists contain an ordered list of track IDs that reference
 * tracks stored in the favorites table or fetched from remote sources.
 * 
 * @property id Unique playlist identifier (UUID)
 * @property name Playlist display name
 * @property description Optional playlist description
 * @property trackIds Ordered list of track composite IDs
 * @property coverArtUrl Optional custom cover art URL
 * @property createdAt Timestamp when playlist was created
 * @property updatedAt Timestamp when playlist was last modified
 * @property isFavorite Whether this playlist is marked as favorite
 */
@Entity(tableName = "playlists")
@TypeConverters(Converters::class)
data class PlaylistEntity(
    
    @PrimaryKey
    val id: String,
    
    val name: String,
    
    val description: String? = null,
    
    /**
     * Ordered list of track IDs in the playlist.
     * These are composite IDs (e.g., "jamendo_12345").
     */
    val trackIds: List<String> = emptyList(),
    
    val coverArtUrl: String? = null,
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val updatedAt: Long = System.currentTimeMillis(),
    
    val isFavorite: Boolean = false
) {
    /**
     * Number of tracks in the playlist.
     */
    val trackCount: Int
        get() = trackIds.size
    
    /**
     * Whether the playlist is empty.
     */
    val isEmpty: Boolean
        get() = trackIds.isEmpty()
}
