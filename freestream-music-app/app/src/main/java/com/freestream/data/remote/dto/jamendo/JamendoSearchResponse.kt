package com.freestream.data.remote.dto.jamendo

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Jamendo API search response.
 * 
 * Jamendo wraps results in a response object with headers and results array.
 * This DTO handles both successful responses and errors.
 * 
 * Example successful response:
 * {
 *   "headers": {
 *     "status": "success",
 *     "code": 0,
 *     "error_message": "",
 *     "warnings": "",
 *     "results_count": 20
 *   },
 *   "results": [ ...tracks... ]
 * }
 */
data class JamendoSearchResponse(
    
    /**
     * Response headers containing status and metadata.
     */
    @SerializedName("headers")
    val headers: JamendoHeadersDto,
    
    /**
     * Array of track results.
     */
    @SerializedName("results")
    val results: List<JamendoTrackDto>?
) {
    /**
     * Whether the request was successful.
     */
    val isSuccess: Boolean
        get() = headers.status == "success" && headers.code == 0
    
    /**
     * Number of results returned.
     */
    val resultCount: Int
        get() = headers.resultsCount ?: results?.size ?: 0
    
    /**
     * Error message if request failed.
     */
    val errorMessage: String?
        get() = if (!isSuccess) headers.errorMessage else null
    
    /**
     * Gets the non-null results list.
     */
    fun getResultsOrEmpty(): List<JamendoTrackDto> = results ?: emptyList()
}

/**
 * Response headers from Jamendo API.
 */
data class JamendoHeadersDto(
    @SerializedName("status")
    val status: String,
    
    @SerializedName("code")
    val code: Int,
    
    @SerializedName("error_message")
    val errorMessage: String?,
    
    @SerializedName("warnings")
    val warnings: String?,
    
    @SerializedName("results_count")
    val resultsCount: Int?
)

/**
 * Artist response from Jamendo (for artist endpoints).
 */
data class JamendoArtistResponse(
    @SerializedName("headers")
    val headers: JamendoHeadersDto,
    
    @SerializedName("results")
    val results: List<JamendoArtistDto>?
)

/**
 * Artist DTO from Jamendo.
 */
data class JamendoArtistDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("website")
    val website: String?,
    
    @SerializedName("joindate")
    val joinDate: String?,
    
    @SerializedName("image")
    val image: String?,
    
    @SerializedName("tracks")
    val tracks: Int?,
    
    @SerializedName("albums")
    val albums: Int?,
    
    @SerializedName("shareurl")
    val shareUrl: String?
)
