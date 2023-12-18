package com.polware.instagramclone.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.polware.instagramclone.viewmodel.IgViewModel

@Composable
fun NotificationMessage(viewModel: IgViewModel) {
    val notificationState = viewModel.popupNotification.value
    val notificationMessage = notificationState?.getContentOrNull()

    if (notificationMessage != null) {
        Toast.makeText(LocalContext.current, notificationMessage, Toast.LENGTH_SHORT).show()
    }

}