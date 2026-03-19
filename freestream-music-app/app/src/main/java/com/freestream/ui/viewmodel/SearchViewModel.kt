package com.freestream.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freestream.data.model.SourceType
import com.freestream.data.model.Track
import com.freestream.data.repository.MusicRepository
import com.freestream.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Search screen.
 * 
 * Manages:
 * - Search query with debouncing
 * - Search results from all active sources
 * - Source filter selection
 * - Loading and error states
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    // ===== UI State =====
    
    data class SearchUiState(
        val query: String = "",
        val isLoading: Boolean = false,
        val results: List<Track> = emptyList(),
        val activeSources: Set<SourceType> = SourceType.values().toSet(),
        val selectedSource: SourceType? = null,
        val recentSearches: Set<String> = emptySet(),
        val error: String? = null,
        val hasSearched: Boolean = false
    )

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Debounced query flow for search
    private val _searchQuery = MutableStateFlow("")

    init {
        observeActiveSources()
        observeRecentSearches()
        setupSearchDebounce()
    }

    /**
     * Setup debounced search.
     * Searches 300ms after user stops typing.
     */
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(Constants.SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isNotBlank()) {
                        performSearch(query)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            results = emptyList(),
                            hasSearched = false
                        )
                    }
                }
        }
    }

    /**
     * Update search query.
     */
    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        _searchQuery.value = query
    }

    /**
     * Clear search query.
     */
    fun clearQuery() {
        _uiState.value = _uiState.value.copy(
            query = "",
            results = emptyList(),
            hasSearched = false
        )
        _searchQuery.value = ""
    }

    /**
     * Perform search across all active sources.
     */
    private suspend fun performSearch(query: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null,
            hasSearched = true
        )

        try {
            val results = repository.search(query)
            
            // Filter by selected source if any
            val filteredResults = if (_uiState.value.selectedSource != null) {
                results.filter { it.source == _uiState.value.selectedSource }
            } else {
                results
            }

            _uiState.value = _uiState.value.copy(
                results = filteredResults,
                isLoading = false
            )

            // Add to recent searches
            repository.settingsDataStore.addRecentSearch(query)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Search failed: ${e.message}",
                isLoading = false
            )
        }
    }

    /**
     * Search immediately (for recent searches or when submitting).
     */
    fun searchNow() {
        val query = _uiState.value.query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                performSearch(query)
            }
        }
    }

    /**
     * Select/deselect a source filter.
     */
    fun selectSource(source: SourceType?) {
        _uiState.value = _uiState.value.copy(selectedSource = source)
        // Re-filter current results
        viewModelScope.launch {
            if (_uiState.value.query.isNotBlank()) {
                performSearch(_uiState.value.query)
            }
        }
    }

    /**
     * Toggle a source on/off in settings.
     */
    fun toggleSource(source: SourceType) {
        viewModelScope.launch {
            repository.toggleSource(source)
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
     * Observe recent searches from settings.
     */
    private fun observeRecentSearches() {
        viewModelScope.launch {
            repository.settingsDataStore.recentSearches.collectLatest { searches ->
                _uiState.value = _uiState.value.copy(recentSearches = searches)
            }
        }
    }

    /**
     * Clear recent searches.
     */
    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.settingsDataStore.clearRecentSearches()
        }
    }

    /**
     * Use a recent search.
     */
    fun useRecentSearch(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        _searchQuery.value = query
        searchNow()
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
