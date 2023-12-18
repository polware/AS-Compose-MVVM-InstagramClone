package com.polware.instagramclone.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.polware.instagramclone.R
import com.polware.instagramclone.data.model.PostData
import com.polware.instagramclone.data.util.DestinationScreen
import com.polware.instagramclone.ui.components.CommonImage
import com.polware.instagramclone.ui.components.CustomDivider
import com.polware.instagramclone.viewmodel.IgViewModel

@Composable
fun PostDetails(navController: NavController, viewModel: IgViewModel, post: PostData) {
    val comments = viewModel.comments.value
    LaunchedEffect(key1 = Unit) {
        viewModel.getPostComment(post.postId)
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(8.dp)
    ) {
        Text(
            text = "Back",
            modifier = Modifier.clickable {
                navController.popBackStack()
                }
        )
        CustomDivider()
        PostDisplay(navController = navController, viewModel = viewModel, post = post, numberComments = comments.size)
    }

}

@Composable
fun PostDisplay(navController: NavController, viewModel: IgViewModel, post: PostData, numberComments: Int) {
    val userData = viewModel.userData.value
    
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(shape = CircleShape, modifier = Modifier
                .padding(8.dp)
                .size(32.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = post.userImage),
                    contentDescription = ""
                )
            }
            Text(text = post.username ?: "")
            Text(text = ".", modifier = Modifier.padding(8.dp))
            if (userData?.userId == post.userId) {
                // TODO:
            }
            else if (userData?.following?.contains(post.userId) == true) {
                Text(
                    text = "Following",
                    color = Color.Gray,
                    modifier = Modifier.clickable {
                        viewModel.onFollowClick(post.userId!!)
                    }
                )
            }
            else {
                Text(
                    text = "Follow",
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        viewModel.onFollowClick(post.userId!!)
                    }
                )
            }
        }
    }
    Box(modifier = Modifier.height(160.dp)) {
        val myModifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 160.dp)
        CommonImage(data = post.postImage, modifier = myModifier, contentScale = ContentScale.FillWidth)
    }
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_favorite),
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(Color.Red)
        )
        Text(
            text = " ${post.likes?.size ?: 0} likes",
            modifier = Modifier.padding(start = 0.dp)
        )
    }
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = post.username ?: "",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = post.postDescription ?: "",
            modifier = Modifier.padding(start = 8.dp)
        )
    }
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = "$numberComments comment(s)",
            color = Color.Gray,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable {
                    post.postId?.let {
                        navController.navigate(DestinationScreen.Comments.createRoute(it))
                    }
                }
        )
    }

}