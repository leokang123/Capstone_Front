package com.example.myapplication.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun AuthCheckScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    LaunchedEffect(Unit) {
        val user = viewModel.checkAutoLogin()
        if (user != null && user.token?.isNotBlank() == true) {
            viewModel.initData(user.hospital?.id ?: 0)
            // 일단 나중에 개편해야함
//            delay(1000)
            navController.navigate("home") {
                popUpTo("auth_check") { inclusive = true } // 뒤로가기 안 되도록 제거
            }
        } else {
            navController.navigate("login") {
                popUpTo("auth_check") { inclusive = true }
            }
        }
    }

    // 로딩 UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
