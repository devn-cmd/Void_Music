package com.freestream.data.remote.source

import com.freestream.data.model.SourceType
import com.freestream.data.model.Track

/**
 * Interface defining the contract for all music sources in FreeStream.
 * 
 * Each music source (Jamendo, Freesound, Archive.org, ccMixter) implements
 * this interface to provide a unified API for the rest of the app.
 * 
 * This abstraction allows the repository to treat all sources identically,
 * making it easy to add new sources in the future.
 */
interface MusicSource {

    /**
     * The type of this music source.
     * Used for identification and filtering.
     */
    val sourceType: SourceType

    /**
     * Human-readable name of this source.
     */
    val sourceName: String
        get() = sourceType.displayName

    /**
     * Whether this source requires authentication.
     */
    val requiresAuth: Boolean
        get() = sourceType.requiresAuth

    /**
     * Search for tracks/sounds in this source.
     * 
     * @param query Search query string
     * @param limit Maximum number of results to return
     * @return Result containing list of tracks on success, or error on failure
     */
    suspend fun search(query: String, limit: Int = 20): Result<List<Track>>

    /**
     * Get trending/popular tracks from this source.
     * 
     * @param limit Maximum number of results to return
     * @return Result containing list of trending tracks on success
     */
    suspend fun getTrending(limit: Int = 20): Result<List<Track>>

    /**
     * Get the direct stream URL for a track.
     * 
     * Some sources may need to fetch fresh URLs (e.g., for expiring links).
     * 
     * @param trackId The track's composite ID
     * @return Result containing the stream URL on success
     */
    suspend fun getStreamUrl(trackId: String): Result<String>

    /**
     * Get attribution text for a track.
     * This is the text that should be displayed for license compliance.
     * 
     * @param track The track to get attribution for
     * @return Formatted attribution string
     */
    fun getAttributionText(track: Track): String

    /**
     * Get the license URL for a track.
     * 
     * @param track The track to get license URL for
     * @return URL to the license page, or null if not available
     */
    fun getLicenseUrl(track: Track): String?

    /**
     * Check if this source is currently available.
     * Performs a lightweight health check.
     * 
     * @return true if the source is accessible
     */
    suspend fun isAvailable(): Boolean

    /**
     * Get recent additions from this source.
     * Default implementation falls back to trending.
     * 
     * @param limit Maximum number of results
     * @return Result containing list of recent tracks
     */
    suspend fun getRecent(limit: Int = 20): Result<List<Track>> = getTrending(limit)
}

/**
 * Sealed class representing the result of a source operation.
 * 
 * Using a sealed class ensures type safety and forces handling of both
 * success and error cases.
 */
sealed class Result<out T> {
    
    /**
     * Successful operation with data.
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Failed operation with error details.
     */
    data class Error(
        val exception: Throwable,
        val message: String
    ) : Result<Nothing>() {
        
        /**
         * Convenience constructor from exception.
         */
        constructor(exception: Exception) : this(
            exception,
            exception.message ?: "Unknown error occurred"
        )
        
        /**
         * Convenience constructor with custom message.
         */
        constructor(message: String) : this(
            RuntimeException(message),
            message
        )
    }

    /**
     * Gets the data if success, null if error.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    /**
     * Gets the data if success, default value if error.
     */
    fun getOrDefault(default: T): T = when (this) {
        is Success -> data
        is Error -> default
    }

    /**
     * Maps the success data to another type.
     */
    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    /**
     * Executes action on success.
     */
    fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes action on error.
     */
    fun onError(action: (Error) -> Unit): Result<T> {
        if (this is Error) action(this)
        return this
    }

    /**
     * Whether this result is a success.
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Whether this result is an error.
     */
    val isError: Boolean
        get() = this is Error
}
