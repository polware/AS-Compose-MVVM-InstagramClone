package com.polware.instagramclone.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.polware.instagramclone.R

@Composable
fun UserImageCard(userImage: String?, modifier: Modifier = Modifier
    .padding(8.dp)
    .size(64.dp)) {

    Card(shape = CircleShape, modifier = modifier) {
        if (userImage.isNullOrEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "",
                colorFilter = ColorFilter.tint(Color.Gray)
            )
        }
        else {
            CommonImage(data = userImage, modifier = Modifier.wrapContentSize())
        }
    }
}