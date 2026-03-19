package com.freestream.data.remote.dto.ccmixter

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for an upload from ccMixter API.
 * 
 * ccMixter focuses on remix culture with acapellas, samples, and remixes.
 * Each upload can have multiple files (different formats).
 * 
 * API Documentation: http://ccmixter.org/api
 */
data class CcMixterUploadDto(
    
    /**
     * Unique upload ID.
     */
    @SerializedName("upload_id")
    val uploadId: String,
    
    /**
     * Upload name/title.
     */
    @SerializedName("upload_name")
    val uploadName: String,
    
    /**
     * Upload description.
     */
    @SerializedName("upload_description_plain")
    val uploadDescription: String?,
    
    /**
     * User who uploaded this.
     */
    @SerializedName("user_name")
    val userName: String,
    
    /**
     * User's real name (if provided).
     */
    @SerializedName("user_real_name")
    val userRealName: String?,
    
    /**
     * Array of files for this upload (different formats).
     */
    @SerializedName("files")
    val files: List<CcMixterFileDto>?,
    
    /**
     * License information.
     */
    @SerializedName("license_name")
    val licenseName: String?,
    
    @SerializedName("license_url")
    val licenseUrl: String?,
    
    /**
     * Tags for this upload.
     */
    @SerializedName("upload_tags")
    val uploadTags: String?,
    
    /**
     * Date uploaded.
     */
    @SerializedName("upload_date_format")
    val uploadDate: String?,
    
    /**
     * Number of remixes of this upload.
     */
    @SerializedName("upload_num_remixes")
    val remixCount: Int?,
    
    /**
     * Number of playlists this is in.
     */
    @SerializedName("upload_num_playlists")
    val playlistCount: Int?,
    
    /**
     * URL to the upload page.
     */
    @SerializedName("file_page_url")
    val filePageUrl: String?,
    
    /**
     * BPM if it's a sample/loop.
     */
    @SerializedName("upload_extra")
    val uploadExtra: CcMixterExtraDto?
) {
    /**
     * Best file for streaming (MP3 preferred).
     */
    val bestFile: CcMixterFileDto?
        get() = files?.find { it.isMp3 } ?: files?.firstOrNull()
    
    /**
     * Stream URL for the best file.
     */
    val streamUrl: String?
        get() = bestFile?.downloadUrl
    
    /**
     * Duration in milliseconds.
     */
    val durationMs: Long
        get() = bestFile?.durationMs ?: 0
    
    /**
     * Artwork URL (ccMixter doesn't provide artwork, use placeholder).
     */
    val artworkUrl: String? = null
    
    /**
     * Tags as a list.
     */
    val tags: List<String>
        get() = uploadTags?.split(",")?.map { it.trim() } ?: emptyList()
    
    /**
     * Display artist name.
     */
    val artistName: String
        get() = userRealName ?: userName
    
    /**
     * Formatted license type.
     */
    val formattedLicense: String
        get() = when {
            licenseName?.contains("by-nc", ignoreCase = true) == true -> "CC-BY-NC"
            licenseName?.contains("by", ignoreCase = true) == true -> "CC-BY"
            licenseName?.contains("zero", ignoreCase = true) == true -> "CC0"
            else -> licenseName ?: "CC-BY"
        }
    
    /**
     * Whether this is a sample/loop.
     */
    val isSample: Boolean
        get() = uploadExtra?.bpm != null
}

/**
 * File information from ccMixter.
 */
data class CcMixterFileDto(
    @SerializedName("download_url")
    val downloadUrl: String,
    
    @SerializedName("file_page_url")
    val filePageUrl: String?,
    
    @SerializedName("file_format_info")
    val fileFormatInfo: String?,
    
    @SerializedName("file_name")
    val fileName: String?,
    
    @SerializedName("file_rawsize")
    val fileSize: Long?,
    
    @SerializedName("file_duration")
    val fileDuration: String?
) {
    /**
     * Whether this is an MP3 file.
     */
    val isMp3: Boolean
        get() = fileName?.endsWith(".mp3", ignoreCase = true) == true ||
                downloadUrl.endsWith(".mp3", ignoreCase = true)
    
    /**
     * Duration in seconds.
     */
    val durationSeconds: Int
        get() = fileDuration?.split(":")?.let { parts ->
            when (parts.size) {
                2 -> parts[0].toIntOrNull()?.times(60)?.plus(parts[1].toIntOrNull() ?: 0) ?: 0
                3 -> parts[0].toIntOrNull()?.times(3600)?.plus(
                    parts[1].toIntOrNull()?.times(60) ?: 0
                )?.plus(parts[2].toIntOrNull() ?: 0) ?: 0
                else -> 0
            }
        } ?: 0
    
    /**
     * Duration in milliseconds.
     */
    val durationMs: Long
        get() = durationSeconds * 1000L
}

/**
 * Extra metadata from ccMixter (BPM, key, etc.).
 */
data class CcMixterExtraDto(
    @SerializedName("bpm")
    val bpm: Int?,
    
    @SerializedName("key")
    val key: String?,
    
    @SerializedName("length")
    val length: String?
)
