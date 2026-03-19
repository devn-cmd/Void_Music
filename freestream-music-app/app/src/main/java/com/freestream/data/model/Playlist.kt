package com.freestream.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Data class representing a user-created playlist.
 * 
 * Playlists are stored locally in Room database and contain
 * references to tracks by their composite IDs.
 * 
 * @param id Unique playlist identifier (UUID)
 * @param name Playlist display name
 * @param description Optional playlist description
 * @param trackIds Ordered list of track IDs in the playlist
 * @param tracks Full track objects (populated when loading from database)
 * @param coverArtUrl Optional custom cover art URL
 * @param createdAt Timestamp when playlist was created
 * @param updatedAt Timestamp when playlist was last modified
 * @param isFavorite Whether this playlist is marked as favorite
 */
@Parcelize
data class Playlist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val trackIds: List<String> = emptyList(),
    val tracks: List<Track> = emptyList(),
    val coverArtUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) : Parcelable {

    /**
     * Number of tracks in the playlist.
     */
    val trackCount: Int
        get() = trackIds.size

    /**
     * Human-readable track count text.
     */
    val trackCountText: String
        get() = "$trackCount ${if (trackCount == 1) "track" else "tracks"}"

    /**
     * Total duration of all tracks in the playlist.
     */
    val totalDurationMs: Long
        get() = tracks.sumOf { it.durationMs }

    /**
     * Formatted total duration text.
     */
    val totalDurationText: String
        get() = com.freestream.util.TimeUtils.formatDuration(totalDurationMs)

    /**
     * Whether the playlist is empty.
     */
    val isEmpty: Boolean
        get() = trackIds.isEmpty()

    /**
     * Whether the playlist has a custom cover art.
     */
    val hasCustomCover: Boolean
        get() = !coverArtUrl.isNullOrBlank()

    /**
     * Gets the artwork URL to display for this playlist.
     * Uses first track's artwork if no custom cover is set.
     */
    val displayArtworkUrl: String?
        get() = coverArtUrl ?: tracks.firstOrNull()?.artworkUrl

    /**
     * Shortened description for preview display.
     */
    val shortDescription: String?
        get() = description?.let {
            if (it.length > 60) it.take(60) + "..." else it
        }

    /**
     * Formatted creation date text.
     */
    val createdDateText: String
        get() = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(createdAt))

    companion object {
        /**
         * Creates a new empty playlist with the given name.
         */
        fun create(name: String, description: String? = null): Playlist {
            return Playlist(
                name = name,
                description = description
            )
        }

        /**
         * Empty playlist for initial states.
         */
        val EMPTY = Playlist(
            id = "",
            name = ""
        )
    }
}
