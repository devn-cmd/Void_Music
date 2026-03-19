package com.freestream

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.freestream.service.PlaybackService
import com.freestream.ui.components.MiniPlayer
import com.freestream.ui.navigation.BottomNavItem
import com.freestream.ui.navigation.BottomNavigationBar
import com.freestream.ui.screens.HomeScreen
import com.freestream.ui.screens.LibraryScreen
import com.freestream.ui.screens.PlayerScreen
import com.freestream.ui.screens.SearchScreen
import com.freestream.ui.screens.SettingsScreen
import com.freestream.ui.theme.FreeStreamTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for FreeStream Music App.
 * 
 * This is the single activity entry point that hosts the entire app
 * using Jetpack Compose for UI and Navigation.
 * 
 * Responsibilities:
 * - Request runtime permissions (notifications on Android 13+)
 * - Bind to PlaybackService for media control
 * - Setup navigation between screens
 * - Display mini player when music is playing
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle permission result
        // Notifications will work if granted, or silently fail if denied
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission on Android 13+
        requestNotificationPermission()
        
        // Initialize MediaController
        initializeMediaController()
        
        setContent {
            FreeStreamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FreeStreamApp()
                }
            }
        }
    }

    /**
     * Request notification permission for Android 13+ (API 33).
     * Required for playback notifications.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale if needed
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    /**
     * Initialize Media3 MediaController for controlling playback.
     */
    private fun initializeMediaController() {
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                mediaController = controllerFuture?.get()
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}

/**
 * Main app composable with navigation.
 */
@Composable
fun FreeStreamApp() {
    val navController = rememberNavController()
    
    // Track current route for bottom nav highlighting
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Show mini player on all screens except full player
    val showMiniPlayer = currentRoute != Screen.Player.route
    
    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Player.route) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onTrackClick = { track ->
                        // Navigate to player with track
                        navController.navigate(Screen.Player.createRoute(track.id))
                    },
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }
            
            composable(Screen.Search.route) {
                SearchScreen(
                    onTrackClick = { track ->
                        navController.navigate(Screen.Player.createRoute(track.id))
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Library.route) {
                LibraryScreen(
                    onPlaylistClick = { playlistId ->
                        // Navigate to playlist detail
                        // navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                    },
                    onTrackClick = { track ->
                        navController.navigate(Screen.Player.createRoute(track.id))
                    }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(
                route = Screen.Player.route,
                arguments = Screen.Player.arguments
            ) { backStackEntry ->
                val trackId = backStackEntry.arguments?.getString("trackId")
                PlayerScreen(
                    trackId = trackId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Screen routes for navigation.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Library : Screen("library")
    data object Settings : Screen("settings")
    data object Player : Screen("player/{trackId}") {
        fun createRoute(trackId: String) = "player/$trackId"
        val arguments = listOf(
            androidx.navigation.navArgument("trackId") {
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    }
}
