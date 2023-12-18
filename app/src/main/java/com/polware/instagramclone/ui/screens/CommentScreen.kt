package com.polware.instagramclone.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Text
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.polware.instagramclone.data.model.CommentData
import com.polware.instagramclone.ui.components.CustomDivider
import com.polware.instagramclone.ui.components.ProgressSpinner
import com.polware.instagramclone.ui.theme.commentBackgroundColor
import com.polware.instagramclone.viewmodel.IgViewModel

@Composable
fun CommentScreen(navController: NavController, viewModel: IgViewModel, postId: String) {
    var commentText by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val comments = viewModel.comments.value
    val commentsProgress = viewModel.commentProgress.value

    Column(modifier = Modifier.fillMaxSize()
    ) {

        if (commentsProgress) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProgressSpinner()
            }
        }
        else if (comments.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Back",
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
                CustomDivider()
                Text(
                    text = "No comments available!",
                    fontWeight = FontWeight.Bold
                )
            }
        }
        else {
            Text(
                text = "Back",
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            CustomDivider()
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(comments) {
                    CommentRow(it)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = commentText,
                onValueChange = {
                    commentText = it
                },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.LightGray),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Button(
                onClick = {
                    viewModel.createComment(postId = postId, text = commentText)
                    commentText = ""
                    focusManager.clearFocus()
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "Comment",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CommentRow(comment: CommentData) {
    Card(
        shape = RoundedCornerShape(20.dp),
        backgroundColor = MaterialTheme.colors.commentBackgroundColor,
        elevation = 6.dp,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "@${comment.username ?: ""}",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = comment.text ?: "",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}