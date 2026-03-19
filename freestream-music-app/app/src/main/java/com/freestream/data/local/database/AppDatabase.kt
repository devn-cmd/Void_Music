package com.freestream.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.freestream.data.local.entity.FavoriteTrackEntity
import com.freestream.data.local.entity.HistoryEntryEntity
import com.freestream.data.local.entity.PlaylistEntity

/**
 * Room Database for FreeStream application.
 * 
 * This is the main database that stores:
 * - User-created playlists
 * - Favorite tracks (with full track data for offline access)
 * - Playback history
 * 
 * Database version: 1
 * Entities: PlaylistEntity, FavoriteTrackEntity, HistoryEntryEntity
 */
@Database(
    entities = [
        PlaylistEntity::class,
        FavoriteTrackEntity::class,
        HistoryEntryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * DAO for playlist operations.
     */
    abstract fun playlistDao(): PlaylistDao

    /**
     * DAO for favorite tracks operations.
     */
    abstract fun favoriteDao(): FavoriteDao

    /**
     * DAO for playback history operations.
     */
    abstract fun historyDao(): HistoryDao

    companion object {
        
        /**
         * Database file name.
         */
        const val DATABASE_NAME = "freestream_database"

        /**
         * Singleton instance of the database.
         * Uses @Volatile to ensure visibility across threads.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get the database instance.
         * Creates the database if it doesn't exist.
         * 
         * @param context Application context
         * @return Database instance
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { 
                    INSTANCE = it 
                }
            }
        }

        /**
         * Build the database.
         * 
         * @param context Application context
         * @return Built database
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                // Enable destructive migration during development
                // In production, implement proper migrations
                .fallbackToDestructiveMigration()
                // Enable WAL mode for better concurrent access
                .enableMultiInstanceInvalidation()
                // Add callbacks if needed
                // .addCallback(databaseCallback)
                .build()
        }

        /**
         * Destroy the database instance.
         * Useful for testing and when database needs to be reset.
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
