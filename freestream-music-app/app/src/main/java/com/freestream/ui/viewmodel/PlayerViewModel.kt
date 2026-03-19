package com.freestream.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freestream.data.model.PlaybackState
import com.freestream.data.model.RepeatMode
import com.freestream.data.model.Track
import com.freestream.data.repository.MusicRepository
import com.freestream.util.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Player screen.
 * 
 * Manages:
 * - Current playback state
 * - Playback controls (play, pause, seek, etc.)
 * - Queue management
 * - Track information display
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: MusicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ===== UI State =====
    
    data class PlayerUiState(
        val playbackState: PlaybackState = PlaybackState.Idle,
        val currentTrack: Track? = null,
        val isPlaying: Boolean = false,
        val currentPosition: Long = 0,
        val duration: Long = 0,
        val progress: Float = 0f,
        val isShuffleEnabled: Boolean = false,
        val repeatMode: RepeatMode = RepeatMode.OFF,
        val queue: List<Track> = emptyList(),
        val currentQueueIndex: Int = 0,
        val volume: Float = 0.8f,
        val isFavorite: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    // Track ID from navigation argument
    private val initialTrackId: String? = savedStateHandle["trackId"]

    init {
        // Initialize with track if provided
        initialTrackId?.let { trackId ->
            // TODO: Load track and start playback
        }
        
        // Start position update loop
        startPositionUpdates()
    }

    /**
     * Start periodic position updates.
     */
    private fun startPositionUpdates() {
        viewModelScope.launch {
            while (true) {
                updatePosition()
                delay(1000) // Update every second
            }
        }
    }

    /**
     * Update current position from playback state.
     */
    private fun updatePosition() {
        val state = _uiState.value.playbackState
        val position = state.currentPosition
        val duration = when (state) {
            is PlaybackState.Playing -> state.durationMs
            is PlaybackState.Paused -> state.durationMs
            else -> 0
        }
        
        val progress = if (duration > 0) {
            position.toFloat() / duration.toFloat()
        } else {
            0f
        }

        _uiState.value = _uiState.value.copy(
            currentPosition = position,
            duration = duration,
            progress = progress.coerceIn(0f, 1f)
        )
    }

    /**
     * Play a track or playlist.
     */
    fun play(tracks: List<Track>, startIndex: Int = 0) {
        // TODO: Integrate with PlaybackService
        _uiState.value = _uiState.value.copy(
            queue = tracks,
            currentQueueIndex = startIndex,
            currentTrack = tracks.getOrNull(startIndex)
        )
    }

    /**
     * Play a single track.
     */
    fun play(track: Track) {
        play(listOf(track), 0)
    }

    /**
     * Toggle play/pause.
     */
    fun togglePlayPause() {
        val isPlaying = _uiState.value.isPlaying
        _uiState.value = _uiState.value.copy(isPlaying = !isPlaying)
        // TODO: Control PlaybackService
    }

    /**
     * Pause playback.
     */
    fun pause() {
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    /**
     * Resume playback.
     */
    fun play() {
        _uiState.value = _uiState.value.copy(isPlaying = true)
    }

    /**
     * Seek to position.
     */
    fun seekTo(progress: Float) {
        val duration = _uiState.value.duration
        val position = (progress * duration).toLong()
        _uiState.value = _uiState.value.copy(
            currentPosition = position,
            progress = progress
        )
        // TODO: Seek in PlaybackService
    }

    /**
     * Skip to next track.
     */
    fun next() {
        val currentIndex = _uiState.value.currentQueueIndex
        val queue = _uiState.value.queue
        
        if (currentIndex < queue.size - 1) {
            val newIndex = currentIndex + 1
            _uiState.value = _uiState.value.copy(
                currentQueueIndex = newIndex,
                currentTrack = queue.getOrNull(newIndex)
            )
        }
    }

    /**
     * Skip to previous track.
     */
    fun previous() {
        val currentIndex = _uiState.value.currentQueueIndex
        val queue = _uiState.value.queue
        
        if (currentIndex > 0) {
            val newIndex = currentIndex - 1
            _uiState.value = _uiState.value.copy(
                currentQueueIndex = newIndex,
                currentTrack = queue.getOrNull(newIndex)
            )
        }
    }

    /**
     * Toggle shuffle mode.
     */
    fun toggleShuffle() {
        _uiState.value = _uiState.value.copy(
            isShuffleEnabled = !_uiState.value.isShuffleEnabled
        )
    }

    /**
     * Cycle through repeat modes: OFF -> ALL -> ONE -> OFF
     */
    fun cycleRepeatMode() {
        val newMode = when (_uiState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _uiState.value = _uiState.value.copy(repeatMode = newMode)
    }

    /**
     * Set volume level.
     */
    fun setVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(
            volume = volume.coerceIn(0f, 1f)
        )
    }

    /**
     * Toggle favorite status of current track.
     */
    fun toggleFavorite() {
        val track = _uiState.value.currentTrack ?: return
        
        viewModelScope.launch {
            val isNowFavorite = repository.toggleFavorite(track)
            _uiState.value = _uiState.value.copy(isFavorite = isNowFavorite)
        }
    }

    /**
     * Check if current track is favorited.
     */
    fun checkFavoriteStatus() {
        val trackId = _uiState.value.currentTrack?.id ?: return
        
        viewModelScope.launch {
            repository.isFavorite(trackId).collect { isFavorite ->
                _uiState.value = _uiState.value.copy(isFavorite = isFavorite)
            }
        }
    }

    /**
     * Get formatted current position.
     */
    fun getFormattedPosition(): String {
        return TimeUtils.formatDuration(_uiState.value.currentPosition)
    }

    /**
     * Get formatted duration.
     */
    fun getFormattedDuration(): String {
        return TimeUtils.formatDuration(_uiState.value.duration)
    }

    /**
     * Get attribution text for current track.
     */
    fun getAttributionText(): String {
        val track = _uiState.value.currentTrack ?: return ""
        return repository.getAttributionText(track)
    }

    /**
     * Get license URL for current track.
     */
    fun getLicenseUrl(): String? {
        val track = _uiState.value.currentTrack ?: return null
        return repository.getLicenseUrl(track)
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
