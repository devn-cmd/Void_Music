package com.freestream.data.remote.api

import com.freestream.data.remote.dto.freesound.FreesoundResultDto
import com.fresound.data.remote.dto.freesound.FreesoundSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for Freesound audio service.
 * 
 * Freesound provides sound effects, samples, and audio snippets.
 * Tracks are limited to 120-second previews on the free tier.
 * 
 * Base URL: https://freesound.org/apiv2/
 * Documentation: https://freesound.org/docs/api/
 * 
 * Authentication: API Key (free registration at https://freesound.org/apiv2/apply/)
 */
interface FreesoundApi {

    /**
     * Search for sounds on Freesound.
     * 
     * @param query Search query string
     * @param token Your Freesound API Key
     * @param page Page number (1-based)
     * @param pageSize Number of results per page
     * @param fields Comma-separated list of fields to return
     * @param sort Sort order ("score", "downloads_desc", "rating_desc", etc.)
     * @param filter Additional filters (license, duration, etc.)
     * @return Search response containing sounds
     */
    @GET("search/text/")
    suspend fun searchSounds(
        @Query("query") query: String,
        @Query("token") token: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String = DEFAULT_FIELDS,
        @Query("sort") sort: String = "score",
        @Query("filter") filter: String? = null
    ): Response<FreesoundSearchResponse>

    /**
     * Get a specific sound by ID.
     * 
     * @param soundId Sound ID
     * @param token Your Freesound API Key
     * @param fields Fields to return
     * @return Sound details
     */
    @GET("sounds/{sound_id}/")
    suspend fun getSound(
        @Path("sound_id") soundId: Int,
        @Query("token") token: String,
        @Query("fields") fields: String = DEFAULT_FIELDS
    ): Response<FreesoundResultDto>

    /**
     * Get similar sounds to a given sound.
     * 
     * @param soundId Sound ID to find similar sounds for
     * @param token Your Freesound API Key
     * @param page Page number
     * @param pageSize Results per page
     * @return Search response containing similar sounds
     */
    @GET("sounds/{sound_id}/similar/")
    suspend fun getSimilarSounds(
        @Path("sound_id") soundId: Int,
        @Query("token") token: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String = DEFAULT_FIELDS
    ): Response<FreesoundSearchResponse>

    /**
     * Get trending/popular sounds.
     * Uses downloads_desc sort to get most downloaded sounds.
     * 
     * @param token Your Freesound API Key
     * @param page Page number
     * @param pageSize Results per page
     * @return Search response containing popular sounds
     */
    @GET("search/text/")
    suspend fun getTrendingSounds(
        @Query("token") token: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String = DEFAULT_FIELDS,
        @Query("sort") sort: String = "downloads_desc",
        @Query("filter") filter: String = "duration:[0.0 TO 120.0]" // Max 2 minutes for previews
    ): Response<FreesoundSearchResponse>

    /**
     * Get sounds by a specific user.
     * 
     * @param username Username
     * @param token Your Freesound API Key
     * @param page Page number
     * @param pageSize Results per page
     * @return Search response containing user's sounds
     */
    @GET("users/{username}/sounds/")
    suspend fun getUserSounds(
        @Path("username") username: String,
        @Query("token") token: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String = DEFAULT_FIELDS
    ): Response<FreesoundSearchResponse>

    companion object {
        /**
         * Base URL for Freesound API v2
         */
        const val BASE_URL = "https://freesound.org/apiv2/"

        /**
         * Default fields to request for efficiency.
         * Only request fields we actually use.
         */
        const val DEFAULT_FIELDS = "id,name,username,duration,previews,images," +
                "license,tags,url,num_downloads,avg_rating,description,created"
    }
}
