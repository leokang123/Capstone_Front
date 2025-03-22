package com.example.myapplication.ui.component

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext


@Composable
fun CheckAuth(navController: NavController, roleId: Long = 1, onAuthChecked: () -> Unit) {
    val context = LocalContext.current
    val userDataStore = remember { UserDataStore(context) }

    // 사용자 정보가 로드된 후 권한 체크
    LaunchedEffect(Unit) {
        val token = userDataStore.getAccessToken()
        val user = userDataStore.getUser()
        val userRoleId = user?.role?.id ?: 1
        val userRoleName = user?.role?.roleName ?: "권한정보 없음"

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "로그인이 필요한 기능", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo(0) // 네비게이션 스택 초기화 (뒤로 가기 방지)
            }
        } else if (user != null && userRoleId < roleId) {
            Toast.makeText(context, "$userRoleName (이)가 사용할 수 없는 기능", Toast.LENGTH_SHORT).show()
            navController.navigateUp() // popBackStack() 대신 navigateUp() 사용
        } else {
            onAuthChecked()

        }
    }
}
