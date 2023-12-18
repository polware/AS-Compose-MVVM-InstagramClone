package com.polware.instagramclone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.polware.instagramclone.R
import com.polware.instagramclone.data.model.NavigationParam
import com.polware.instagramclone.data.model.PostData
import com.polware.instagramclone.data.util.Constants.ARGS_KEY
import com.polware.instagramclone.data.util.DestinationScreen
import com.polware.instagramclone.navigateTo
import com.polware.instagramclone.ui.components.BottomNavigationBar
import com.polware.instagramclone.ui.components.CommonImage
import com.polware.instagramclone.ui.components.LikeClickAnimation
import com.polware.instagramclone.ui.components.ProgressSpinner
import com.polware.instagramclone.ui.components.UserImageCard
import com.polware.instagramclone.viewmodel.IgViewModel
import kotlinx.coroutines.delay

@Composable
fun MainScreen(navController: NavController, viewModel: IgViewModel) {
    viewModel.clearSearchedPost()
    val userDataLoading = viewModel.inProgress.value
    val userData = viewModel.userData.value
    val personalizedFeed = viewModel.postsFeed.value
    val feedLoading = viewModel.feedProgress.value

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White)
        ) {
            UserImageCard(userImage = userData?.imageUrl)
            Text(
                text = "@${userData?.userName}",
                modifier = Modifier
                    .padding(start = 1.dp)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
        Column(modifier = Modifier
            .wrapContentSize()
            .background(colorResource(id = R.color.gray_bg)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (personalizedFeed.isEmpty()) {
                Row(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "You are not following any user!",
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            else {
                PostsList(
                    posts = personalizedFeed,
                    modifier = Modifier.weight(1f),
                    loading = userDataLoading or feedLoading,
                    navController = navController,
                    viewModel = viewModel,
                    userId = userData?.userId ?: ""
                )
            }
            BottomNavigationBar(navController = navController)
        }
    }
}

@Composable
fun PostsList(posts: List<PostData>, modifier: Modifier, loading: Boolean, navController:
NavController, viewModel: IgViewModel, userId: String) {

    Box(
        modifier = modifier
    ) {
        if (loading)
            ProgressSpinner()

        LazyColumn {
            items(posts) {
                Post(post = it, userId = userId, viewModel = viewModel) {
                    navigateTo(
                        navController = navController,
                        destination = DestinationScreen.PostDetails,
                        NavigationParam(ARGS_KEY, it)
                        )
                }
            }
        }
    }
}

@Composable
fun Post(post: PostData, userId: String, viewModel: IgViewModel, onPostClick: () -> Unit) {
    val likeAnimation = remember { mutableStateOf(false) }
    val dislikeAnimation = remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(corner = CornerSize(4.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    CommonImage(
                        data = post.userImage,
                        modifier = Modifier.wrapContentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = post.username ?: "",
                    modifier = Modifier.padding(4.dp)
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val myModifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                /** Double clicking on the post is equivalent to "like" or "dislike" */
                                if (post.likes?.contains(userId) == true) {
                                    dislikeAnimation.value = true
                                } else {
                                    likeAnimation.value = true
                                }
                                viewModel.gestureToLikePost(post)
                            },
                            onTap = {
                                /** One click on the post opens its details */
                                onPostClick.invoke()
                            }
                        )
                    }
                CommonImage(
                    data = post.postImage,
                    modifier = myModifier,
                    contentScale = ContentScale.FillWidth
                )
                if (likeAnimation.value) {
                    /** Calls a Coroutine during a composition */
                    LaunchedEffect(true) {
                        delay(1000L)
                        likeAnimation.value = false
                    }
                    LikeClickAnimation()
                }
                if (dislikeAnimation.value) {
                    LaunchedEffect(true) {
                        delay(1000L)
                        dislikeAnimation.value = false
                    }
                    LikeClickAnimation(false)
                }
            }
        }
    }
}