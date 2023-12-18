package com.polware.instagramclone.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.polware.instagramclone.R
import com.polware.instagramclone.data.model.NavigationParam
import com.polware.instagramclone.data.util.Constants.ARGS_KEY
import com.polware.instagramclone.data.util.DestinationScreen
import com.polware.instagramclone.navigateTo
import com.polware.instagramclone.ui.components.BottomNavigationBar
import com.polware.instagramclone.ui.components.PostItem
import com.polware.instagramclone.ui.components.ProgressSpinner
import com.polware.instagramclone.ui.components.UserImageCard
import com.polware.instagramclone.viewmodel.IgViewModel

@Composable
fun PostScreen(navController: NavController, viewModel: IgViewModel) {
    viewModel.clearSearchedPost()
    val userData = viewModel.userData.value
    val isLoading = viewModel.inProgress.value
    val userName = if (userData?.userName == null) "" else "@${userData.userName}"
    val postsLoading = viewModel.refreshPostProgress.value
    val posts = viewModel.posts.value
    val followers = viewModel.followers.value

    val launcherNewPost = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ){
        uri ->
        uri?.let {
            val encodedUri = Uri.encode(it.toString())
            val route = DestinationScreen.NewPost.createRoute(encodedUri)
            navController.navigate(route)
        }
    }

    if (isLoading) {
        ProgressSpinner()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row {

                ProfileImage(userData?.imageUrl) {
                    launcherNewPost.launch("image/*")
                }
                Text(
                    text = "${posts.size}\nPosts",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$followers\nFollowers",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${userData?.following?.size ?: 0}\nFollowing",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier= Modifier.padding(8.dp)) {
                Text(
                    text = userData?.name ?: "",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userName
                )
                Text(
                    text = userData?.bio ?: ""
                )
            }
            OutlinedButton(
                onClick = {
                    navigateTo(navController, DestinationScreen.Profile)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp,
                    pressedElevation = 0.dp, disabledElevation = 0.dp),
                shape = RoundedCornerShape(10)
            ) {
                Text(
                    text = "Edit Profile",
                    color = Color.Black
                )
            }
            PostItem(isLoading = isLoading, postsLoading = postsLoading, posts = posts,
                modifier = Modifier.weight(1f).padding(1.dp).fillMaxSize()
            ) {
                navigateTo(navController = navController, destination = DestinationScreen.PostDetails,
                    NavigationParam(ARGS_KEY, it))
            }
        }
        BottomNavigationBar(navController = navController)
    }
}

@Composable
fun ProfileImage(imageUrl: String?, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable {
                onClick.invoke()
            }
    ) {
        UserImageCard(userImage = imageUrl, modifier = Modifier
            .padding(8.dp)
            .size(80.dp))

        Card(
            shape = CircleShape,
            border = BorderStroke(width = 2.dp, color = Color.White),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add image",
                modifier = Modifier.background(Color.Blue)
            )
        }
    }
}