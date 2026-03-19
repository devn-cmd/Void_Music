package com.freestream.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Sealed class representing all possible playback states in the app.
 * 
 * Using a sealed class ensures exhaustive when() expressions and type safety.
 * Each state contains relevant data for that specific state.
 */
sealed class PlaybackState : Parcelable {

    /**
     * Nothing is playing. Initial state or after playback ends.
     */
    @Parcelize
    data object Idle : PlaybackState()

    /**
     * Loading/preparing a track for playback.
     * Shows loading indicator in UI.
     */
    @Parcelize
    data object Buffering : PlaybackState()

    /**
     * Track is currently playing.
     * 
     * @param track The currently playing track
     * @param positionMs Current playback position in milliseconds
     * @param durationMs Total track duration in milliseconds
     * @param isShuffleEnabled Whether shuffle mode is active
     * @param repeatMode Current repeat mode (off, one, all)
     */
    @Parcelize
    data class Playing(
        val track: Track,
        val positionMs: Long = 0,
        val durationMs: Long = 0,
        val isShuffleEnabled: Boolean = false,
        val repeatMode: RepeatMode = RepeatMode.OFF
    ) : PlaybackState()

    /**
     * Track is paused but ready to resume.
     * 
     * @param track The paused track
     * @param positionMs Current playback position in milliseconds
     * @param durationMs Total track duration in milliseconds
     * @param isShuffleEnabled Whether shuffle mode is active
     * @param repeatMode Current repeat mode
     */
    @Parcelize
    data class Paused(
        val track: Track,
        val positionMs: Long = 0,
        val durationMs: Long = 0,
        val isShuffleEnabled: Boolean = false,
        val repeatMode: RepeatMode = RepeatMode.OFF
    ) : PlaybackState()

    /**
     * Playback error occurred.
     * 
     * @param errorMessage Human-readable error description
     * @param errorCode Optional error code for debugging
     * @param retryable Whether the operation can be retried
     */
    @Parcelize
    data class Error(
        val errorMessage: String,
        val errorCode: Int? = null,
        val retryable: Boolean = true
    ) : PlaybackState()

    /**
     * Playback reached end of queue.
     */
    @Parcelize
    data object Ended : PlaybackState()

    /**
     * Gets the current track from any state that has one.
     */
    val currentTrack: Track?
        get() = when (this) {
            is Playing -> track
            is Paused -> track
            else -> null
        }

    /**
     * Gets the current position from any state that has one.
     */
    val currentPosition: Long
        get() = when (this) {
            is Playing -> positionMs
            is Paused -> positionMs
            else -> 0
        }

    /**
     * Whether playback is currently active (Playing state).
     */
    val isPlaying: Boolean
        get() = this is Playing

    /**
     * Whether playback is paused.
     */
    val isPaused: Boolean
        get() = this is Paused

    /**
     * Whether currently in a loading state.
     */
    val isBuffering: Boolean
        get() = this is Buffering

    /**
     * Whether an error has occurred.
     */
    val hasError: Boolean
        get() = this is Error
}

/**
 * Enum representing repeat modes for playback.
 */
enum class RepeatMode {
    OFF,    // No repeat
    ONE,    // Repeat current track
    ALL     // Repeat entire queue
}

/**
 * Data class representing the playback queue state.
 * 
 * @param tracks List of tracks in the queue
 * @param currentIndex Index of currently playing track
 * @param originalOrder Original order before shuffle (for unshuffling)
 */
@Parcelize
data class PlaybackQueue(
    val tracks: List<Track> = emptyList(),
    val currentIndex: Int = 0,
    val originalOrder: List<Track> = emptyList()
) : Parcelable {
    
    /**
     * Current track in the queue.
     */
    val currentTrack: Track?
        get() = tracks.getOrNull(currentIndex)
    
    /**
     * Whether there is a next track.
     */
    val hasNext: Boolean
        get() = currentIndex < tracks.size - 1
    
    /**
     * Whether there is a previous track.
     */
    val hasPrevious: Boolean
        get() = currentIndex > 0
    
    /**
     * Total number of tracks in queue.
     */
    val size: Int
        get() = tracks.size
    
    /**
     * Whether queue is empty.
     */
    val isEmpty: Boolean
        get() = tracks.isEmpty()
}
