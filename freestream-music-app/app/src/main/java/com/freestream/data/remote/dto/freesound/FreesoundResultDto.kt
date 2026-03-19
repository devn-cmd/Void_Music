package com.freestream.data.remote.dto.freesound

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for a sound from Freesound API.
 * 
 * Freesound focuses on sound effects and samples rather than full songs.
 * Previews are limited to 120 seconds. This DTO maps Freesound's API response.
 * 
 * API Documentation: https://freesound.org/docs/api/
 */
data class FreesoundResultDto(
    
    /**
     * Unique sound ID from Freesound.
     */
    @SerializedName("id")
    val id: Int,
    
    /**
     * Sound name/title.
     */
    @SerializedName("name")
    val name: String,
    
    /**
     * Sound description.
     */
    @SerializedName("description")
    val description: String?,
    
    /**
     * Duration in seconds (convert to ms for app).
     */
    @SerializedName("duration")
    val duration: Double,
    
    /**
     * Username of the uploader.
     */
    @SerializedName("username")
    val username: String,
    
    /**
     * Preview URLs (HQ MP3 is what we use for streaming).
     */
    @SerializedName("previews")
    val previews: FreesoundPreviewsDto?,
    
    /**
     * Images including waveform and spectral display.
     */
    @SerializedName("images")
    val images: FreesoundImagesDto?,
    
    /**
     * License type.
     */
    @SerializedName("license")
    val license: String,
    
    /**
     * Tags/categories for the sound.
     */
    @SerializedName("tags")
    val tags: List<String>?,
    
    /**
     * URL to the sound page on Freesound.
     */
    @SerializedName("url")
    val url: String,
    
    /**
     * Download count (popularity indicator).
     */
    @SerializedName("num_downloads")
    val downloadCount: Int?,
    
    /**
     * Average rating.
     */
    @SerializedName("avg_rating")
    val averageRating: Double?,
    
    /**
     * File size in bytes.
     */
    @SerializedName("filesize")
    val fileSize: Long?,
    
    /**
     * Bitrate.
     */
    @SerializedName("bitrate")
    val bitrate: Int?,
    
    /**
     * Sample rate.
     */
    @SerializedName("samplerate")
    val sampleRate: Double?,
    
    /**
     * Number of channels (1=mono, 2=stereo).
     */
    @SerializedName("channels")
    val channels: Int?,
    
    /**
     * Date created.
     */
    @SerializedName("created")
    val created: String?
) {
    /**
     * Converts duration from seconds to milliseconds.
     */
    val durationMs: Long
        get() = (duration * 1000).toLong()
    
    /**
     * Best preview URL for streaming (HQ MP3).
     */
    val previewUrl: String?
        get() = previews?.previewHqMp3 ?: previews?.previewLqMp3
    
    /**
     * Artwork/image URL for the sound.
     */
    val artworkUrl: String?
        get() = images?.waveformM ?: images?.waveformL
    
    /**
     * Formatted license type for display.
     */
    val formattedLicense: String
        get() = when {
            license.contains("cc0", ignoreCase = true) -> "CC0"
            license.contains("by-nc", ignoreCase = true) -> "CC-BY-NC"
            license.contains("by", ignoreCase = true) -> "CC-BY"
            license.contains("sampling+", ignoreCase = true) -> "Sampling+"
            else -> "Custom"
        }
    
    /**
     * Whether this is a high-quality sound.
     */
    val isHighQuality: Boolean
        get() = (bitrate ?: 0) >= 192000 || (sampleRate ?: 0) >= 44100
}

/**
 * Preview URLs from Freesound.
 */
data class FreesoundPreviewsDto(
    @SerializedName("preview-hq-mp3")
    val previewHqMp3: String?,
    
    @SerializedName("preview-lq-mp3")
    val previewLqMp3: String?,
    
    @SerializedName("preview-hq-ogg")
    val previewHqOgg: String?,
    
    @SerializedName("preview-lq-ogg")
    val previewLqOgg: String?
)

/**
 * Image URLs from Freesound.
 */
data class FreesoundImagesDto(
    @SerializedName("waveform_l")
    val waveformL: String?,
    
    @SerializedName("waveform_m")
    val waveformM: String?,
    
    @SerializedName("spectral_l")
    val spectralL: String?,
    
    @SerializedName("spectral_m")
    val spectralM: String?
)
