package com.freestream.data.remote.api

import com.freestream.data.remote.dto.jamendo.JamendoArtistResponse
import com.freestream.data.remote.dto.jamendo.JamendoSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for Jamendo music service.
 * 
 * Jamendo provides 400,000+ Creative Commons licensed tracks from independent artists.
 * This is the primary music source for FreeStream.
 * 
 * Base URL: https://api.jamendo.com/v3.0/
 * Documentation: https://developer.jamendo.com/v3.0 
 * 
 * Authentication: Client ID (free registration at https://devportal.jamendo.com/)
 */
interface JamendoApi {

    /**
     * Search for tracks on Jamendo.
     * 
     * @param clientId Your Jamendo Client ID
     * @param query Search query string
     * @param limit Maximum number of results (default: 20)
     * @param offset Pagination offset
     * @param format Response format (always "json")
     * @param audioFormat Audio format preference ("mp32" for 32kbps MP3, good for streaming)
     * @param include Additional data to include ("musicinfo" for tags)
     * @return Search response containing tracks
     */
    @GET("tracks")
    suspend fun searchTracks(
        @Query("client_id") clientId: String,
        @Query("search") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("format") format: String = "json",
        @Query("audioformat") audioFormat: String = "mp32",
        @Query("include") include: String = "musicinfo"
    ): Response<JamendoSearchResponse>

    /**
     * Get trending/popular tracks from Jamendo.
     * 
     * @param clientId Your Jamendo Client ID
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @param order Sort order ("popularity_total" for most popular)
     * @param format Response format
     * @param audioFormat Audio format preference
     * @param include Additional data to include
     * @return Search response containing trending tracks
     */
    @GET("tracks")
    suspend fun getTrendingTracks(
        @Query("client_id") clientId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("order") order: String = "popularity_total",
        @Query("format") format: String = "json",
        @Query("audioformat") audioFormat: String = "mp32",
        @Query("include") include: String = "musicinfo"
    ): Response<JamendoSearchResponse>

    /**
     * Get recently added tracks from Jamendo.
     * 
     * @param clientId Your Jamendo Client ID
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @param order Sort order ("releasedate" for newest)
     * @return Search response containing recent tracks
     */
    @GET("tracks")
    suspend fun getRecentTracks(
        @Query("client_id") clientId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("order") order: String = "releasedate",
        @Query("format") format: String = "json",
        @Query("audioformat") audioFormat: String = "mp32",
        @Query("include") include: String = "musicinfo"
    ): Response<JamendoSearchResponse>

    /**
     * Get tracks by a specific artist.
     * 
     * @param clientId Your Jamendo Client ID
     * @param artistId Artist ID
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @return Search response containing artist's tracks
     */
    @GET("tracks")
    suspend fun getTracksByArtist(
        @Query("client_id") clientId: String,
        @Query("artist_id") artistId: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("format") format: String = "json",
        @Query("audioformat") audioFormat: String = "mp32",
        @Query("include") include: String = "musicinfo"
    ): Response<JamendoSearchResponse>

    /**
     * Get artist information.
     * 
     * @param clientId Your Jamendo Client ID
     * @param artistId Artist ID
     * @param format Response format
     * @return Artist response containing artist details
     */
    @GET("artists")
    suspend fun getArtist(
        @Query("client_id") clientId: String,
        @Query("id") artistId: String,
        @Query("format") format: String = "json"
    ): Response<JamendoArtistResponse>

    /**
     * Search for artists on Jamendo.
     * 
     * @param clientId Your Jamendo Client ID
     * @param query Artist name search query
     * @param limit Maximum number of results
     * @return Artist response containing matching artists
     */
    @GET("artists")
    suspend fun searchArtists(
        @Query("client_id") clientId: String,
        @Query("search") query: String,
        @Query("limit") limit: Int = 20,
        @Query("format") format: String = "json"
    ): Response<JamendoArtistResponse>

    companion object {
        /**
         * Base URL for Jamendo API v3.0
         */
        const val BASE_URL = "https://api.jamendo.com/v3.0/"
    }
}
