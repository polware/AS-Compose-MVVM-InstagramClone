package com.polware.instagramclone.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.polware.instagramclone.R
import com.polware.instagramclone.data.util.DestinationScreen
import com.polware.instagramclone.navigateTo
import com.polware.instagramclone.ui.components.CheckSignedIn
import com.polware.instagramclone.ui.components.ProgressSpinner
import com.polware.instagramclone.viewmodel.IgViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: IgViewModel) {
    val focus = LocalFocusManager.current
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    // Takes the user to the main view when is already registered
    CheckSignedIn(navController = navController, viewModel = viewModel)

    Box(modifier = Modifier.fillMaxSize()) {

        val isLoading = viewModel.inProgress.value
        if (isLoading) {
            ProgressSpinner()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Image(
                painter = painterResource(id = R.drawable.ig_logo),
                contentDescription = null,
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Card(
                modifier = Modifier
                    .width(340.dp)
                    .height(340.dp)
                    .padding(4.dp),
                shape = RectangleShape,
                elevation = 6.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Log In",
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                    OutlinedTextField(
                        value = emailState.value,
                        onValueChange = {
                            emailState.value = it
                        },
                        modifier = Modifier.padding(8.dp),
                        label = {
                            Text(text = "Email")
                        }
                    )
                    OutlinedTextField(
                        value = passwordState.value,
                        onValueChange = {
                            passwordState.value = it
                        },
                        modifier = Modifier.padding(8.dp),
                        label = {
                            Text(text = "Password")
                        },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Button(
                        onClick = {
                            // Dismiss the keyboard
                            focus.clearFocus(force = true)
                            viewModel.onLogin(emailState.value.text, passwordState.value.text)
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Log in")
                    }
                    Text(
                        text = "Don't have an account? Sign up ->",
                        color = Color.Blue,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                navigateTo(
                                    navController = navController,
                                    destination = DestinationScreen.Signup
                                )
                            }
                    )
                }
            }
        }
    }
}