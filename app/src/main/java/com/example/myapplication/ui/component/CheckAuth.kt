package com.example.myapplication.ui.component

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext

fun getToken(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("auth_token", null)
}

@Composable
fun CheckAuth(navController: NavController) {
    val context = LocalContext.current
    val token by remember { mutableStateOf(getToken(context)) }

    LaunchedEffect(token) {
        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "로그인이 필요한 기능입니다", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo(0) // 뒤로 가기 방지 (완전히 새로운 네비게이션 스택)
            }
        }
    }
}