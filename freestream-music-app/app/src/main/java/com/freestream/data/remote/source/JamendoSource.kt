package com.freestream.data.remote.source

import android.util.Log
import com.freestream.data.model.SourceType
import com.freestream.data.model.Track
import com.freestream.data.remote.api.JamendoApi
import com.freestream.util.ApiKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MusicSource for Jamendo music service.
 * 
 * Jamendo is the primary music source for FreeStream, providing:
 * - 400,000+ Creative Commons licensed tracks
 * - Direct MP3 streaming URLs
 * - Album artwork
 * - Genre tags and metadata
 * 
 * This source requires a Client ID for authentication.
 */
@Singleton
class JamendoSource @Inject constructor(
    private val api: JamendoApi
) : MusicSource {

    companion object {
        private const val TAG = "JamendoSource"
    }

    override val sourceType: SourceType = SourceType.JAMENDO

    /**
     * Search for tracks on Jamendo.
     * 
     * @param query Search query string
     * @param limit Maximum number of results
     * @return Result containing list of matching tracks
     */
    override suspend fun search(query: String, limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Searching Jamendo for: $query")
                
                val response = api.searchTracks(
                    clientId = ApiKeys.JAMENDO_CLIENT_ID,
                    query = query,
                    limit = limit
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    
                    if (body?.isSuccess == true) {
                        val tracks = body.getResultsOrEmpty()
                            .map { dto -> mapToTrack(dto) }
                        
                        Log.d(TAG, "Found ${tracks.size} tracks on Jamendo")
                        Result.Success(tracks)
                    } else {
                        val errorMsg = body?.errorMessage ?: "Unknown error from Jamendo"
                        Log.e(TAG, "Jamendo API error: $errorMsg")
                        Result.Error(errorMsg)
                    }
                } else {
                    val errorMsg = "HTTP ${response.code()}: ${response.errorBody()?.string()}"
                    Log.e(TAG, "Jamendo HTTP error: $errorMsg")
                    Result.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception searching Jamendo", e)
                Result.Error(e)
            }
        }

    /**
     * Get trending tracks from Jamendo.
     * Sorted by total popularity (all-time most played).
     * 
     * @param limit Maximum number of results
     * @return Result containing list of trending tracks
     */
    override suspend fun getTrending(limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching trending tracks from Jamendo")
                
                val response = api.getTrendingTracks(
                    clientId = ApiKeys.JAMENDO_CLIENT_ID,
                    limit = limit
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    
                    if (body?.isSuccess == true) {
                        val tracks = body.getResultsOrEmpty()
                            .map { dto -> mapToTrack(dto) }
                        
                        Log.d(TAG, "Found ${tracks.size} trending tracks on Jamendo")
                        Result.Success(tracks)
                    } else {
                        val errorMsg = body?.errorMessage ?: "Unknown error"
                        Result.Error(errorMsg)
                    }
                } else {
                    Result.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting trending from Jamendo", e)
                Result.Error(e)
            }
        }

    /**
     * Get recently added tracks from Jamendo.
     * 
     * @param limit Maximum number of results
     * @return Result containing list of recent tracks
     */
    override suspend fun getRecent(limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching recent tracks from Jamendo")
                
                val response = api.getRecentTracks(
                    clientId = ApiKeys.JAMENDO_CLIENT_ID,
                    limit = limit
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    
                    if (body?.isSuccess == true) {
                        val tracks = body.getResultsOrEmpty()
                            .map { dto -> mapToTrack(dto) }
                        
                        Result.Success(tracks)
                    } else {
                        Result.Error(body?.errorMessage ?: "Unknown error")
                    }
                } else {
                    Result.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    /**
     * Get stream URL for a track.
     * For Jamendo, the stream URL is included in the track data,
     * so we just extract it from the track ID.
     * 
     * @param trackId Composite track ID
     * @return Result containing the stream URL
     */
    override suspend fun getStreamUrl(trackId: String): Result<String> {
        // Jamendo provides direct URLs in the track data
        // If we need a fresh URL, we'd need to refetch the track
        // For now, return error - the track's streamUrl should be used directly
        return Result.Error("Jamendo URLs are static, use track.streamUrl directly")
    }

    /**
     * Get attribution text for a Jamendo track.
     * 
     * @param track The track to get attribution for
     * @return Formatted attribution string
     */
    override fun getAttributionText(track: Track): String {
        return "\"${track.title}\" by ${track.artist} • Licensed under ${track.licenseType}"
    }

    /**
     * Get license URL for a Jamendo track.
     * 
     * @param track The track to get license URL for
     * @return URL to the license page
     */
    override fun getLicenseUrl(track: Track): String? {
        return when (track.licenseType.uppercase()) {
            "CC0" -> "https://creativecommons.org/publicdomain/zero/1.0/"
            "CC-BY" -> "https://creativecommons.org/licenses/by/4.0/"
            "CC-BY-NC" -> "https://creativecommons.org/licenses/by-nc/4.0/"
            "CC-BY-SA" -> "https://creativecommons.org/licenses/by-sa/4.0/"
            "CC-BY-ND" -> "https://creativecommons.org/licenses/by-nd/4.0/"
            else -> track.externalUrl
        }
    }

    /**
     * Check if Jamendo is available.
     * Makes a lightweight API call to verify connectivity.
     * 
     * @return true if Jamendo is accessible
     */
    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrendingTracks(
                clientId = ApiKeys.JAMENDO_CLIENT_ID,
                limit = 1
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Jamendo availability check failed", e)
            false
        }
    }

    /**
     * Map Jamendo DTO to unified Track model.
     * 
     * @param dto Jamendo track DTO
     * @return Unified Track object
     */
    private fun mapToTrack(dto: com.freestream.data.remote.dto.jamendo.JamendoTrackDto): Track {
        return Track(
            id = Track.createCompositeId(SourceType.JAMENDO, dto.id),
            title = dto.name,
            artist = dto.artistName,
            album = dto.albumName,
            durationMs = dto.durationMs,
            artworkUrl = dto.albumImage,
            streamUrl = dto.audioUrl,
            source = SourceType.JAMENDO,
            licenseType = dto.formattedLicense,
            externalUrl = dto.shareUrl,
            tags = dto.tags
        )
    }
}
