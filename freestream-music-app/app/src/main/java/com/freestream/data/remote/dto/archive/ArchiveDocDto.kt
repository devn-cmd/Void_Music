package com.freestream.data.remote.dto.archive

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for a document from Internet Archive search.
 * 
 * Internet Archive search returns basic metadata. Full audio details
 * require a second API call to the metadata endpoint.
 * 
 * API Documentation: https://archive.org/help/json.php
 */
data class ArchiveDocDto(
    
    /**
     * Unique identifier for the archive item.
     * Used to fetch full metadata.
     */
    @SerializedName("identifier")
    val identifier: String,
    
    /**
     * Title of the item.
     */
    @SerializedName("title")
    val title: String?,
    
    /**
     * Creator/artist name.
     */
    @SerializedName("creator")
    val creator: String?,
    
    /**
     * Publication date.
     */
    @SerializedName("date")
    val date: String?,
    
    /**
     * Year of publication.
     */
    @SerializedName("year")
    val year: String?,
    
    /**
     * Description of the item.
     */
    @SerializedName("description")
    val description: String?,
    
    /**
     * Genre/category.
     */
    @SerializedName("genre")
    val genre: String?,
    
    /**
     * Number of downloads (popularity indicator).
     */
    @SerializedName("downloads")
    val downloads: Int?,
    
    /**
     * Subject tags.
     */
    @SerializedName("subject")
    val subject: List<String>?,
    
    /**
     * Collection this item belongs to.
     */
    @SerializedName("collection")
    val collection: List<String>?,
    
    /**
     * Media type (should be "audio" for our searches).
     */
    @SerializedName("mediatype")
    val mediaType: String?,
    
    /**
     * Public date when item was added.
     */
    @SerializedName("publicdate")
    val publicDate: String?
) {
    /**
     * Display title (falls back to identifier if no title).
     */
    val displayTitle: String
        get() = title ?: identifier
    
    /**
     * Display creator (falls back to "Unknown Artist").
     */
    val displayCreator: String
        get() = creator ?: "Unknown Artist"
    
    /**
     * Tags derived from subject and genre.
     */
    val tags: List<String>
        get() {
            val allTags = mutableListOf<String>()
            subject?.let { allTags.addAll(it) }
            genre?.let { allTags.add(it) }
            return allTags.distinct()
        }
    
    /**
     * Formatted download count.
     */
    val downloadCountText: String
        get() = downloads?.let {
            when {
                it >= 1000000 -> "${it / 1000000}M downloads"
                it >= 1000 -> "${it / 1000}K downloads"
                else -> "$it downloads"
            }
        } ?: ""
    
    /**
     * URL to the item page on Archive.org.
     */
    val itemUrl: String
        get() = "https://archive.org/details/$identifier"
}
