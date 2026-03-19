package com.freestream.data.model

import android.os.Parcelable
import com.freestream.util.TimeUtils
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Unified Track data class that represents a music track from any of the 4 sources.
 * 
 * This class normalizes data from Jamendo, Freesound, Internet Archive, and ccMixter
 * into a single consistent format used throughout the app.
 * 
 * @param id Composite unique identifier: "{source}_{originalId}" (e.g., "jamendo_12345")
 * @param title Track title/name
 * @param artist Artist/creator name
 * @param album Album name (nullable - not all sources provide this)
 * @param durationMs Track duration in milliseconds for ExoPlayer
 * @param artworkUrl URL for album/track artwork (nullable - some sources don't provide images)
 * @param streamUrl Direct URL to audio stream (MP3 or other format)
 * @param source Which of the 4 sources this track came from
 * @param licenseType License type: "CC-BY", "CC0", "Public Domain", "CC-BY-NC", etc.
 * @param externalUrl Link to original page for attribution
 * @param tags Genre/keyword tags for recommendations and filtering
 * @param addedAt Timestamp when track was added to local database (for sorting)
 */
@Parcelize
@Serializable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val durationMs: Long,
    val artworkUrl: String? = null,
    val streamUrl: String,
    val source: SourceType,
    val licenseType: String,
    val externalUrl: String,
    val tags: List<String> = emptyList(),
    val addedAt: Long = System.currentTimeMillis()
) : Parcelable {

    /**
     * Human-readable duration string formatted as "mm:ss" or "h:mm:ss".
     * Used for display in track lists and player.
     */
    val durationText: String
        get() = TimeUtils.formatDuration(durationMs)

    /**
     * Shortened title for display in compact spaces.
     * Truncates to 30 characters with ellipsis if longer.
     */
    val shortTitle: String
        get() = if (title.length > 30) title.take(30) + "..." else title

    /**
     * Shortened artist name for display in compact spaces.
     * Truncates to 25 characters with ellipsis if longer.
     */
    val shortArtist: String
        get() = if (artist.length > 25) artist.take(25) + "..." else artist

    /**
     * Primary genre tag (first tag in the list) for categorization.
     */
    val primaryGenre: String?
        get() = tags.firstOrNull()

    /**
     * Whether this track has artwork available.
     */
    val hasArtwork: Boolean
        get() = !artworkUrl.isNullOrBlank()

    /**
     * Whether this track has album information.
     */
    val hasAlbum: Boolean
        get() = !album.isNullOrBlank()

    /**
     * Source-specific ID (extracted from composite ID).
     * E.g., "12345" from "jamendo_12345"
     */
    val sourceId: String
        get() = id.substringAfter("_", id)

    /**
     * Formatted attribution text for displaying license information.
     * Includes artist name and license type.
     */
    val attributionText: String
        get() = "$title by $artist • $licenseType"

    companion object {
        /**
         * Creates a composite ID from source type and original ID.
         * Ensures uniqueness across all sources.
         */
        fun createCompositeId(source: SourceType, originalId: String): String {
            return "${source.name.lowercase()}_$originalId"
        }

        /**
         * Empty track for initial states and placeholders.
         */
        val EMPTY = Track(
            id = "",
            title = "",
            artist = "",
            durationMs = 0,
            streamUrl = "",
            source = SourceType.JAMENDO,
            licenseType = "",
            externalUrl = ""
        )
    }
}
