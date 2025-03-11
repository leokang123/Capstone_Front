package com.example.myapplication.ui.screen

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController

@Composable
fun NotificationScreen(navController: NavController) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Notification", style = MaterialTheme.typography.headlineMedium)
        Text("Example1", style = MaterialTheme.typography.headlineSmall)
        Text("Example2", style = MaterialTheme.typography.headlineSmall)
        Text("Example3", style = MaterialTheme.typography.headlineSmall)

        Button(
            onClick = { navController.popBackStack() }, // ✅ 이전 화면으로 돌아가기
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Back")
        }
    }
}
