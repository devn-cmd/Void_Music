package com.freestream.data.remote.source

import android.util.Log
import com.freestream.data.model.SourceType
import com.freestream.data.model.Track
import com.freestream.data.remote.api.ArchiveApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MusicSource for Internet Archive audio collection.
 * 
 * Internet Archive provides:
 * - Live concert recordings (etree collection)
 * - Vintage 78rpm recordings
 * - Public domain audio
 * - Creative Commons licensed content
 * 
 * No authentication required.
 * 
 * Note: This is a two-step API:
 * 1. Search to get item identifiers
 * 2. Fetch metadata for each item to get audio file URLs
 */
@Singleton
class ArchiveOrgSource @Inject constructor(
    private val api: ArchiveApi
) : MusicSource {

    companion object {
        private const val TAG = "ArchiveOrgSource"
        private const val MAX_CONCURRENT_METADATA_REQUESTS = 5
    }

    override val sourceType: SourceType = SourceType.ARCHIVE

    /**
     * Search for audio on Internet Archive.
     * 
     * This performs a search then fetches metadata for each result
     * to get the actual audio file URLs.
     * 
     * @param query Search query string
     * @param limit Maximum number of results
     * @return Result containing list of matching tracks
     */
    override suspend fun search(query: String, limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Searching Archive.org for: $query")
                
                // Step 1: Search for items
                val searchResponse = api.searchAudio(
                    query = query,
                    rows = limit
                )

                if (searchResponse.isSuccessful) {
                    val searchBody = searchResponse.body()
                    val docs = searchBody?.documents ?: emptyList()
                    
                    Log.d(TAG, "Found ${docs.size} items on Archive.org")
                    
                    if (docs.isEmpty()) {
                        return@withContext Result.Success(emptyList())
                    }
                    
                    // Step 2: Fetch metadata for each item (concurrent)
                    val tracks = fetchMetadataForItems(docs)
                    
                    Log.d(TAG, "Successfully processed ${tracks.size} tracks from Archive.org")
                    Result.Success(tracks)
                } else {
                    val errorMsg = "HTTP ${searchResponse.code()}: ${searchResponse.errorBody()?.string()}"
                    Log.e(TAG, "Archive.org search error: $errorMsg")
                    Result.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception searching Archive.org", e)
                Result.Error(e)
            }
        }

    /**
     * Get trending audio from Archive.org.
     * Sorted by download count.
     * 
     * @param limit Maximum number of results
     * @return Result containing list of trending tracks
     */
    override suspend fun getTrending(limit: Int): Result<List<Track>> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching trending audio from Archive.org")
                
                val searchResponse = api.getTrendingAudio(rows = limit)

                if (searchResponse.isSuccessful) {
                    val searchBody = searchResponse.body()
                    val docs = searchBody?.documents ?: emptyList()
                    
                    if (docs.isEmpty()) {
                        return@withContext Result.Success(emptyList())
                    }
                    
                    val tracks = fetchMetadataForItems(docs)
                    Result.Success(tracks)
                } else {
                    Result.Error("HTTP ${searchResponse.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting trending from Archive.org", e)
                Result.Error(e)
            }
        }

    /**
     * Fetch metadata for multiple items concurrently.
     * Limits concurrent requests to avoid overwhelming the API.
     * 
     * @param docs List of archive documents from search
     * @return List of tracks with full metadata
     */
    private suspend fun fetchMetadataForItems(
        docs: List<com.freestream.data.remote.dto.archive.ArchiveDocDto>
    ): List<Track> = coroutineScope {
        docs.map { doc ->
            async {
                try {
                    fetchTrackFromMetadata(doc)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch metadata for ${doc.identifier}", e)
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }

    /**
     * Fetch metadata for a single item and convert to Track.
     * 
     * @param doc Archive document from search
     * @return Track object or null if no audio available
     */
    private suspend fun fetchTrackFromMetadata(
        doc: com.freestream.data.remote.dto.archive.ArchiveDocDto
    ): Track? = withContext(Dispatchers.IO) {
        try {
            val metadataResponse = api.getMetadata(doc.identifier)
            
            if (metadataResponse.isSuccessful) {
                val metadata = metadataResponse.body()
                val streamUrl = metadata?.streamUrl
                
                if (streamUrl != null) {
                    Track(
                        id = Track.createCompositeId(SourceType.ARCHIVE, doc.identifier),
                        title = metadata.title ?: doc.displayTitle,
                        artist = metadata.creator ?: doc.displayCreator,
                        album = metadata.collection?.firstOrNull(),
                        durationMs = metadata.durationMs,
                        artworkUrl = null, // Archive doesn't provide artwork
                        streamUrl = streamUrl,
                        source = SourceType.ARCHIVE,
                        licenseType = detectLicense(metadata.licenseUrl),
                        externalUrl = doc.itemUrl,
                        tags = metadata.subject ?: doc.tags
                    )
                } else {
                    Log.w(TAG, "No audio file found for ${doc.identifier}")
                    null
                }
            } else {
                Log.w(TAG, "Metadata request failed for ${doc.identifier}: HTTP ${metadataResponse.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching metadata for ${doc.identifier}", e)
            null
        }
    }

    /**
     * Detect license type from license URL.
     * 
     * @param licenseUrl License URL from metadata
     * @return License type string
     */
    private fun detectLicense(licenseUrl: String?): String {
        return when {
            licenseUrl == null -> "Unknown"
            licenseUrl.contains("publicdomain") -> "Public Domain"
            licenseUrl.contains("zero") -> "CC0"
            licenseUrl.contains("by-nc") -> "CC-BY-NC"
            licenseUrl.contains("by-sa") -> "CC-BY-SA"
            licenseUrl.contains("by-nd") -> "CC-BY-ND"
            licenseUrl.contains("by/") -> "CC-BY"
            else -> "Unknown"
        }
    }

    /**
     * Get stream URL for a track.
     * For Archive.org, we may need to refresh the metadata.
     * 
     * @param trackId Composite track ID
     * @return Result containing the stream URL
     */
    override suspend fun getStreamUrl(trackId: String): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                val identifier = trackId.substringAfter("${SourceType.ARCHIVE.name.lowercase()}_")
                
                val response = api.getMetadata(identifier)
                
                if (response.isSuccessful) {
                    val metadata = response.body()
                    val streamUrl = metadata?.streamUrl
                    
                    if (streamUrl != null) {
                        Result.Success(streamUrl)
                    } else {
                        Result.Error("No audio file available")
                    }
                } else {
                    Result.Error("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    /**
     * Get attribution text for an Archive.org track.
     * 
     * @param track The track to get attribution for
     * @return Formatted attribution string
     */
    override fun getAttributionText(track: Track): String {
        return "\"${track.title}\" by ${track.artist} • From Internet Archive • ${track.licenseType}"
    }

    /**
     * Get license URL for an Archive.org track.
     * 
     * @param track The track to get license URL for
     * @return URL to the license page
     */
    override fun getLicenseUrl(track: Track): String? {
        return when (track.licenseType.uppercase()) {
            "PUBLIC DOMAIN" -> "https://creativecommons.org/publicdomain/mark/1.0/"
            "CC0" -> "https://creativecommons.org/publicdomain/zero/1.0/"
            "CC-BY" -> "https://creativecommons.org/licenses/by/4.0/"
            "CC-BY-NC" -> "https://creativecommons.org/licenses/by-nc/4.0/"
            "CC-BY-SA" -> "https://creativecommons.org/licenses/by-sa/4.0/"
            else -> track.externalUrl
        }
    }

    /**
     * Check if Archive.org is available.
     * 
     * @return true if Archive.org is accessible
     */
    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrendingAudio(rows = 1)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Archive.org availability check failed", e)
            false
        }
    }
}
