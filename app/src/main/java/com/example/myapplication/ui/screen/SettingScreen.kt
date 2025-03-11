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
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        Button(
            onClick = {
                logout(context)  // ✅ 토큰 삭제 후 로그아웃
                navController.navigate("login") { // ✅ 로그인 화면으로 이동
                    popUpTo("home") { inclusive = true } // 뒤로 가기 방지
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Logout")
        }
        Button(
            onClick = { navController.popBackStack() }, // ✅ 이전 화면으로 돌아가기
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Back")
        }
    }
}

fun logout(context: Context) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit() { remove("auth_token") }  // ✅ 저장된 토큰 삭제

}