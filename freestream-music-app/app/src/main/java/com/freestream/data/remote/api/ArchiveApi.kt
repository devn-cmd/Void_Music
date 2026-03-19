package com.freestream.data.remote.api

import com.freestream.data.remote.dto.archive.ArchiveMetadataDto
import com.freestream.data.remote.dto.archive.ArchiveSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for Internet Archive audio collection.
 * 
 * Internet Archive provides free access to live concerts, vintage recordings,
 * and public domain audio. No authentication required.
 * 
 * Search API: https://archive.org/advancedsearch.php
 * Metadata API: https://archive.org/metadata/{identifier}
 * 
 * Note: This is a two-step API:
 * 1. Search to get item identifiers
 * 2. Fetch metadata for each item to get audio file URLs
 */
interface ArchiveApi {

    /**
     * Search for audio items on Internet Archive.
     * 
     * Uses the advanced search endpoint which returns JSON.
     * 
     * @param query Search query
     * @param mediaType Filter by media type (should be "audio")
     * @param fields Fields to return (identifier, title, creator, etc.)
     * @param sort Sort order ("downloads desc" for most popular)
     * @param rows Number of results to return
     * @param page Page number for pagination
     * @param output Output format (always "json")
     * @return Search response containing item identifiers and basic metadata
     */
    @GET("advancedsearch.php")
    suspend fun searchAudio(
        @Query("q") query: String,
        @Query("mediatype") mediaType: String = "audio",
        @Query("fl[]") fields: List<String> = DEFAULT_FIELDS,
        @Query("sort[]") sort: String = "downloads desc",
        @Query("rows") rows: Int = 20,
        @Query("page") page: Int = 1,
        @Query("output") output: String = "json"
    ): Response<ArchiveSearchResponse>

    /**
     * Get full metadata for an archive item.
     * 
     * This is the second step - use the identifier from search
     * to get detailed metadata including audio file URLs.
     * 
     * @param identifier Archive item identifier
     * @return Full metadata including files array
     */
    @GET("metadata/{identifier}")
    suspend fun getMetadata(
        @Path("identifier") identifier: String
    ): Response<ArchiveMetadataDto>

    /**
     * Search for live music concerts (etree collection).
     * 
     * @param query Search query
     * @param rows Number of results
     * @param page Page number
     * @return Search response containing concert items
     */
    @GET("advancedsearch.php")
    suspend fun searchConcerts(
        @Query("q") query: String,
        @Query("collection") collection: String = "etree",
        @Query("mediatype") mediaType: String = "audio",
        @Query("fl[]") fields: List<String> = DEFAULT_FIELDS,
        @Query("sort[]") sort: String = "downloads desc",
        @Query("rows") rows: Int = 20,
        @Query("page") page: Int = 1,
        @Query("output") output: String = "json"
    ): Response<ArchiveSearchResponse>

    /**
     * Search for 78rpm records (vintage recordings).
     * 
     * @param query Search query
     * @param rows Number of results
     * @param page Page number
     * @return Search response containing vintage recordings
     */
    @GET("advancedsearch.php")
    suspend fun search78rpm(
        @Query("q") query: String,
        @Query("collection") collection: String = "78rpm",
        @Query("mediatype") mediaType: String = "audio",
        @Query("fl[]") fields: List<String> = DEFAULT_FIELDS,
        @Query("sort[]") sort: String = "downloads desc",
        @Query("rows") rows: Int = 20,
        @Query("page") page: Int = 1,
        @Query("output") output: String = "json"
    ): Response<ArchiveSearchResponse>

    /**
     * Get trending/popular audio from Archive.
     * Sorts by download count.
     * 
     * @param rows Number of results
     * @param page Page number
     * @return Search response containing popular audio
     */
    @GET("advancedsearch.php")
    suspend fun getTrendingAudio(
        @Query("mediatype") mediaType: String = "audio",
        @Query("fl[]") fields: List<String> = DEFAULT_FIELDS,
        @Query("sort[]") sort: String = "downloads desc",
        @Query("rows") rows: Int = 20,
        @Query("page") page: Int = 1,
        @Query("output") output: String = "json"
    ): Response<ArchiveSearchResponse>

    companion object {
        /**
         * Base URL for Internet Archive APIs
         */
        const val BASE_URL = "https://archive.org/"

        /**
         * Default fields to request from search.
         * These provide enough info for display without the full metadata call.
         */
        val DEFAULT_FIELDS = listOf(
            "identifier",
            "title",
            "creator",
            "downloads",
            "year",
            "genre",
            "subject",
            "collection",
            "date",
            "description"
        )
    }
}
