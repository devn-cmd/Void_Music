package com.freestream.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Data class representing an artist/creator across all music sources.
 * 
 * Normalizes artist information from Jamendo, Freesound, Archive.org, and ccMixter.
 * 
 * @param id Unique artist identifier
 * @param name Artist display name
 * @param bio Artist biography/description (nullable)
 * @param avatarUrl URL to artist image/avatar (nullable)
 * @param websiteUrl Artist's personal website (nullable)
 * @param source Which music source this artist is from
 * @param externalUrl Link to artist's page on the source platform
 * @param trackCount Number of tracks by this artist (if known)
 * @param followersCount Number of followers (if available)
 */
@Parcelize
@Serializable
data class Artist(
    val id: String,
    val name: String,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val websiteUrl: String? = null,
    val source: SourceType,
    val externalUrl: String,
    val trackCount: Int? = null,
    val followersCount: Int? = null
) : Parcelable {

    /**
     * Shortened bio for preview display.
     * Truncates to 100 characters.
     */
    val shortBio: String?
        get() = bio?.let {
            if (it.length > 100) it.take(100) + "..." else it
        }

    /**
     * Whether this artist has an avatar image.
     */
    val hasAvatar: Boolean
        get() = !avatarUrl.isNullOrBlank()

    /**
     * Display text for track count.
     */
    val trackCountText: String
        get() = trackCount?.let { "$it tracks" } ?: ""

    /**
     * Display text for followers count (formatted).
     */
    val followersText: String
        get() = followersCount?.let { count ->
            when {
                count >= 1_000_000 -> "${count / 1_000_000}M followers"
                count >= 1_000 -> "${count / 1_000}K followers"
                else -> "$count followers"
            }
        } ?: ""

    companion object {
        /**
         * Empty artist for initial states.
         */
        val EMPTY = Artist(
            id = "",
            name = "",
            source = SourceType.JAMENDO,
            externalUrl = ""
        )
    }
}
