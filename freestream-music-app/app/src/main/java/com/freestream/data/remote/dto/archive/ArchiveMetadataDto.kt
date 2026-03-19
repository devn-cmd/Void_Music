package com.freestream.data.remote.dto.archive

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Internet Archive metadata response.
 * 
 * After getting search results, we fetch full metadata to get audio file URLs.
 * This contains detailed file information including audio formats.
 * 
 * API Endpoint: https://archive.org/metadata/{identifier}
 */
data class ArchiveMetadataDto(
    
    /**
     * Item identifier.
     */
    @SerializedName("identifier")
    val identifier: String?,
    
    /**
     * Item metadata including title, creator, etc.
     */
    @SerializedName("metadata")
    val metadata: ArchiveItemMetadataDto?,
    
    /**
     * Array of files in this item.
     */
    @SerializedName("files")
    val files: List<ArchiveFileDto>?,
    
    /**
     * Server hosting the files.
     */
    @SerializedName("server")
    val server: String?,
    
    /**
     * Directory path on the server.
     */
    @SerializedName("dir")
    val dir: String?,
    
    /**
     * Item creation date.
     */
    @SerializedName("created")
    val created: Long?,
    
    /**
     * Item size in bytes.
     */
    @SerializedName("item_size")
    val itemSize: Long?
) {
    /**
     * Gets the best audio file for streaming.
     * Prefers VBR MP3, falls back to regular MP3.
     */
    val bestAudioFile: ArchiveFileDto?
        get() {
            val audioFiles = files?.filter { it.isAudio } ?: emptyList()
            return audioFiles.find { it.format?.contains("VBR MP3", ignoreCase = true) == true }
                ?: audioFiles.find { it.format?.contains("MP3", ignoreCase = true) == true }
                ?: audioFiles.firstOrNull()
        }
    
    /**
     * Direct URL to the best audio file for streaming.
     */
    val streamUrl: String?
        get() = bestAudioFile?.let { file ->
            "https://archive.org/download/$identifier/${file.name}"
        }
    
    /**
     * Duration in seconds (from best audio file).
     */
    val duration: Int?
        get() = bestAudioFile?.length?.toInt()
    
    /**
     * Duration in milliseconds.
     */
    val durationMs: Long
        get() = (duration ?: 0) * 1000L
    
    /**
     * Gets the title from metadata.
     */
    val title: String?
        get() = metadata?.title
    
    /**
     * Gets the creator from metadata.
     */
    val creator: String?
        get() = metadata?.creator?.firstOrNull()
    
    /**
     * Gets the description from metadata.
     */
    val description: String?
        get() = metadata?.description?.firstOrNull()
    
    /**
     * Gets the license URL from metadata.
     */
    val licenseUrl: String?
        get() = metadata?.licenseUrl
    
    /**
     * Gets the collection from metadata.
     */
    val collection: List<String>?
        get() = metadata?.collection
    
    /**
     * Gets the subject tags from metadata.
     */
    val subject: List<String>?
        get() = metadata?.subject
}

/**
 * Item metadata from Archive.org.
 */
data class ArchiveItemMetadataDto(
    @SerializedName("identifier")
    val identifier: String?,
    
    @SerializedName("title")
    val title: String?,
    
    @SerializedName("creator")
    val creator: List<String>?,
    
    @SerializedName("description")
    val description: List<String>?,
    
    @SerializedName("date")
    val date: String?,
    
    @SerializedName("year")
    val year: String?,
    
    @SerializedName("subject")
    val subject: List<String>?,
    
    @SerializedName("collection")
    val collection: List<String>?,
    
    @SerializedName("licenseurl")
    val licenseUrl: String?,
    
    @SerializedName("mediatype")
    val mediaType: String?,
    
    @SerializedName("publicdate")
    val publicDate: String?,
    
    @SerializedName("addeddate")
    val addedDate: String?
)

/**
 * File information from Archive.org metadata.
 */
data class ArchiveFileDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("source")
    val source: String?,
    
    @SerializedName("format")
    val format: String?,
    
    @SerializedName("size")
    val size: String?,
    
    @SerializedName("length")
    val length: String?,
    
    @SerializedName("track")
    val track: String?,
    
    @SerializedName("title")
    val title: String?,
    
    @SerializedName("creator")
    val creator: String?
) {
    /**
     * Whether this is an audio file.
     */
    val isAudio: Boolean
        get() = format?.contains("MP3", ignoreCase = true) == true ||
                format?.contains("OGG", ignoreCase = true) == true ||
                format?.contains("FLAC", ignoreCase = true) == true ||
                format?.contains("WAV", ignoreCase = true) == true ||
                name?.endsWith(".mp3", ignoreCase = true) == true
    
    /**
     * File size in bytes.
     */
    val sizeBytes: Long
        get() = size?.toLongOrNull() ?: 0
    
    /**
     * Duration in seconds.
     */
    val durationSeconds: Int
        get() = length?.toIntOrNull() ?: 0
    
    /**
     * Duration in milliseconds.
     */
    val durationMs: Long
        get() = durationSeconds * 1000L
}
