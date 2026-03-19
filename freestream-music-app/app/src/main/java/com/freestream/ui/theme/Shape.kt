package com.freestream.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for FreeStream Music App.
 * 
 * Defines corner radius values for various UI components.
 */

/**
 * Material 3 shapes for the app.
 */
val AppShapes = Shapes(
    // Small shapes: buttons, chips, small cards
    small = RoundedCornerShape(4.dp),
    
    // Medium shapes: cards, dialogs
    medium = RoundedCornerShape(8.dp),
    
    // Large shapes: bottom sheets, expanded cards
    large = RoundedCornerShape(16.dp)
)

// ===== Custom Shapes =====

/**
 * Shape for track list items.
 */
val TrackItemShape = RoundedCornerShape(8.dp)

/**
 * Shape for album artwork.
 */
val AlbumArtShape = RoundedCornerShape(8.dp)

/**
 * Shape for large album artwork (player screen).
 */
val LargeAlbumArtShape = RoundedCornerShape(12.dp)

/**
 * Shape for buttons.
 */
val ButtonShape = RoundedCornerShape(24.dp)

/**
 * Shape for filter chips.
 */
val ChipShape = RoundedCornerShape(16.dp)

/**
 * Shape for text fields.
 */
val TextFieldShape = RoundedCornerShape(8.dp)

/**
 * Shape for bottom sheets.
 */
val BottomSheetShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/**
 * Shape for dialogs.
 */
val DialogShape = RoundedCornerShape(16.dp)

/**
 * Shape for mini player.
 */
val MiniPlayerShape = RoundedCornerShape(
    topStart = 12.dp,
    topEnd = 12.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

/**
 * Shape for cards.
 */
val CardShape = RoundedCornerShape(12.dp)

/**
 * Shape for playlist items.
 */
val PlaylistItemShape = RoundedCornerShape(8.dp)

/**
 * Shape for source badges.
 */
val BadgeShape = RoundedCornerShape(4.dp)

/**
 * Shape for progress indicators.
 */
val ProgressIndicatorShape = RoundedCornerShape(2.dp)

/**
 * Shape for sliders.
 */
val SliderShape = RoundedCornerShape(4.dp)

/**
 * Shape for notification artwork.
 */
val NotificationArtShape = RoundedCornerShape(4.dp)

/**
 * Shape for search bar.
 */
val SearchBarShape = RoundedCornerShape(24.dp)

/**
 * Shape for FAB (Floating Action Button).
 */
val FabShape = RoundedCornerShape(16.dp)

/**
 * Shape for menu items.
 */
val MenuItemShape = RoundedCornerShape(0.dp)

/**
 * Shape for tabs.
 */
val TabShape = RoundedCornerShape(0.dp)

/**
 * Shape for dividers.
 */
val DividerShape = RoundedCornerShape(0.dp)
