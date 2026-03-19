package com.freestream.data.remote.dto.ccmixter

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for ccMixter API response.
 * 
 * ccMixter returns a simple array of uploads.
 * 
 * Example response:
 * [
 *   { ...upload1... },
 *   { ...upload2... }
 * ]
 */
typealias CcMixterResponse = List<CcMixterUploadDto>

/**
 * Wrapper for ccMixter response with additional metadata.
 * Some endpoints may return wrapped responses.
 */
data class CcMixterWrappedResponse(
    
    /**
     * Array of uploads.
     */
    @SerializedName("uploads")
    val uploads: List<CcMixterUploadDto>?,
    
    /**
     * Total count if paginated.
     */
    @SerializedName("total")
    val total: Int?,
    
    /**
     * Offset for pagination.
     */
    @SerializedName("offset")
    val offset: Int?,
    
    /**
     * Limit for pagination.
     */
    @SerializedName("limit")
    val limit: Int?
) {
    /**
     * Gets non-null uploads list.
     */
    fun getUploadsOrEmpty(): List<CcMixterUploadDto> = uploads ?: emptyList()
    
    /**
     * Whether there are more results.
     */
    val hasMore: Boolean
        get() = ((offset ?: 0) + (uploads?.size ?: 0)) < (total ?: 0)
}

/**
 * Error response from ccMixter API.
 */
data class CcMixterErrorResponse(
    @SerializedName("error")
    val error: String?,
    
    @SerializedName("message")
    val message: String?
) {
    /**
     * Human-readable error message.
     */
    val errorMessage: String
        get() = message ?: error ?: "Unknown error from ccMixter"
}
