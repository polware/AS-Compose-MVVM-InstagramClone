package com.polware.instagramclone.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.polware.instagramclone.data.util.DestinationScreen
import com.polware.instagramclone.viewmodel.IgViewModel

@Composable
fun CheckSignedIn(navController: NavController, viewModel: IgViewModel) {
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = viewModel.signedIn.value

    if (signedIn && !alreadyLoggedIn.value) {
        alreadyLoggedIn.value = true
        navController.navigate(DestinationScreen.Main.route) {
            popUpTo(0)
        }
    }
}