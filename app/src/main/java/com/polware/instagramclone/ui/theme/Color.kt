package com.polware.instagramclone.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Blue_bg = Color(0xFF03396c)
val Dark_pink = Color(0xFFdec3c3)
val Medium_pink = Color(0xFFe7d3d3)
val Light_pink = Color(0xFFf0e4e4)
val LightGray = Color(0xFFFCFCFC)
val BlueBackground = Color(0xFFe7eff6)

val Colors.commentBackgroundColor: Color
    @Composable
    get() = if (isLight) BlueBackground else LightGray
