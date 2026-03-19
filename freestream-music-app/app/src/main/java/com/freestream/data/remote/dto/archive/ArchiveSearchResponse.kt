package com.freestream.data.remote.dto.archive

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Internet Archive advanced search response.
 * 
 * Archive.org uses a response wrapper with response body containing docs.
 * 
 * Example response:
 * {
 *   "responseHeader": { ... },
 *   "response": {
 *     "numFound": 1000,
 *     "start": 0,
 *     "docs": [ ...items... ]
 *   }
 * }
 */
data class ArchiveSearchResponse(
    
    /**
     * Response header containing status and timing info.
     */
    @SerializedName("responseHeader")
    val responseHeader: ArchiveResponseHeaderDto?,
    
    /**
     * Response body containing search results.
     */
    @SerializedName("response")
    val response: ArchiveResponseBodyDto?
) {
    /**
     * Whether the search was successful.
     */
    val isSuccess: Boolean
        get() = responseHeader?.status == 0
    
    /**
     * Total number of matching documents.
     */
    val totalFound: Int
        get() = response?.numFound ?: 0
    
    /**
     * Starting index of this result set.
     */
    val startIndex: Int
        get() = response?.start ?: 0
    
    /**
     * Search results (documents).
     */
    val documents: List<ArchiveDocDto>
        get() = response?.docs ?: emptyList()
    
    /**
     * Whether there are more results.
     */
    val hasMore: Boolean
        get() = (startIndex + documents.size) < totalFound
}

/**
 * Response header from Archive.org.
 */
data class ArchiveResponseHeaderDto(
    @SerializedName("status")
    val status: Int?,
    
    @SerializedName("QTime")
    val queryTime: Int?,
    
    @SerializedName("params")
    val params: Map<String, String>?
)

/**
 * Response body containing search results.
 */
data class ArchiveResponseBodyDto(
    @SerializedName("numFound")
    val numFound: Int?,
    
    @SerializedName("start")
    val start: Int?,
    
    @SerializedName("docs")
    val docs: List<ArchiveDocDto>?
)
