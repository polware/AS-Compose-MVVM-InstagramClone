package com.polware.instagramclone.data.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomMenuItem(val icon: ImageVector, val navDestination: DestinationScreen, val title: String) {
    MAIN(Icons.Outlined.Home, DestinationScreen.Main, "Main"),
    SEARCH(Icons.Outlined.Search, DestinationScreen.Search, "Search"),
    POST(Icons.Outlined.List, DestinationScreen.Post, "Posts")
}