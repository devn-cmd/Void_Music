package com.freestream.data.remote.dto.freesound

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Freesound API search response.
 * 
 * Freesound uses a paginated response format with count and results array.
 * Supports pagination with next/previous URLs.
 * 
 * Example response:
 * {
 *   "count": 150,
 *   "next": "https://freesound.org/apiv2/search/text/?...&page=2",
 *   "previous": null,
 *   "results": [ ...sounds... ]
 * }
 */
data class FreesoundSearchResponse(
    
    /**
     * Total number of results matching the query.
     */
    @SerializedName("count")
    val count: Int,
    
    /**
     * URL for the next page of results (null if no more pages).
     */
    @SerializedName("next")
    val next: String?,
    
    /**
     * URL for the previous page of results (null if first page).
     */
    @SerializedName("previous")
    val previous: String?,
    
    /**
     * Array of sound results for this page.
     */
    @SerializedName("results")
    val results: List<FreesoundResultDto>?
) {
    /**
     * Whether there are more results available.
     */
    val hasMore: Boolean
        get() = !next.isNullOrBlank()
    
    /**
     * Current page number (estimated from results size).
     */
    val currentPage: Int
        get() {
            val pageSize = results?.size?.coerceAtLeast(1) ?: 15
            return ((count - (results?.size ?: 0)) / pageSize) + 1
        }
    
    /**
     * Total number of pages (estimated).
     */
    val totalPages: Int
        get() {
            val pageSize = results?.size?.coerceAtLeast(1) ?: 15
            return (count + pageSize - 1) / pageSize
        }
    
    /**
     * Gets the non-null results list.
     */
    fun getResultsOrEmpty(): List<FreesoundResultDto> = results ?: emptyList()
    
    /**
     * Whether the response contains any results.
     */
    val hasResults: Boolean
        get() = !results.isNullOrEmpty()
}

/**
 * Error response from Freesound API.
 */
data class FreesoundErrorResponse(
    @SerializedName("detail")
    val detail: String?,
    
    @SerializedName("status_code")
    val statusCode: Int?
) {
    /**
     * Human-readable error message.
     */
    val errorMessage: String
        get() = detail ?: "Unknown error occurred"
}
