package com.freestream.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.Color

/**
 * Extension functions for FreeStream application.
 * 
 * Provides convenient extensions for common operations.
 */

// ===== Context Extensions =====

/**
 * Show a short toast message.
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Show a long toast message.
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Open a URL in Chrome Custom Tabs.
 * Falls back to default browser if Custom Tabs not available.
 */
fun Context.openUrl(url: String) {
    try {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    } catch (e: Exception) {
        // Fallback to default browser
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}

/**
 * Share text via system share dialog.
 */
fun Context.shareText(text: String, title: String = "Share") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, title))
}

// ===== String Extensions =====

/**
 * Truncate string to specified length with ellipsis.
 */
fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (length > maxLength) {
        take(maxLength - ellipsis.length) + ellipsis
    } else {
        this
    }
}

/**
 * Capitalize first letter of each word.
 */
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase() else it.toString() 
        }
    }
}

/**
 * Check if string is a valid URL.
 */
fun String.isValidUrl(): Boolean {
    return try {
        val uri = Uri.parse(this)
        uri.scheme != null && (uri.scheme == "http" || uri.scheme == "https")
    } catch (e: Exception) {
        false
    }
}

// ===== Long Extensions =====

/**
 * Format milliseconds to duration string.
 */
fun Long.toDurationString(): String {
    return TimeUtils.formatDuration(this)
}

/**
 * Format timestamp to relative time.
 */
fun Long.toRelativeTime(): String {
    return TimeUtils.formatRelativeTime(this)
}

/**
 * Format timestamp to date string.
 */
fun Long.toDateString(): String {
    return TimeUtils.formatDate(this)
}

// ===== Int Extensions =====

/**
 * Format as count with K/M suffix.
 */
fun Int.formatCount(): String {
    return when {
        this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format("%.1fK", this / 1_000.0)
        else -> toString()
    }
}

/**
 * Convert seconds to milliseconds.
 */
fun Int.secondsToMs(): Long {
    return this * 1000L
}

// ===== Float Extensions =====

/**
 * Format as percentage string.
 */
fun Float.toPercentage(decimals: Int = 0): String {
    return String.format("%.${decimals}f%%", this * 100)
}

/**
 * Coerce to valid volume range (0.0 to 1.0).
 */
fun Float.coerceToVolume(): Float {
    return coerceIn(0f, 1f)
}

// ===== List Extensions =====

/**
 * Shuffle list and return new shuffled list.
 */
fun <T> List<T>.shuffled(): List<T> {
    return toMutableList().apply { shuffle() }
}

/**
 * Move element from one index to another.
 */
fun <T> MutableList<T>.move(fromIndex: Int, toIndex: Int) {
    if (fromIndex in indices && toIndex in indices) {
        val element = removeAt(fromIndex)
        add(toIndex, element)
    }
}

/**
 * Get random element or null if empty.
 */
fun <T> List<T>.randomOrNull(): T? {
    return if (isNotEmpty()) random() else null
}

// ===== Color Extensions =====

/**
 * Create color with alpha.
 */
fun Color.withAlpha(alpha: Float): Color {
    return copy(alpha = alpha.coerceIn(0f, 1f))
}

/**
 * Lighten color by specified amount.
 */
fun Color.lighten(amount: Float = 0.2f): Color {
    return copy(
        red = (red + amount).coerceAtMost(1f),
        green = (green + amount).coerceAtMost(1f),
        blue = (blue + amount).coerceAtMost(1f)
    )
}

/**
 * Darken color by specified amount.
 */
fun Color.darken(amount: Float = 0.2f): Color {
    return copy(
        red = (red - amount).coerceAtLeast(0f),
        green = (green - amount).coerceAtLeast(0f),
        blue = (blue - amount).coerceAtLeast(0f)
    )
}

// ===== Boolean Extensions =====

/**
 * Toggle boolean value.
 */
fun Boolean.toggle(): Boolean = !this

// ===== Result Extensions =====

/**
 * Get success data or null.
 */
fun <T> com.freestream.data.remote.source.Result<T>.getOrNull(): T? {
    return when (this) {
        is com.freestream.data.remote.source.Result.Success -> data
        is com.freestream.data.remote.source.Result.Error -> null
    }
}

/**
 * Get success data or default.
 */
fun <T> com.freestream.data.remote.source.Result<T>.getOrDefault(default: T): T {
    return when (this) {
        is com.freestream.data.remote.source.Result.Success -> data
        is com.freestream.data.remote.source.Result.Error -> default
    }
}

/**
 * Execute action on success.
 */
fun <T> com.freestream.data.remote.source.Result<T>.onSuccess(action: (T) -> Unit): com.freestream.data.remote.source.Result<T> {
    if (this is com.freestream.data.remote.source.Result.Success) action(data)
    return this
}

/**
 * Execute action on error.
 */
fun <T> com.freestream.data.remote.source.Result<T>.onError(action: (com.freestream.data.remote.source.Result.Error) -> Unit): com.freestream.data.remote.source.Result<T> {
    if (this is com.freestream.data.remote.source.Result.Error) action(this)
    return this
}
