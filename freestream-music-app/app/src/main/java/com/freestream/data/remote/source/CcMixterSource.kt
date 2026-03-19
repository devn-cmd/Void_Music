package com.freestream.data.remote.source

import android.util.Log
import com.freestream.data.model.SourceType
import com.freestream.data.model.Track
import com.freestream.data.remote.api.CcMixterApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MusicSource for ccMixter music service.
 * 
 * ccMixter provides:
 * - Remix culture content (remixes of existing songs)
 * - Acapellas (vocal tracks without instrumentation)
 * - Samples and loops for music production
 * - All content is Creative Commons BY licensed
 * 
 * No authentication required.
 * 
 * Note: ccMixter uses HTTP (not HTTPS), so cleartext traffic
 * must be enabled in the Android manifest.
 */
@Singleton
class CcMixterSource @Inject constructor(
    private val api: CcMixterApi
) : MusicSource {

    companion object {
        private const val TAG = "CcMixterSource"
    }

    override val sourceType: SourceType = SourceType.CCMIXTER

    /**
     * Search for uploads on ccMixter.
     * 
     * ccMixter searches by tags, so we use the query as tags.
     * 
     * @param query Search query string (used as tags)
     * @param limit Maximum number of results
     * @return Result containing list of matching tracks
     */
    override suspend fun search(query: String, limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Searching ccMixter for: $query")
                
                val response = api.queryByTags(
                    tags = query,
                    limit = limit
                )

                if (response.isSuccessful) {
                    val uploads = response.body() ?: emptyList()
                    
                    val tracks = uploads.map { dto -> mapToTrack(dto) }
                        .filter { it.streamUrl.isNotBlank() } // Only include tracks with valid URLs
                    
                    Log.d(TAG, "Found ${tracks.size} tracks on ccMixter")
                    Result.Success(tracks)
                } else {
                    val errorMsg = "HTTP ${response.code()}: ${response.errorBody()?.string()}"
                    Log.e(TAG, "ccMixter HTTP error: $errorMsg")
                    Result.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception searching ccMixter", e)
                Result.Error(e)
            }
        }

    /**
     * Get trending uploads from ccMixter.
     * Sorted by rank (popularity).
     * 
     * @param limit Maximum number of results
     * @return Result containing list of trending tracks
     */
    override suspend fun getTrending(limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching trending from ccMixter")
                
                val response = api.getTrendingUploads(limit = limit)

                if (response.isSuccessful) {
                    val uploads = response.body() ?: emptyList()
                    
                    val tracks = uploads.map { dto -> mapToTrack(dto) }
                        .filter { it.streamUrl.isNotBlank() }
                    
                    Log.d(TAG, "Found ${tracks.size} trending tracks on ccMixter")
                    Result.Success(tracks)
                } else {
                    Result.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting trending from ccMixter", e)
                Result.Error(e)
            }
        }

    /**
     * Get recent uploads from ccMixter.
     * 
     * @param limit Maximum number of results
     * @return Result containing list of recent tracks
     */
    override suspend fun getRecent(limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching recent uploads from ccMixter")
                
                val response = api.getRecentUploads(limit = limit)

                if (response.isSuccessful) {
                    val uploads = response.body() ?: emptyList()
                    
                    val tracks = uploads.map { dto -> mapToTrack(dto) }
                        .filter { it.streamUrl.isNotBlank() }
                    
                    Result.Success(tracks)
                } else {
                    Result.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    /**
     * Get stream URL for a track.
     * For ccMixter, URLs are static and included in the track data.
     * 
     * @param trackId Composite track ID
     * @return Result containing the stream URL
     */
    override suspend fun getStreamUrl(trackId: String): Result<String> {
        // ccMixter URLs are static, use track.streamUrl directly
        return Result.Error("ccMixter URLs are static, use track.streamUrl directly")
    }

    /**
     * Get attribution text for a ccMixter track.
     * 
     * @param track The track to get attribution for
     * @return Formatted attribution string
     */
    override fun getAttributionText(track: Track): String {
        return "\"${track.title}\" by ${track.artist} • From ccMixter.org • CC-BY"
    }

    /**
     * Get license URL for a ccMixter track.
     * All ccMixter content is CC-BY.
     * 
     * @param track The track to get license URL for
     * @return URL to the license page
     */
    override fun getLicenseUrl(track: Track): String {
        return "https://creativecommons.org/licenses/by/3.0/"
    }

    /**
     * Check if ccMixter is available.
     * 
     * @return true if ccMixter is accessible
     */
    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrendingUploads(limit = 1)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "ccMixter availability check failed", e)
            false
        }
    }

    /**
     * Map ccMixter DTO to unified Track model.
     * 
     * @param dto ccMixter upload DTO
     * @return Unified Track object
     */
    private fun mapToTrack(dto: com.freestream.data.remote.dto.ccmixter.CcMixterUploadDto): Track {
        return Track(
            id = Track.createCompositeId(SourceType.CCMIXTER, dto.uploadId),
            title = dto.uploadName,
            artist = dto.artistName,
            album = null, // ccMixter doesn't have albums
            durationMs = dto.durationMs,
            artworkUrl = null, // ccMixter doesn't provide artwork
            streamUrl = dto.streamUrl ?: "",
            source = SourceType.CCMIXTER,
            licenseType = dto.formattedLicense,
            externalUrl = dto.filePageUrl ?: "http://ccmixter.org",
            tags = dto.tags
        )
    }
}
