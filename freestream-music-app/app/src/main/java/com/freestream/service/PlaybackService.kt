package com.freestream.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.freestream.MainActivity
import com.freestream.R
import com.freestream.data.model.PlaybackState
import com.freestream.data.model.RepeatMode
import com.freestream.data.model.Track
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Media playback service for FreeStream.
 * 
 * This service handles:
 * - Background audio playback
 * - Media session for lock screen controls
 * - Playback notifications
 * - Audio focus management
 * 
 * Extends MediaSessionService for proper Android media integration.
 */
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var exoPlayer: ExoPlayer

    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Playback state
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    // Current queue
    private val _currentQueue = MutableStateFlow<List<Track>>(emptyList())
    val currentQueue: StateFlow<List<Track>> = _currentQueue.asStateFlow()

    private var currentTrackIndex = 0

    override fun onCreate() {
        super.onCreate()
        initializeMediaSession()
        setupPlayerListeners()
    }

    /**
     * Initialize the MediaSession with ExoPlayer.
     */
    private fun initializeMediaSession() {
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setSessionCallback(MediaSessionCallback())
            .build()
    }

    /**
     * Setup ExoPlayer listeners for state updates.
     */
    private fun setupPlayerListeners() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                updatePlaybackState()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentTrackIndex = exoPlayer.currentMediaItemIndex
                updatePlaybackState()
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                updatePlaybackState()
            }
        })
    }

    /**
     * Update the playback state flow based on ExoPlayer state.
     */
    private fun updatePlaybackState() {
        val currentTrack = _currentQueue.value.getOrNull(currentTrackIndex)
        
        val state = when (exoPlayer.playbackState) {
            Player.STATE_IDLE -> PlaybackState.Idle
            Player.STATE_BUFFERING -> PlaybackState.Buffering
            Player.STATE_READY -> {
                if (currentTrack != null) {
                    if (exoPlayer.isPlaying) {
                        PlaybackState.Playing(
                            track = currentTrack,
                            positionMs = exoPlayer.currentPosition,
                            durationMs = exoPlayer.duration.coerceAtLeast(0),
                            isShuffleEnabled = exoPlayer.shuffleModeEnabled,
                            repeatMode = mapRepeatMode(exoPlayer.repeatMode)
                        )
                    } else {
                        PlaybackState.Paused(
                            track = currentTrack,
                            positionMs = exoPlayer.currentPosition,
                            durationMs = exoPlayer.duration.coerceAtLeast(0),
                            isShuffleEnabled = exoPlayer.shuffleModeEnabled,
                            repeatMode = mapRepeatMode(exoPlayer.repeatMode)
                        )
                    }
                } else {
                    PlaybackState.Idle
                }
            }
            Player.STATE_ENDED -> PlaybackState.Ended
            else -> PlaybackState.Idle
        }
        
        _playbackState.value = state
    }

    /**
     * Map ExoPlayer repeat mode to app RepeatMode.
     */
    private fun mapRepeatMode(mode: Int): RepeatMode {
        return when (mode) {
            Player.REPEAT_MODE_OFF -> RepeatMode.OFF
            Player.REPEAT_MODE_ONE -> RepeatMode.ONE
            Player.REPEAT_MODE_ALL -> RepeatMode.ALL
            else -> RepeatMode.OFF
        }
    }

    /**
     * Play a track or playlist.
     */
    fun play(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        
        _currentQueue.value = tracks
        currentTrackIndex = startIndex.coerceIn(0, tracks.size - 1)
        
        val mediaItems = tracks.map { it.toMediaItem() }
        exoPlayer.setMediaItems(mediaItems, currentTrackIndex, 0)
        exoPlayer.prepare()
        exoPlayer.play()
        
        updatePlaybackState()
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
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    /**
     * Pause playback.
     */
    fun pause() {
        exoPlayer.pause()
    }

    /**
     * Resume playback.
     */
    fun play() {
        exoPlayer.play()
    }

    /**
     * Stop playback.
     */
    fun stop() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        _currentQueue.value = emptyList()
        currentTrackIndex = 0
        _playbackState.value = PlaybackState.Idle
    }

    /**
     * Skip to next track.
     */
    fun next() {
        if (currentTrackIndex < _currentQueue.value.size - 1) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    /**
     * Skip to previous track.
     */
    fun previous() {
        if (currentTrackIndex > 0) {
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    /**
     * Seek to position.
     */
    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    /**
     * Set shuffle mode.
     */
    fun setShuffleEnabled(enabled: Boolean) {
        exoPlayer.shuffleModeEnabled = enabled
    }

    /**
     * Set repeat mode.
     */
    fun setRepeatMode(mode: RepeatMode) {
        exoPlayer.repeatMode = when (mode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }
    }

    /**
     * Convert Track to ExoPlayer MediaItem.
     */
    private fun Track.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(streamUrl)
            .setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(artworkUrl?.let { android.net.Uri.parse(it) })
                    .build()
            )
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Continue playing in background or stop based on preference
        // For now, continue playing
        if (!exoPlayer.isPlaying) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        mediaSession?.release()
        mediaSession = null
        exoPlayer.release()
        super.onDestroy()
    }

    /**
     * MediaSession callback for handling controller requests.
     */
    private inner class MediaSessionCallback : MediaSession.Callback {
        // Handle custom commands if needed
    }

    /**
     * Binder for service binding.
     */
    inner class PlaybackBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return PlaybackBinder()
    }
}
