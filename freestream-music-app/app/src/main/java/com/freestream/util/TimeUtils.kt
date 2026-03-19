package com.freestream.util

/**
 * Utility functions for time and duration formatting.
 */
object TimeUtils {
    
    /**
     * Format duration in milliseconds to a human-readable string.
     * 
     * Formats:
     * - Less than 1 hour: "mm:ss"
     * - 1 hour or more: "h:mm:ss"
     * 
     * @param durationMs Duration in milliseconds
     * @return Formatted duration string
     */
    fun formatDuration(durationMs: Long): String {
        if (durationMs <= 0) return "0:00"
        
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
    
    /**
     * Format duration to a compact string.
     * 
     * @param durationMs Duration in milliseconds
     * @return Compact duration string (e.g., "3:45")
     */
    fun formatDurationCompact(durationMs: Long): String {
        return formatDuration(durationMs)
    }
    
    /**
     * Format duration to include hours even if zero.
     * Always returns "h:mm:ss" format.
     * 
     * @param durationMs Duration in milliseconds
     * @return Formatted duration string with hours
     */
    fun formatDurationWithHours(durationMs: Long): String {
        if (durationMs <= 0) return "0:00:00"
        
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return String.format("%d:%02d:%02d", hours, minutes, seconds)
    }
    
    /**
     * Parse a duration string to milliseconds.
     * 
     * Supports formats:
     * - "mm:ss"
     * - "h:mm:ss"
     * 
     * @param durationStr Duration string
     * @return Duration in milliseconds, or 0 if parsing fails
     */
    fun parseDuration(durationStr: String?): Long {
        if (durationStr.isNullOrBlank()) return 0
        
        val parts = durationStr.split(":").mapNotNull { it.toIntOrNull() }
        
        return when (parts.size) {
            2 -> { // mm:ss
                val minutes = parts[0]
                val seconds = parts[1]
                (minutes * 60 + seconds) * 1000L
            }
            3 -> { // h:mm:ss
                val hours = parts[0]
                val minutes = parts[1]
                val seconds = parts[2]
                (hours * 3600 + minutes * 60 + seconds) * 1000L
            }
            else -> 0
        }
    }
    
    /**
     * Format a timestamp to a relative time string.
     * 
     * @param timestamp Timestamp in milliseconds
     * @return Relative time string (e.g., "2 hours ago")
     */
    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> {
                val minutes = diff / 60_000
                "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            }
            diff < 86_400_000 -> {
                val hours = diff / 3_600_000
                "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            }
            diff < 604_800_000 -> {
                val days = diff / 86_400_000
                "$days ${if (days == 1L) "day" else "days"} ago"
            }
            diff < 2_592_000_000 -> {
                val weeks = diff / 604_800_000
                "$weeks ${if (weeks == 1L) "week" else "weeks"} ago"
            }
            else -> {
                val months = diff / 2_592_000_000
                "$months ${if (months == 1L) "month" else "months"} ago"
            }
        }
    }
    
    /**
     * Format a timestamp to a date string.
     * 
     * @param timestamp Timestamp in milliseconds
     * @return Date string (e.g., "Jan 15, 2024")
     */
    fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Format a timestamp to a date and time string.
     * 
     * @param timestamp Timestamp in milliseconds
     * @return Date and time string (e.g., "Jan 15, 2024 3:45 PM")
     */
    fun formatDateTime(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("MMM d, yyyy h:mm a", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Calculate completion percentage.
     * 
     * @param positionMs Current position in milliseconds
     * @param durationMs Total duration in milliseconds
     * @return Completion percentage (0-100)
     */
    fun calculateCompletionPercentage(positionMs: Long, durationMs: Long): Int {
        if (durationMs <= 0) return 0
        return ((positionMs * 100) / durationMs).toInt().coerceIn(0, 100)
    }
    
    /**
     * Format a duration in a human-friendly way.
     * 
     * @param durationMs Duration in milliseconds
     * @return Human-friendly string (e.g., "3 minutes", "1 hour 30 minutes")
     */
    fun formatDurationHumanFriendly(durationMs: Long): String {
        if (durationMs <= 0) return "0 minutes"
        
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        
        return buildString {
            if (hours > 0) {
                append("$hours ${if (hours == 1L) "hour" else "hours"}")
                if (minutes > 0) append(" ")
            }
            if (minutes > 0 || hours == 0L) {
                append("$minutes ${if (minutes == 1L) "minute" else "minutes"}")
            }
        }
    }
}
