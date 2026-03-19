package com.freestream.data.local.database

import androidx.room.TypeConverter
import com.freestream.data.model.SourceType
import com.freestream.data.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Type converters for Room database.
 * 
 * Room only supports primitive types directly. These converters
 * allow storing complex types like Lists, custom objects, and enums.
 */
class Converters {

    private val gson = Gson()

    // ===== List<String> Converters =====
    
    /**
     * Convert List<String> to JSON string for storage.
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * Convert JSON string back to List<String>.
     */
    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    // ===== Date Converters =====
    
    /**
     * Convert Date to Long timestamp.
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    /**
     * Convert Long timestamp to Date.
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    // ===== SourceType Converters =====
    
    /**
     * Convert SourceType enum to String.
     */
    @TypeConverter
    fun fromSourceType(sourceType: SourceType?): String? {
        return sourceType?.name
    }

    /**
     * Convert String to SourceType enum.
     */
    @TypeConverter
    fun toSourceType(value: String?): SourceType? {
        return value?.let { SourceType.valueOf(it) }
    }

    // ===== Track Converters =====
    
    /**
     * Convert Track object to JSON string.
     */
    @TypeConverter
    fun fromTrack(track: Track?): String? {
        return track?.let { gson.toJson(it) }
    }

    /**
     * Convert JSON string to Track object.
     */
    @TypeConverter
    fun toTrack(value: String?): Track? {
        if (value == null) return null
        return gson.fromJson(value, Track::class.java)
    }

    // ===== Int List Converters (for flexibility) =====
    
    /**
     * Convert List<Int> to JSON string.
     */
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * Convert JSON string to List<Int>.
     */
    @TypeConverter
    fun toIntList(value: String?): List<Int> {
        if (value == null) return emptyList()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}
