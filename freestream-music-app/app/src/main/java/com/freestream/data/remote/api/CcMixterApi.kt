package com.freestream.data.remote.api

import com.freestream.data.remote.dto.ccmixter.CcMixterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for ccMixter music service.
 * 
 * ccMixter is a community music site featuring remixes, samples, and acapellas.
 * All content is Creative Commons licensed. No authentication required.
 * 
 * Base URL: http://ccmixter.org/api/
 * Documentation: http://ccmixter.org/api
 * 
 * Note: ccMixter uses HTTP (not HTTPS), so cleartext traffic must be enabled
 * in the Android manifest.
 */
interface CcMixterApi {

    /**
     * Query uploads by tags.
     * 
     * This is the primary search method for ccMixter.
     * 
     * @param tags Comma-separated tags to search for
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @param sort Sort order ("rank", "date", "downloads", etc.)
     * @param license License filter ("open" for CC-BY only)
     * @param format Response format (always "json")
     * @return Array of uploads matching the tags
     */
    @GET("query")
    suspend fun queryByTags(
        @Query("tags") tags: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String = "rank",
        @Query("lic") license: String = "open",
        @Query("f") format: String = "json"
    ): Response<CcMixterResponse>

    /**
     * Search for remixes.
     * 
     * @param query Search query
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @param sort Sort order
     * @return Array of remix uploads
     */
    @GET("query")
    suspend fun searchRemixes(
        @Query("remix") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String = "rank",
        @Query("lic") license: String = "open",
        @Query("f") format: String = "json"
    ): Response<CcMixterResponse>

    /**
     * Search for samples.
     * 
     * @param query Search query
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @param sort Sort order
     * @return Array of sample uploads
     */
    @GET("query")
    suspend fun searchSamples(
        @Query("sample") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String = "rank",
        @Query("lic") license: String = "open",
        @Query("f") format: String = "json"
    ): Response<CcMixterResponse>

    /**
     * Search for acapellas.
     * 
     * @param query Search query
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @param sort Sort order
     * @return Array of acapella uploads
     */
    @GET("query")
    suspend fun searchAcapellas(
        @Query("acapella") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String = "rank",
        @Query("lic") license: String = "open",
        @Query("f") format: String = "json"
    ): Response<CcMixterResponse>

    /**
     * Get uploads by a specific user.
     * 
     * @param user User name
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @return Array of user's uploads
     */
    @GET("query")
    suspend fun getUserUploads(
        @Query("user") user: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String = "date",
        @Query("lic") license: String = "open",
        @Query("f") format: String = "json"
    ): Response<CcMixterResponse>

    /**
     * Get trending/popular uploads.
     * Uses "rank" sort for most popular.
     * 
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @return Array of popular uploads
     */
    @GET("query")
    suspend fun getTrendingUploads(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String = "rank",
        @Query("lic") license: String = "open",
        @Query("f") format: String = "json"
    ): Response<CcMixterResponse>

    /**
     * Get recent uploads.
     * Uses "date" sort for newest first.
     * 
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @return Array of recent uploads
     */
    @GET("query")
    suspend fun getRecentUploads(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String = "date",
        @Query("lic") license: String = "open",
        @Query("f") format: String = "json"
    ): Response<CcMixterResponse>

    /**
     * General search across all content types.
     * 
     * @param query Search query (searches in title, tags, description)
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @param sort Sort order
     * @return Array of matching uploads
     */
    @GET("query")
    suspend fun search(
        @Query("search") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String = "rank",
        @Query("lic") license: String = "open",
        @Query("f") format: String = "json"
    ): Response<CcMixterResponse>

    companion object {
        /**
         * Base URL for ccMixter API
         * Note: Uses HTTP, not HTTPS
         */
        const val BASE_URL = "http://ccmixter.org/api/"
    }
}
