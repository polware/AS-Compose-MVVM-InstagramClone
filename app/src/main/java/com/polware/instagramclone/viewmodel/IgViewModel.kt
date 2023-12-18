package com.polware.instagramclone.viewmodel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.polware.instagramclone.data.model.CommentData
import com.polware.instagramclone.data.model.PostData
import com.polware.instagramclone.data.model.UserData
import com.polware.instagramclone.data.util.Constants.COMMENTS
import com.polware.instagramclone.data.util.Constants.POSTS
import com.polware.instagramclone.data.util.Constants.USERS
import com.polware.instagramclone.data.util.IgEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class IgViewModel @Inject constructor(private val authFb: FirebaseAuth,
                                      private val databaseFs: FirebaseFirestore,
                                      private val storageFb: FirebaseStorage): ViewModel() {

    val signedIn = mutableStateOf(false)
    val inProgress = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)
    val popupNotification = mutableStateOf<IgEvent<String>?>(null)
    val refreshPostProgress = mutableStateOf(false)
    val posts = mutableStateOf<List<PostData>>(listOf())
    val searchPost = mutableStateOf<List<PostData>>(listOf())
    val searchProgress = mutableStateOf(false)
    val postsFeed = mutableStateOf<List<PostData>>(listOf())
    val feedProgress = mutableStateOf(false)
    val comments = mutableStateOf<List<CommentData>>(listOf())
    val commentProgress = mutableStateOf(false)
    val followers = mutableStateOf(0)

    init {
        val currentUser = authFb.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun onSignup(userName: String, email: String, password: String) {
        if (userName.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleExceptionOrEvent(customMessage = "Please fill all fields")
            return
        }
        inProgress.value = true
        databaseFs.collection(USERS).whereEqualTo("userName", userName).get()
            .addOnSuccessListener {
                documents ->
                if (documents.size() > 0) {
                    handleExceptionOrEvent(customMessage = "Username already exists!")
                    inProgress.value = false
                }
                else {
                    authFb.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            task ->
                            if (task.isSuccessful) {
                                signedIn.value = true
                                // Create profile
                                createOrUpdateProfile(userName = userName)
                            }
                            else {
                                handleExceptionOrEvent(task.exception, "SigIn failed!")
                            }
                            inProgress.value = false
                        }
                }
            }
            .addOnFailureListener {

            }

    }

    fun onLogin(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleExceptionOrEvent(customMessage = "Please fill all fields")
            return
        }
        inProgress.value = true
        authFb.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    signedIn.value = true
                    inProgress.value = false
                    authFb.currentUser?.uid?.let {
                        uid ->
                        //handleExceptionOrEvent(customMessage = "Login successfully")
                        getUserData(uid)
                    }
                }
                else {
                    handleExceptionOrEvent(task.exception, "Login failed")
                    inProgress.value = false
                }
            }
            .addOnFailureListener {
                handleExceptionOrEvent(it, "Login failed")
                inProgress.value = false
            }
    }

    private fun createOrUpdateProfile(name: String? = null, userName: String? = null,
                                      bio: String? = null, imageUrl: String? = null) {

        val userId = authFb.currentUser?.uid
        val newUser = UserData(userId = userId, name = name ?: userData.value?.name,
            userName = userName ?: userData.value?.userName, bio = bio ?: userData.value?.bio,
            imageUrl = imageUrl ?: userData.value?.imageUrl, following = userData.value?.following)

        userId?.let {
            uid ->
            inProgress.value = true
            databaseFs.collection(USERS).document(uid).get()
                .addOnSuccessListener {
                if (it.exists()) {
                    it.reference.update(newUser.toMapFirebase())
                        .addOnSuccessListener {
                        this.userData.value = newUser
                        inProgress.value = false
                        popupNotification.value = IgEvent("Profile updated!")
                        }
                        .addOnFailureListener {
                            handleExceptionOrEvent(it, "Cannot update user!")
                            inProgress.value = false
                        }
                }
                else {
                    databaseFs.collection(USERS).document(uid).set(newUser)
                    getUserData(uid)
                    inProgress.value = false
                    }
                }
                .addOnFailureListener {
                    handleExceptionOrEvent(it, "Cannot create user!")
                    inProgress.value = false
                }
        }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        databaseFs.collection(USERS).document(uid).get()
            .addOnSuccessListener {
                val userInfo = it.toObject<UserData>()
                userData.value = userInfo
                inProgress.value = false
                //popupNotification.value = IgEvent("User data retrieved successfully")
                refreshPost()
                getPersonalizedFeed()
                getFollowers(userInfo?.userId)
            }
            .addOnFailureListener {
                handleExceptionOrEvent(it, "Cannot retrieve user data")
                inProgress.value = false
            }
    }

    fun updateUserProfile(name: String, username: String, bio: String) {
        createOrUpdateProfile(name = name, userName = username, bio = bio)
    }

    private fun handleExceptionOrEvent(exception: Exception? = null, customMessage: String = "") {
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMessage else "$customMessage: $errorMessage"
        popupNotification.value = IgEvent(message)
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storageFb.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
        }
            .addOnFailureListener {
                exception ->
                handleExceptionOrEvent(exception)
                inProgress.value = false
            }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
            syncUserPostsImage(it.toString())
        }
    }

    private fun syncUserPostsImage(imageUrl: String) {
        val currentUuid = authFb.currentUser?.uid
        databaseFs.collection(POSTS).whereEqualTo("userId", currentUuid).get()
            .addOnSuccessListener {
                val posts = mutableStateOf<List<PostData>>(arrayListOf())
                convertPosts(it, posts)
                val refs = arrayListOf<DocumentReference>()
                for (post in posts.value) {
                    post.postId?.let {
                        id ->
                        refs.add(databaseFs.collection(POSTS).document(id))
                    }
                }
                if (refs.isNotEmpty()) {
                    databaseFs.runBatch {
                        batch ->
                        for (ref in refs) {
                            batch.update(ref, "userImage", imageUrl)
                        }
                    }.addOnSuccessListener {
                        refreshPost()
                        }
                }
            }
    }

    fun onLogout() {
        authFb.signOut()
        signedIn.value = false
        userData.value = null
        popupNotification.value = IgEvent("Logged out!")
        searchPost.value = listOf()
        postsFeed.value = listOf()
        comments.value = listOf()
    }

    fun onNewPost(uri: Uri, description: String, onPostSuccess: () -> Unit) {
        uploadImage(uri) {
            onCreatePost(it, description, onPostSuccess)
        }
    }

    private fun onCreatePost(imageUri: Uri, description: String, onPostSuccess: () -> Unit) {
        inProgress.value = true
        val currentUid = authFb.currentUser?.uid
        val currentUsername = userData.value?.userName
        val currentUserImage = userData.value?.imageUrl
        if (currentUid != null) {
            val postUuid = UUID.randomUUID().toString()
            val fillerWords = listOf("the", "to", "is", "of", "and", "or", "in", "it", "a")
            val searchTerms = description.split(" ", ".", ",", "?", "!", "#")
                .map {
                    it.lowercase()
                }
                .filter {
                    it.isNotEmpty() and !fillerWords.contains(it)
                }

            val post = PostData(
                postId = postUuid,
                userId = currentUid,
                username = currentUsername,
                userImage = currentUserImage,
                postImage = imageUri.toString(),
                postDescription = description,
                time = System.currentTimeMillis(),
                likes = listOf(),
                searchTerms = searchTerms
            )
            databaseFs.collection(POSTS).document(postUuid).set(post)
                .addOnSuccessListener {
                    popupNotification.value = IgEvent("Post successfully added!")
                    inProgress.value = false
                    refreshPost()
                    onPostSuccess.invoke()
                }
                .addOnFailureListener {
                    handleExceptionOrEvent(it, "Unable to create post")
                    inProgress.value = false
                }
        }
        else {
            handleExceptionOrEvent(customMessage = "Error: Username unavailable, unable to publish post.")
            onLogout()
            inProgress.value = false
        }
    }

    private fun refreshPost() {
        val currentUid = authFb.currentUser?.uid
        if (currentUid != null) {
            refreshPostProgress.value = true
            databaseFs.collection(POSTS).whereEqualTo("userId", currentUid).get()
                .addOnSuccessListener {
                    convertPosts(it, posts)
                    refreshPostProgress.value = false
                }
                .addOnFailureListener {
                    handleExceptionOrEvent(it, "Cannot fetch posts")
                    refreshPostProgress.value = false
                }
        }
        else {
            handleExceptionOrEvent(customMessage = "Error: Username unavailable, unable to refresh post.")
            onLogout()
        }
    }

    private fun convertPosts(documents: QuerySnapshot, outState: MutableState<List<PostData>>) {
        val newPosts = mutableListOf<PostData>()
        documents.forEach {
            val post = it.toObject<PostData>()
            newPosts.add(post)
        }
        val sortedPosts: MutableList<PostData> = newPosts.toMutableList()
        sortedPosts.sortByDescending { it.time }
        outState.value = sortedPosts
    }

    fun onSearchPost(searchTerm: String) {
        if (searchTerm.isNotEmpty()) {
            searchProgress.value = true
            databaseFs.collection(POSTS)
                .whereArrayContains("searchTerms", searchTerm.trim().lowercase())
                .get()
                .addOnSuccessListener {
                    convertPosts(it, searchPost)
                    searchProgress.value = false
                }
                .addOnFailureListener {
                    handleExceptionOrEvent(it, "Cannot search post")
                    searchProgress.value = false
                }
        }
    }

    fun clearSearchedPost() {
        searchPost.value = listOf()
    }

    fun onFollowClick(userId: String) {
        authFb.currentUser?.uid?.let {
            currentUser ->
            val following = arrayListOf<String>()
            userData.value?.following?.let {
                following.addAll(it)
            }
            if (following.contains(userId)) {
                following.remove(userId)
            }
            else {
                following.add(userId)
            }
            databaseFs.collection(USERS).document(currentUser).update("following", following)
                .addOnSuccessListener {
                    getUserData(currentUser)
                }
        }
    }

    private fun getPersonalizedFeed() {
        val following = userData.value?.following
        feedProgress.value = true
        if (!following.isNullOrEmpty()) {
            databaseFs.collection(POSTS).whereIn("userId", following).get()
                .addOnSuccessListener {
                    convertPosts(documents = it, outState = postsFeed)
                    if (postsFeed.value.isEmpty()) {
                        getGeneralFeed()
                    }
                    else {
                        feedProgress.value = false
                    }
                }
                .addOnFailureListener {
                    handleExceptionOrEvent(it, "Cannot get personalized feed")
                    feedProgress.value = false
                }
        }
        else {
            getGeneralFeed()
        }
    }

    private fun getGeneralFeed() {
        val currentTime = System.currentTimeMillis()
        val difference = 24 * 60 * 60 * 1000 // One day in milliseconds

        feedProgress.value = true
        databaseFs.collection(POSTS).whereGreaterThan("time", currentTime - difference).get()
            .addOnSuccessListener {
                convertPosts(documents = it, outState = postsFeed)
                feedProgress.value = false
            }
            .addOnFailureListener {
                handleExceptionOrEvent(it, "Cannot get feed")
                feedProgress.value = false
            }
    }

    fun gestureToLikePost(postData: PostData) {
        val newLikes = arrayListOf<String>()
        authFb.currentUser?.uid?.let {
            userId ->
            postData.likes?.let {
                    listLikes ->
                if (listLikes.contains(userId)) {
                    newLikes.addAll(listLikes.filter {
                        userId != it
                    })
                }
                else {
                    newLikes.addAll(listLikes)
                    newLikes.add(userId)
                }
                postData.postId?.let {
                        postId ->
                    databaseFs.collection(POSTS).document(postId).update("likes", newLikes)
                        .addOnSuccessListener {
                            postData.likes = newLikes
                        }
                        .addOnFailureListener {
                            handleExceptionOrEvent(it, "Unable to like the post")
                        }
                }
            }
        }
    }

    fun createComment(postId: String, text: String) {
        userData.value?.userName?.let {
            username ->
            val commentId = UUID.randomUUID().toString()
            val comment = CommentData(commentId = commentId, postId = postId, username = username,
                text = text, timestamp = System.currentTimeMillis())
            databaseFs.collection(COMMENTS).document(commentId).set(comment)
                .addOnSuccessListener {
                    getPostComment(postId)
                    popupNotification.value = IgEvent("Comment added!")
                }
                .addOnFailureListener {
                    handleExceptionOrEvent(it, "Cannot create the comment")

                }
        }
    }

    fun getPostComment(postId: String?) {
        commentProgress.value = true
        val newComments = mutableListOf<CommentData>()
        databaseFs.collection(COMMENTS).whereEqualTo("postId", postId).get()
            .addOnSuccessListener {
                querySnapshot ->
                querySnapshot.forEach {
                    document ->
                    val comment = document.toObject<CommentData>()
                    newComments.add(comment)
                }
                val sortedComments = newComments.sortedByDescending { it.timestamp }
                comments.value = sortedComments
                commentProgress.value = false
            }
            .addOnFailureListener {
                handleExceptionOrEvent(it, "Cannot retrieve comments")
                commentProgress.value = false
            }
    }

    private fun getFollowers(uid: String?) {
        databaseFs.collection(USERS).whereArrayContains("following", uid ?: "").get()
            .addOnSuccessListener {
                followers.value = it.size()
            }

    }

}