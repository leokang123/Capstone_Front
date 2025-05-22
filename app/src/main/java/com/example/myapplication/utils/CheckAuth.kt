package com.example.myapplication.utils

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.viewmodel.AuthState
import com.example.myapplication.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue


@Composable
fun CheckAuth(
    navController: NavController,
    role: Roles = Roles.USER,
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAuth(role)
    }

    when (authState) {
        is AuthState.Loading -> {
            // 로딩 처리 UI or 아무것도 하지 않음
        }
        is AuthState.NotLoggedIn -> {
            Toast.makeText(context, "로그인이 필요한 기능입니다", Toast.LENGTH_SHORT).show()
            LaunchedEffect(Unit) {
                navController.navigate("login") { popUpTo(0) }
            }
        }
        is AuthState.Unauthorized -> {
            val actual = (authState as AuthState.Unauthorized).actualRole
            Toast.makeText(context, "$actual (이)가 사용할 수 없는 기능입니다", Toast.LENGTH_SHORT).show()
            LaunchedEffect(Unit) {
                navController.navigateUp()
            }
        }
        is AuthState.Authorized -> {
            onAuthSuccess()
        }
    }
}

