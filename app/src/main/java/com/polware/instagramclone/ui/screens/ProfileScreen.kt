package com.polware.instagramclone.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.polware.instagramclone.data.util.DestinationScreen
import com.polware.instagramclone.navigateTo
import com.polware.instagramclone.ui.components.CommonImage
import com.polware.instagramclone.ui.components.CustomDivider
import com.polware.instagramclone.ui.components.ProgressSpinner
import com.polware.instagramclone.viewmodel.IgViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: IgViewModel) {
    val isLoading = viewModel.inProgress.value
    val userData = viewModel.userData.value
    var name by rememberSaveable {
        mutableStateOf(userData?.name ?: "")
    }
    var username by rememberSaveable {
        mutableStateOf(userData?.userName ?: "")
    }
    var bio by rememberSaveable {
        mutableStateOf(userData?.bio ?: "")
    }

    if (isLoading) {
        ProgressSpinner()
    }
    else {
        ProfileContent(viewModel = viewModel, name = name, userName = username, bio = bio,
            onNameChange = { name = it}, onUsernameChange = { username = it }, onBioChange = { bio = it},
            onSave = {
                viewModel.updateUserProfile(name, username, bio)
            },
            onBack = {
                navigateTo(navController, DestinationScreen.Post)
            },
            onLogout = {
                viewModel.onLogout()
                navigateTo(navController, DestinationScreen.Login)
            })
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileContent(viewModel: IgViewModel, name: String, userName: String, bio: String,
                   onNameChange: (String) -> Unit, onUsernameChange: (String) -> Unit,
                   onBioChange: (String) -> Unit, onSave: () -> Unit, onBack: () -> Unit, onLogout: () -> Unit) {
    val scrollState = rememberScrollState()
    val imageUrl = viewModel.userData.value?.imageUrl
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Back",
                modifier = Modifier.clickable {
                    onBack.invoke()
                }
            )
            Text(
                text = "Save",
                modifier = Modifier.clickable {
                    onSave.invoke()
                }
            )
        }
        CustomDivider()
        // User image
        ProfileImage(imageUrl = imageUrl, viewModel = viewModel)
        CustomDivider()
        // Field for Name
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            value = name,
            onValueChange = { onNameChange(it) },
            label = { Text(text = "Name") },
            textStyle = MaterialTheme.typography.body1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        // Field for Username
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            value = userName,
            onValueChange = { onUsernameChange(it) },
            label = { Text(text = "Username") },
            textStyle = MaterialTheme.typography.body1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        // Field for Bio
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(start = 4.dp, end = 4.dp),
            value = bio,
            onValueChange = { onBioChange(it) },
            label = { Text(text = "Bio") },
            singleLine = false,
            textStyle = MaterialTheme.typography.body1,
            keyboardActions = KeyboardActions {
                onBioChange(bio.trim())
                keyboardController?.hide()
            }
        )
        // Row for Logout action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Logout",
                modifier = Modifier.clickable {
                    onLogout.invoke()
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?, viewModel: IgViewModel) {
    val isLoading = viewModel.inProgress.value
    val launcherImageSelect = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri ->
        uri?.let {
            viewModel.uploadProfileImage(it)
        }
    }

    Box(modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        if (isLoading) {
            ProgressSpinner()
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    // Select image from gallery
                    launcherImageSelect.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(4.dp)
                    .size(120.dp)
                ) {
                CommonImage(data = imageUrl, modifier = Modifier.wrapContentSize())
            }
            Text(
                text = "Change profile image",
                modifier = Modifier.alpha(0.5f)
            )
        }
    }
}