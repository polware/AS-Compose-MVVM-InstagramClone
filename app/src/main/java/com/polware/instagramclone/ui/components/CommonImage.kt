package com.polware.instagramclone.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.polware.instagramclone.R

@Composable
fun CommonImage(data: String?, modifier: Modifier,
                contentScale: ContentScale = ContentScale.Crop) {

    ProgressSpinner()
    AsyncImage(
        model = if (data.isNullOrEmpty()) R.drawable.unavailable_image else data,
        contentDescription = "",
        modifier = modifier,
        contentScale = contentScale
    )
}