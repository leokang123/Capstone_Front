package com.example.myapplication.utils

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.myapplication.data.enums.Roles


@Composable
fun CheckAuth(navController: NavController, role: Roles = Roles.USER, onAuthChecked: () -> Unit) {
    val context = LocalContext.current
    val userDataStore = remember { UserDataStore(context) }

    // 사용자 정보가 로드된 후 권한 체크
    LaunchedEffect(Unit) {
        val token = userDataStore.getAccessToken()
        val user = userDataStore.getUser()
        val userRole = user?.primaryRoles

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "로그인이 필요한 기능", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo(0) // 네비게이션 스택 초기화 (뒤로 가기 방지)
            }
        } else if (user?.roles?.contains(role) == false) {
            Toast.makeText(context, "$userRole (이)가 사용할 수 없는 기능", Toast.LENGTH_SHORT).show()
            navController.navigateUp() // popBackStack() 대신 navigateUp() 사용
        } else {
            onAuthChecked()

        }
    }
}
