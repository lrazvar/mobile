package com.example.mobilki.presentation.screens.auth_screen.adminScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mobilki.data.enity.User

@Composable
fun UserRow(
    user: User,
    onMakeAdminClicked: () -> Unit,
    onRemoveAdminClicked: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        user.name?.let { Text(text = it) }
        Spacer(modifier = Modifier.weight(1f))
        if (!user.isAdmin) {
            Button(onClick = onMakeAdminClicked) {
                Text(text = "Назначить администратором")
            }
        } else {
            Button(onClick = onRemoveAdminClicked) {
                Text(text = "Снять администраторские права")
            }
        }
    }
}

