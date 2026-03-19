package com.freestream.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.freestream.data.model.SourceType
import com.freestream.util.ApiKeys

/**
 * Settings screen for FreeStream.
 * 
 * Allows users to configure:
 * - Active music sources
 * - Appearance (dark theme)
 * - Playback settings
 * - Notifications
 * - Data management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    var showClearDataDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // API Keys Status
            if (!ApiKeys.areKeysConfigured()) {
                ApiKeysWarning()
            }

            // Music Sources Section
            SettingsSectionHeader(
                title = "Music Sources",
                icon = Icons.Default.MusicNote
            )

            SourceToggleItem(
                source = SourceType.JAMENDO,
                isEnabled = true, // TODO: Get from settings
                onToggle = { }
            )

            SourceToggleItem(
                source = SourceType.FREESOUND,
                isEnabled = true,
                onToggle = { }
            )

            SourceToggleItem(
                source = SourceType.ARCHIVE,
                isEnabled = true,
                onToggle = { }
            )

            SourceToggleItem(
                source = SourceType.CCMIXTER,
                isEnabled = true,
                onToggle = { }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Appearance Section
            SettingsSectionHeader(
                title = "Appearance",
                icon = Icons.Default.Palette
            )

            SettingsSwitchItem(
                title = "Dark Theme",
                subtitle = "Use dark color scheme",
                isChecked = true, // TODO: Get from settings
                onCheckedChange = { }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Notifications Section
            SettingsSectionHeader(
                title = "Notifications",
                icon = Icons.Default.Notifications
            )

            SettingsSwitchItem(
                title = "Show Notifications",
                subtitle = "Display playback notifications",
                isChecked = true,
                onCheckedChange = { }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Data Section
            SettingsSectionHeader(
                title = "Data",
                icon = Icons.Default.Info
            )

            SettingsActionItem(
                title = "Clear Cache",
                subtitle = "Free up storage space",
                onClick = { /* Clear cache */ }
            )

            SettingsActionItem(
                title = "Clear All Data",
                subtitle = "Delete playlists, favorites, and history",
                onClick = { showClearDataDialog = true }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // About Section
            SettingsSectionHeader(
                title = "About",
                icon = Icons.Default.Info
            )

            SettingsInfoItem(
                title = "Version",
                value = "1.0.0"
            )

            SettingsActionItem(
                title = "Open Source Licenses",
                subtitle = "View third-party licenses",
                onClick = { /* Show licenses */ }
            )

            SettingsActionItem(
                title = "Privacy Policy",
                subtitle = "Read our privacy policy",
                onClick = { /* Open privacy policy */ }
            )
        }

        // Clear Data Confirmation Dialog
        if (showClearDataDialog) {
            AlertDialog(
                onDismissRequest = { showClearDataDialog = false },
                title = { Text("Clear All Data") },
                text = {
                    Text(
                        "This will delete all your playlists, favorites, and history. " +
                        "This action cannot be undone."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // TODO: Clear all data
                            showClearDataDialog = false
                        }
                    ) {
                        Text("Clear", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDataDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ApiKeysWarning() {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "API Keys Not Configured",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "Please add your Jamendo Client ID and Freesound API Key in ApiKeys.kt",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SourceToggleItem(
    source: SourceType,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    ListItem(
        headlineContent = { Text(source.displayName) },
        supportingContent = { Text(source.description) },
        leadingContent = {
            androidx.compose.foundation.Canvas(
                modifier = Modifier.size(12.dp)
            ) {
                drawCircle(color = source.color)
            }
        },
        trailingContent = {
            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() }
            )
        },
        modifier = Modifier.clickable { onToggle() }
    )
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
private fun SettingsActionItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun SettingsInfoItem(
    title: String,
    value: String
) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = { Text(value) }
    )
}
