package com.example.myapplication.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.viewmodel.SettingsViewModel

/**
 * 설정창
 * 3/11 (강정훈)
 * 일단 화면이 심심해서 넣어놨는데 젤 나중에 구현해도 될거같음 넣을 항목이없어서 로그아웃기능넣어둠
 */
@Composable
fun SettingsDialog(navController: NavController) {
    Dialog(onDismissRequest = { navController.popBackStack() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SettingsScreen(navController)
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = {
                viewModel.logout { success ->
                    if (success) {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Logout")
        }
        Button(
            onClick = { navController.popBackStack() }, //  이전 화면으로 돌아가기
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Back")
        }
    }
}

