package com.polware.instagramclone.data.util

sealed class DestinationScreen(val route: String) {

    object Signup: DestinationScreen("signup")

    object Login: DestinationScreen("login")

    object Main: DestinationScreen("main")

    object Search: DestinationScreen("search")

    object Post: DestinationScreen("posts")

    object Profile: DestinationScreen("profile")

    object NewPost: DestinationScreen("new_post/{imageUri}") {
        fun createRoute(uri: String) = "new_post/$uri"
    }

    object PostDetails: DestinationScreen("post_details")

    object Comments: DestinationScreen("comments/{postId}") {
        fun createRoute(postId: String) = "comments/$postId"
    }

}
