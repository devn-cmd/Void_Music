package com.freestream.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.freestream.data.local.database.AppDatabase
import com.freestream.data.local.database.FavoriteDao
import com.freestream.data.local.database.HistoryDao
import com.freestream.data.local.database.PlaylistDao
import com.freestream.data.remote.api.ArchiveApi
import com.freestream.data.remote.api.CcMixterApi
import com.freestream.data.remote.api.FreesoundApi
import com.freestream.data.remote.api.JamendoApi
import com.freestream.data.remote.source.ArchiveOrgSource
import com.freestream.data.remote.source.CcMixterSource
import com.freestream.data.remote.source.FreesoundSource
import com.freestream.data.remote.source.JamendoSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt Dependency Injection Module for FreeStream.
 * 
 * Provides all dependencies for the application:
 * - Retrofit API clients for all 4 music sources
 * - Room database and DAOs
 * - ExoPlayer instance
 * - Music source implementations
 * 
 * All dependencies are singletons (created once and reused).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ===== OkHttp Client =====
    
    /**
     * Provides the OkHttp client used by all Retrofit instances.
     * Configured with logging and timeouts.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (com.freestream.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ===== Jamendo API =====
    
    /**
     * Provides the Jamendo Retrofit API client.
     */
    @Provides
    @Singleton
    fun provideJamendoApi(okHttpClient: OkHttpClient): JamendoApi {
        return Retrofit.Builder()
            .baseUrl(JamendoApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JamendoApi::class.java)
    }

    /**
     * Provides the Jamendo music source implementation.
     */
    @Provides
    @Singleton
    fun provideJamendoSource(api: JamendoApi): JamendoSource {
        return JamendoSource(api)
    }

    // ===== Freesound API =====
    
    /**
     * Provides the Freesound Retrofit API client.
     */
    @Provides
    @Singleton
    fun provideFreesoundApi(okHttpClient: OkHttpClient): FreesoundApi {
        return Retrofit.Builder()
            .baseUrl(FreesoundApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FreesoundApi::class.java)
    }

    /**
     * Provides the Freesound music source implementation.
     */
    @Provides
    @Singleton
    fun provideFreesoundSource(api: FreesoundApi): FreesoundSource {
        return FreesoundSource(api)
    }

    // ===== Archive.org API =====
    
    /**
     * Provides the Archive.org Retrofit API client.
     */
    @Provides
    @Singleton
    fun provideArchiveApi(okHttpClient: OkHttpClient): ArchiveApi {
        return Retrofit.Builder()
            .baseUrl(ArchiveApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ArchiveApi::class.java)
    }

    /**
     * Provides the Archive.org music source implementation.
     */
    @Provides
    @Singleton
    fun provideArchiveSource(api: ArchiveApi): ArchiveOrgSource {
        return ArchiveOrgSource(api)
    }

    // ===== ccMixter API =====
    
    /**
     * Provides the ccMixter Retrofit API client.
     * Note: ccMixter uses HTTP, not HTTPS.
     */
    @Provides
    @Singleton
    fun provideCcMixterApi(okHttpClient: OkHttpClient): CcMixterApi {
        return Retrofit.Builder()
            .baseUrl(CcMixterApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CcMixterApi::class.java)
    }

    /**
     * Provides the ccMixter music source implementation.
     */
    @Provides
    @Singleton
    fun provideCcMixterSource(api: CcMixterApi): CcMixterSource {
        return CcMixterSource(api)
    }

    // ===== Room Database =====
    
    /**
     * Provides the Room database instance.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    /**
     * Provides the Playlist DAO.
     */
    @Provides
    @Singleton
    fun providePlaylistDao(database: AppDatabase): PlaylistDao {
        return database.playlistDao()
    }

    /**
     * Provides the Favorite DAO.
     */
    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    /**
     * Provides the History DAO.
     */
    @Provides
    @Singleton
    fun provideHistoryDao(database: AppDatabase): HistoryDao {
        return database.historyDao()
    }

    // ===== ExoPlayer =====
    
    /**
     * Provides the ExoPlayer instance for audio playback.
     * Configured with audio attributes for media playback.
     */
    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        
        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true) // Handle audio focus
            .setWakeMode(C.WAKE_MODE_NETWORK) // Keep device awake during playback
            .setHandleAudioBecomingNoisy(true) // Pause when headphones unplug
            .build()
    }
}
