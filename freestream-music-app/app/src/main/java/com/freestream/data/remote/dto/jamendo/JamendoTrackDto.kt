package com.freestream.data.remote.dto.jamendo

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for a track from Jamendo API.
 * 
 * Jamendo provides comprehensive track metadata including direct MP3 URLs
 * and album artwork. This DTO maps the JSON response from Jamendo's API.
 * 
 * API Documentation: https://developer.jamendo.com/v3.0 
 */
data class JamendoTrackDto(
    
    /**
     * Unique track ID from Jamendo.
     */
    @SerializedName("id")
    val id: String,
    
    /**
     * Track title/name.
     */
    @SerializedName("name")
    val name: String,
    
    /**
     * Track duration in seconds (Jamendo provides seconds, we convert to ms).
     */
    @SerializedName("duration")
    val duration: Int,
    
    /**
     * Direct URL to the audio file (MP3 format).
     * This is the stream URL used by ExoPlayer.
     */
    @SerializedName("audio")
    val audioUrl: String,
    
    /**
     * URL to the album cover image (various sizes available).
     */
    @SerializedName("album_image")
    val albumImage: String?,
    
    /**
     * Album name this track belongs to.
     */
    @SerializedName("album_name")
    val albumName: String?,
    
    /**
     * Artist name.
     */
    @SerializedName("artist_name")
    val artistName: String,
    
    /**
     * Artist ID for linking to artist page.
     */
    @SerializedName("artist_id")
    val artistId: String?,
    
    /**
     * URL to share this track.
     */
    @SerializedName("shareurl")
    val shareUrl: String,
    
    /**
     * URL to the license information.
     */
    @SerializedName("license_ccurl")
    val licenseUrl: String?,
    
    /**
     * Short license name (e.g., "cc-by", "cczero").
     */
    @SerializedName("license_cc")
    val licenseShortName: String?,
    
    /**
     * Full license name.
     */
    @SerializedName("license_name")
    val licenseName: String?,
    
    /**
     * Music info containing tags/genres.
     */
    @SerializedName("musicinfo")
    val musicInfo: JamendoMusicInfoDto?,
    
    /**
     * Track position in album.
     */
    @SerializedName("position")
    val position: Int?,
    
    /**
     * Release date of the track.
     */
    @SerializedName("releasedate")
    val releaseDate: String?,
    
    /**
     * Audio download URL (if available).
     */
    @SerializedName("audiodownload")
    val audioDownloadUrl: String?,
    
    /**
     * Whether the track is explicit.
     */
    @SerializedName("explicit")
    val isExplicit: String? = "false"
) {
    /**
     * Converts duration from seconds to milliseconds.
     */
    val durationMs: Long
        get() = duration * 1000L
    
    /**
     * Gets the genre tags from music info.
     */
    val tags: List<String>
        get() = musicInfo?.tags?.vocals?.plus(musicInfo.tags.instruments ?: emptyList()) 
            ?: emptyList()
    
    /**
     * Formatted license type for display.
     */
    val formattedLicense: String
        get() = when (licenseShortName?.lowercase()) {
            "cczero", "cc0" -> "CC0"
            "cc-by" -> "CC-BY"
            "cc-by-nc" -> "CC-BY-NC"
            "cc-by-sa" -> "CC-BY-SA"
            "cc-by-nd" -> "CC-BY-ND"
            else -> licenseName ?: "Creative Commons"
        }
    
    /**
     * Whether the track is marked as explicit.
     */
    val explicit: Boolean
        get() = isExplicit?.lowercase() == "true"
}

/**
 * Music info containing tags and genres from Jamendo.
 */
data class JamendoMusicInfoDto(
    @SerializedName("vocalinstrumental")
    val vocalInstrumental: String?,
    
    @SerializedName("lang")
    val language: String?,
    
    @SerializedName("gender")
    val gender: String?,
    
    @SerializedName("acousticelectric")
    val acousticElectric: String?,
    
    @SerializedName("speed")
    val speed: String?,
    
    @SerializedName("tags")
    val tags: JamendoTagsDto?
)

/**
 * Tags/genres from Jamendo.
 */
data class JamendoTagsDto(
    @SerializedName("vocals")
    val vocals: List<String>?,
    
    @SerializedName("instruments")
    val instruments: List<String>?,
    
    @SerializedName("genres")
    val genres: List<String>?
)
