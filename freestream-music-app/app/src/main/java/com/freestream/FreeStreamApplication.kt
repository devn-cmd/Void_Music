package com.freestream

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.freestream.util.ApiKeys
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for FreeStream Music App.
 * 
 * This is the entry point of the application. It:
 * - Initializes Hilt for dependency injection
 * - Configures Coil image loading
 * - Validates API keys on startup
 * - Sets up global application state
 */
@HiltAndroidApp
class FreeStreamApplication : Application(), ImageLoaderFactory {

    companion object {
        private const val TAG = "FreeStreamApp"
        
        lateinit var instance: FreeStreamApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Log.d(TAG, "FreeStream Application starting...")
        
        // Validate API keys
        validateApiKeys()
        
        // Initialize Coil image loader
        // (done via ImageLoaderFactory interface)
        
        Log.d(TAG, "FreeStream Application initialized successfully")
    }

    /**
     * Validate that required API keys are configured.
     * Logs warnings if keys are missing.
     */
    private fun validateApiKeys() {
        if (!ApiKeys.areKeysConfigured()) {
            val missingKeys = ApiKeys.getMissingKeys()
            Log.w(TAG, "WARNING: Missing API keys: $missingKeys")
            Log.w(TAG, "Please configure your API keys in ApiKeys.kt")
            Log.w(TAG, "- Jamendo: https://devportal.jamendo.com/")
            Log.w(TAG, "- Freesound: https://freesound.org/apiv2/apply/")
        } else {
            Log.d(TAG, "All required API keys are configured")
        }
    }

    /**
     * Provide Coil ImageLoader configuration.
     * Configures caching for optimal image loading performance.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // Memory cache for fast in-memory image storage
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Use 25% of available memory
                    .build()
            }
            // Disk cache for persistent image storage
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024) // 100 MB
                    .build()
            }
            // Enable disk caching for all requests
            .diskCachePolicy(CachePolicy.ENABLED)
            // Respect cache headers but use cache when offline
            .respectCacheHeaders(true)
            // Crossfade animation for smooth image transitions
            .crossfade(true)
            // Enable logging in debug builds
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}

/**
 * Debug logger for Coil (only used in debug builds).
 */
private class DebugLogger : coil.util.Logger {
    override var level: coil.util.Logger.Level = coil.util.Logger.Level.Debug
    
    override fun log(tag: String, priority: Int, message: String?, throwable: Throwable?) {
        Log.println(priority, "Coil.$tag", message ?: "")
        throwable?.let { Log.println(priority, "Coil.$tag", Log.getStackTraceString(it)) }
    }
}
