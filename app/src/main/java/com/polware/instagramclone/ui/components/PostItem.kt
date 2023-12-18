package com.polware.instagramclone.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.polware.instagramclone.data.model.PostData
import com.polware.instagramclone.data.model.PostItemRow

@Composable
fun PostItem(isLoading: Boolean, postsLoading: Boolean, posts: List<PostData>,
             modifier: Modifier, onPostClick: (PostData) -> Unit) {

    val rows = arrayListOf<PostItemRow>()
    var currentRow = PostItemRow()

    if (postsLoading) {
        ProgressSpinner()
    }
    else if (posts.isEmpty()) {

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isLoading)
                Text(
                    text = "No posts available!",
                    fontWeight = FontWeight.Bold
                )
        }
    }
    else {
        LazyColumn(modifier = modifier) {
            rows.add(currentRow)
            for (post in posts) {
                if (currentRow.isFull()) {
                    currentRow = PostItemRow()
                    rows.add(currentRow)
                }
                currentRow.addRow(post = post)
            }
            items(items = rows) {
                PostsRow(item = it, onPostClick = onPostClick)
            }
        }
    }
}

@Composable
fun PostsRow(item: PostItemRow, onPostClick: (PostData) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)) {
        PostImage(
            imageUrl = item.post1?.postImage,
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dp, end = 2.dp)
                .clickable {
                    item.post1?.let {
                        onPostClick(it)
                    }
                }
        )
        PostImage(
            imageUrl = item.post2?.postImage,
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dp, end = 2.dp)
                .clickable {
                    item.post2?.let {
                        onPostClick(it)
                    }
                }
        )
        PostImage(
            imageUrl = item.post3?.postImage,
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dp, end = 2.dp)
                .clickable {
                    item.post3?.let {
                        onPostClick(it)
                    }
                }
        )
    }
}

@Composable
fun PostImage(imageUrl: String?, modifier: Modifier) {
    Box(modifier = modifier) {
        var newModifier = Modifier
            .padding(1.dp)
            .fillMaxSize()
        if (imageUrl == null) {
            newModifier = newModifier.clickable(enabled = false) { }
        }
        else {
            CommonImage(data = imageUrl, modifier = modifier, contentScale = ContentScale.Crop)
        }
    }
}