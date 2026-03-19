package com.freestream.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freestream.data.model.Playlist
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
 * ViewModel for the Library screen.
 * 
 * Manages:
 * - User playlists (CRUD operations)
 * - Favorite tracks
 * - Playback history
 * - Downloads (if implemented)
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    // ===== UI State =====
    
    data class LibraryUiState(
        val selectedTab: LibraryTab = LibraryTab.PLAYLISTS,
        val playlists: List<Playlist> = emptyList(),
        val favorites: List<Track> = emptyList(),
        val history: List<com.freestream.data.local.entity.HistoryEntryEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val showCreatePlaylistDialog: Boolean = false
    )

    enum class LibraryTab {
        PLAYLISTS,
        FAVORITES,
        HISTORY,
        DOWNLOADS
    }

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        observePlaylists()
        observeFavorites()
        observeHistory()
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
     * Observe favorites from database.
     */
    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collectLatest { favorites ->
                _uiState.value = _uiState.value.copy(favorites = favorites)
            }
        }
    }

    /**
     * Observe history from database.
     */
    private fun observeHistory() {
        viewModelScope.launch {
            repository.getHistory(100).collectLatest { history ->
                _uiState.value = _uiState.value.copy(history = history)
            }
        }
    }

    /**
     * Select a tab.
     */
    fun selectTab(tab: LibraryTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    /**
     * Create a new playlist.
     */
    fun createPlaylist(name: String, description: String? = null) {
        if (name.isBlank()) return
        
        viewModelScope.launch {
            repository.createPlaylist(name, description)
            _uiState.value = _uiState.value.copy(showCreatePlaylistDialog = false)
        }
    }

    /**
     * Delete a playlist.
     */
    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    /**
     * Rename a playlist.
     */
    fun renamePlaylist(playlistId: String, newName: String) {
        viewModelScope.launch {
            repository.renamePlaylist(playlistId, newName)
        }
    }

    /**
     * Toggle playlist favorite status.
     */
    fun togglePlaylistFavorite(playlistId: String) {
        viewModelScope.launch {
            repository.togglePlaylistFavorite(playlistId)
        }
    }

    /**
     * Add a track to a playlist.
     */
    fun addToPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch {
            repository.addToPlaylist(playlistId, trackId)
        }
    }

    /**
     * Remove a track from a playlist.
     */
    fun removeFromPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch {
            repository.removeFromPlaylist(playlistId, trackId)
        }
    }

    /**
     * Toggle favorite status of a track.
     */
    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            repository.toggleFavorite(track)
        }
    }

    /**
     * Remove a track from favorites.
     */
    fun removeFromFavorites(trackId: String) {
        viewModelScope.launch {
            repository.removeFromFavorites(trackId)
        }
    }

    /**
     * Clear playback history.
     */
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    /**
     * Show create playlist dialog.
     */
    fun showCreatePlaylistDialog() {
        _uiState.value = _uiState.value.copy(showCreatePlaylistDialog = true)
    }

    /**
     * Hide create playlist dialog.
     */
    fun hideCreatePlaylistDialog() {
        _uiState.value = _uiState.value.copy(showCreatePlaylistDialog = false)
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
