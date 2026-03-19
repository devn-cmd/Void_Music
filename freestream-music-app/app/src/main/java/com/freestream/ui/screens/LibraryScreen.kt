package com.freestream.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.freestream.data.model.Playlist
import com.freestream.data.model.Track
import com.freestream.ui.components.TrackListItem
import com.freestream.ui.viewmodel.LibraryViewModel

/**
 * Library screen for FreeStream.
 * 
 * Features tabs for:
 * - Playlists (with create FAB)
 * - Favorites
 * - History
 * - Downloads
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onPlaylistClick: (String) -> Unit,
    onTrackClick: (Track) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var newPlaylistName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Library",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            if (uiState.selectedTab == LibraryViewModel.LibraryTab.PLAYLISTS) {
                FloatingActionButton(
                    onClick = { viewModel.showCreatePlaylistDialog() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Playlist"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal
            ) {
                LibraryViewModel.LibraryTab.values().forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = { Text(tab.name.capitalize()) },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    LibraryViewModel.LibraryTab.PLAYLISTS -> Icons.Default.PlaylistPlay
                                    LibraryViewModel.LibraryTab.FAVORITES -> Icons.Default.Favorite
                                    LibraryViewModel.LibraryTab.HISTORY -> Icons.Default.History
                                    LibraryViewModel.LibraryTab.DOWNLOADS -> Icons.Default.PlaylistPlay
                                },
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }

            // Content
            when (uiState.selectedTab) {
                LibraryViewModel.LibraryTab.PLAYLISTS -> {
                    PlaylistsTab(
                        playlists = uiState.playlists,
                        onPlaylistClick = onPlaylistClick
                    )
                }
                LibraryViewModel.LibraryTab.FAVORITES -> {
                    FavoritesTab(
                        favorites = uiState.favorites,
                        onTrackClick = onTrackClick,
                        onRemoveFavorite = { track ->
                            viewModel.removeFromFavorites(track.id)
                        }
                    )
                }
                LibraryViewModel.LibraryTab.HISTORY -> {
                    HistoryTab(
                        history = uiState.history,
                        onClearHistory = { viewModel.clearHistory() }
                    )
                }
                LibraryViewModel.LibraryTab.DOWNLOADS -> {
                    DownloadsTab()
                }
            }
        }

        // Create Playlist Dialog
        if (uiState.showCreatePlaylistDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideCreatePlaylistDialog() },
                title = { Text("Create Playlist") },
                text = {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("Playlist Name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.createPlaylist(newPlaylistName)
                            newPlaylistName = ""
                        },
                        enabled = newPlaylistName.isNotBlank()
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideCreatePlaylistDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun PlaylistsTab(
    playlists: List<Playlist>,
    onPlaylistClick: (String) -> Unit
) {
    if (playlists.isEmpty()) {
        EmptyTabContent(
            message = "No playlists yet",
            subMessage = "Tap the + button to create your first playlist"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(playlists) { playlist ->
                PlaylistCard(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FavoritesTab(
    favorites: List<Track>,
    onTrackClick: (Track) -> Unit,
    onRemoveFavorite: (Track) -> Unit
) {
    if (favorites.isEmpty()) {
        EmptyTabContent(
            message = "No favorites yet",
            subMessage = "Tap the heart icon on any track to add it here"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(favorites) { track ->
                TrackListItem(
                    track = track,
                    onClick = { onTrackClick(track) },
                    onMoreClick = { onRemoveFavorite(track) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun HistoryTab(
    history: List<com.freestream.data.local.entity.HistoryEntryEntity>,
    onClearHistory: () -> Unit
) {
    if (history.isEmpty()) {
        EmptyTabContent(
            message = "No playback history",
            subMessage = "Tracks you play will appear here"
        )
    } else {
        Column {
            TextButton(
                onClick = onClearHistory,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp)
            ) {
                Text("Clear History")
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(history) { entry ->
                    HistoryListItem(entry = entry)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun DownloadsTab() {
    EmptyTabContent(
        message = "Downloads coming soon",
        subMessage = "This feature will be available in a future update"
    )
}

@Composable
private fun EmptyTabContent(
    message: String,
    subMessage: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (!playlist.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = playlist.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${playlist.trackCount} tracks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryListItem(
    entry: com.freestream.data.local.entity.HistoryEntryEntity
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Track ID: ${entry.trackId}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Played: ${java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault()).format(java.util.Date(entry.playedAt))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${entry.completionPercentage}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun String.capitalize(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
