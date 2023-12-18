package com.polware.instagramclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.polware.instagramclone.data.model.NavigationParam
import com.polware.instagramclone.data.model.PostData
import com.polware.instagramclone.data.util.Constants.ARGS_KEY
import com.polware.instagramclone.ui.screens.SignupScreen
import com.polware.instagramclone.data.util.DestinationScreen
import com.polware.instagramclone.ui.components.NotificationMessage
import com.polware.instagramclone.ui.screens.CommentScreen
import com.polware.instagramclone.ui.screens.LoginScreen
import com.polware.instagramclone.ui.screens.MainScreen
import com.polware.instagramclone.ui.screens.NewPost
import com.polware.instagramclone.ui.screens.PostDetails
import com.polware.instagramclone.ui.screens.PostScreen
import com.polware.instagramclone.ui.screens.ProfileScreen
import com.polware.instagramclone.ui.screens.SearchScreen
import com.polware.instagramclone.ui.theme.InstagramCloneTheme
import com.polware.instagramclone.viewmodel.IgViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstagramCloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InstagramApp()
                }
            }
        }
    }
}

@Composable
fun InstagramApp() {
    val viewModel = hiltViewModel<IgViewModel>()
    val navController = rememberNavController()
    
    NotificationMessage(viewModel = viewModel)
    NavHost(navController = navController, startDestination = DestinationScreen.Login.route) {

        composable(DestinationScreen.Login.route) {
            LoginScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreen.Signup.route) {
            SignupScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreen.Main.route) {
            MainScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreen.Search.route) {
            SearchScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreen.Post.route) {
            PostScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController = navController, viewModel = viewModel)
        }

        composable(DestinationScreen.NewPost.route) {
            val imageUri = it.arguments?.getString("imageUri")
            imageUri?.let {
                uri ->
                NewPost(navController = navController, viewModel = viewModel, encodeUri = uri)
            }
        }

        composable(DestinationScreen.PostDetails.route) {
            val postData = navController.previousBackStackEntry?.arguments?.getParcelable<PostData>(ARGS_KEY)
            postData?.let {
                PostDetails(navController = navController, viewModel = viewModel, post = it)
            }
        }

        composable(DestinationScreen.Comments.route) {
            navBackStackEntry ->
            val postId = navBackStackEntry.arguments?.getString("postId")
            postId?.let {
                CommentScreen(navController = navController, viewModel = viewModel, postId = it)
            }
        }

    }
}

fun navigateTo(navController: NavController, destination: DestinationScreen, vararg params: NavigationParam) {
    // Pass all parameters into the BackStackEntry
    for (param in params) {
        navController.currentBackStackEntry?.arguments?.putParcelable(param.nameParam, param.valueParam)
    }
    navController.navigate(destination.route) {
        popUpTo(destination.route)
        // Reuse loaded screens to prevent large number of screens in BackStack
        launchSingleTop = true
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InstagramCloneTheme {
        InstagramApp()
    }
}