package com.freestream.data.remote.source

import android.util.Log
import com.freestream.data.model.SourceType
import com.freestream.data.model.Track
import com.freestream.data.remote.api.FreesoundApi
import com.freestream.util.ApiKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MusicSource for Freesound audio service.
 * 
 * Freesound provides sound effects, samples, and short audio clips.
 * Key characteristics:
 * - 120-second preview limit on free tier
 * - Great for sound effects and ambient sounds
 * - Creative Commons licensed
 * - Waveform visualizations available
 * 
 * This source requires an API Key for authentication.
 */
@Singleton
class FreesoundSource @Inject constructor(
    private val api: FreesoundApi
) : MusicSource {

    companion object {
        private const val TAG = "FreesoundSource"
    }

    override val sourceType: SourceType = SourceType.FREESOUND

    /**
     * Search for sounds on Freesound.
     * 
     * @param query Search query string
     * @param limit Maximum number of results
     * @return Result containing list of matching tracks
     */
    override suspend fun search(query: String, limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Searching Freesound for: $query")
                
                val response = api.searchSounds(
                    query = query,
                    token = ApiKeys.FREESOUND_API_KEY,
                    pageSize = limit
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    
                    if (body != null) {
                        val tracks = body.getResultsOrEmpty()
                            .map { dto -> mapToTrack(dto) }
                        
                        Log.d(TAG, "Found ${tracks.size} sounds on Freesound")
                        Result.Success(tracks)
                    } else {
                        Result.Error("Empty response from Freesound")
                    }
                } else {
                    val errorMsg = "HTTP ${response.code()}: ${response.errorBody()?.string()}"
                    Log.e(TAG, "Freesound HTTP error: $errorMsg")
                    Result.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception searching Freesound", e)
                Result.Error(e)
            }
        }

    /**
     * Get trending sounds from Freesound.
     * Sorted by download count.
     * 
     * @param limit Maximum number of results
     * @return Result containing list of trending sounds
     */
    override suspend fun getTrending(limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching trending sounds from Freesound")
                
                val response = api.getTrendingSounds(
                    token = ApiKeys.FREESOUND_API_KEY,
                    pageSize = limit
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    
                    if (body != null) {
                        val tracks = body.getResultsOrEmpty()
                            .map { dto -> mapToTrack(dto) }
                        
                        Log.d(TAG, "Found ${tracks.size} trending sounds on Freesound")
                        Result.Success(tracks)
                    } else {
                        Result.Error("Empty response")
                    }
                } else {
                    Result.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting trending from Freesound", e)
                Result.Error(e)
            }
        }

    /**
     * Get stream URL for a sound.
     * For Freesound, the preview URL is included in the track data.
     * 
     * @param trackId Composite track ID
     * @return Result containing the stream URL
     */
    override suspend fun getStreamUrl(trackId: String): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                // Extract Freesound ID from composite ID
                val freesoundId = trackId.substringAfter("${SourceType.FREESOUND.name.lowercase()}_")
                    .toIntOrNull()
                
                if (freesoundId == null) {
                    return@withContext Result.Error("Invalid Freesound track ID: $trackId")
                }
                
                val response = api.getSound(
                    soundId = freesoundId,
                    token = ApiKeys.FREESOUND_API_KEY
                )

                if (response.isSuccessful) {
                    val dto = response.body()
                    val previewUrl = dto?.previewUrl
                    
                    if (previewUrl != null) {
                        Result.Success(previewUrl)
                    } else {
                        Result.Error("No preview URL available")
                    }
                } else {
                    Result.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    /**
     * Get attribution text for a Freesound track.
     * 
     * @param track The track to get attribution for
     * @return Formatted attribution string
     */
    override fun getAttributionText(track: Track): String {
        return "\"${track.title}\" by ${track.artist} • ${track.licenseType} • From Freesound.org"
    }

    /**
     * Get license URL for a Freesound track.
     * 
     * @param track The track to get license URL for
     * @return URL to the license page
     */
    override fun getLicenseUrl(track: Track): String? {
        return when (track.licenseType.uppercase()) {
            "CC0" -> "https://creativecommons.org/publicdomain/zero/1.0/"
            "CC-BY" -> "https://creativecommons.org/licenses/by/4.0/"
            "CC-BY-NC" -> "https://creativecommons.org/licenses/by-nc/4.0/"
            "SAMPLING+" -> "https://creativecommons.org/licenses/sampling+/1.0/"
            else -> track.externalUrl
        }
    }

    /**
     * Check if Freesound is available.
     * 
     * @return true if Freesound is accessible
     */
    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrendingSounds(
                token = ApiKeys.FREESOUND_API_KEY,
                pageSize = 1
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Freesound availability check failed", e)
            false
        }
    }

    /**
     * Map Freesound DTO to unified Track model.
     * 
     * @param dto Freesound result DTO
     * @return Unified Track object
     */
    private fun mapToTrack(dto: com.freestream.data.remote.dto.freesound.FreesoundResultDto): Track {
        return Track(
            id = Track.createCompositeId(SourceType.FREESOUND, dto.id.toString()),
            title = dto.name,
            artist = dto.username,
            album = null, // Freesound doesn't have albums
            durationMs = dto.durationMs,
            artworkUrl = dto.artworkUrl,
            streamUrl = dto.previewUrl ?: "", // Fallback, should always have preview
            source = SourceType.FREESOUND,
            licenseType = dto.formattedLicense,
            externalUrl = dto.url,
            tags = dto.tags ?: emptyList()
        )
    }
}
