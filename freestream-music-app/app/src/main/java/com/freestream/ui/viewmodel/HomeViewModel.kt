package com.freestream.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freestream.data.model.Playlist
import com.freestream.data.model.SourceType
import com.freestream.data.model.Track
import com.freestream.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 * 
 * Manages:
 * - Trending tracks from Jamendo
 * - Recently played tracks from history
 * - User playlists
 * - Active music sources
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    // ===== UI State =====
    
    data class HomeUiState(
        val isLoading: Boolean = false,
        val trendingTracks: List<Track> = emptyList(),
        val recentlyPlayed: List<Track> = emptyList(),
        val playlists: List<Playlist> = emptyList(),
        val activeSources: Set<SourceType> = SourceType.values().toSet(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeActiveSources()
        observePlaylists()
    }

    /**
     * Load all home screen data.
     */
    private fun loadData() {
        loadTrending()
        loadRecentlyPlayed()
    }

    /**
     * Load trending tracks from Jamendo.
     */
    fun loadTrending() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val tracks = repository.getTrending(limit = 10)
                _uiState.value = _uiState.value.copy(
                    trendingTracks = tracks,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load trending tracks",
                    isLoading = false
                )
            }
        }
    }

    /**
     * Load recently played tracks from history.
     */
    private fun loadRecentlyPlayed() {
        viewModelScope.launch {
            // TODO: Implement history retrieval with full track data
            // For now, we'll leave it empty
            _uiState.value = _uiState.value.copy(recentlyPlayed = emptyList())
        }
    }

    /**
     * Observe active sources from settings.
     */
    private fun observeActiveSources() {
        viewModelScope.launch {
            repository.activeSources.collectLatest { sources ->
                _uiState.value = _uiState.value.copy(activeSources = sources)
            }
        }
    }

    /**
     * Observe playlists from database.
     */
    private fun observePlaylists() {
        viewModelScope.launch {
            repository.getPlaylists().collectLatest { playlists ->
                _uiState.value = _uiState.value.copy(playlists = playlists)
            }
        }
    }

    /**
     * Create a new playlist.
     */
    fun createPlaylist(name: String, description: String? = null) {
        viewModelScope.launch {
            repository.createPlaylist(name, description)
        }
    }

    /**
     * Toggle a music source on/off.
     */
    fun toggleSource(source: SourceType) {
        viewModelScope.launch {
            repository.toggleSource(source)
        }
    }

    /**
     * Refresh all data.
     */
    fun refresh() {
        loadData()
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
