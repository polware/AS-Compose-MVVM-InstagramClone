package com.polware.instagramclone.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.polware.instagramclone.data.model.NavigationParam
import com.polware.instagramclone.data.util.Constants.ARGS_KEY
import com.polware.instagramclone.data.util.DestinationScreen
import com.polware.instagramclone.navigateTo
import com.polware.instagramclone.ui.components.BottomNavigationBar
import com.polware.instagramclone.ui.components.PostItem
import com.polware.instagramclone.viewmodel.IgViewModel

@Composable
fun SearchScreen(navController: NavController, viewModel: IgViewModel) {
    val searchLoading = viewModel.searchProgress.value
    val searchPosts = viewModel.searchPost.value
    var searchTerm by rememberSaveable {
        mutableStateOf("")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            searchTerm = searchTerm,
            onSearchChange = { searchTerm = it },
            onSearch = { viewModel.onSearchPost(searchTerm) },
            onCloseClicked = {
                searchTerm = ""
            }
        )
        PostItem(
            isLoading = false,
            postsLoading = searchLoading,
            posts = searchPosts,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            navigateTo(navController = navController, destination = DestinationScreen.PostDetails,
                NavigationParam(ARGS_KEY, it))
        }
        BottomNavigationBar(navController = navController)
    }
}

@Composable
fun SearchBar(searchTerm: String, onSearchChange: (String) -> Unit, onSearch: () -> Unit, onCloseClicked: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    
    TextField(
        value = searchTerm,
        onValueChange = onSearchChange,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, CircleShape),
        shape = CircleShape,
        placeholder = {
            Text(
                text = "Search",
                color = Color.Black,
                modifier = Modifier
                    .alpha(ContentAlpha.medium)

            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch()
            focusManager.clearFocus()
        }),
        maxLines = 1,
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            textColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            IconButton(
                onClick = {
                    if (searchTerm.isNotEmpty()) {
                        onSearch()
                        focusManager.clearFocus()
                    }
                    else {
                        Toast.makeText(context, "Please type a word for the search!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    onCloseClicked()
                }
            ) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
            }
        }
    )
}