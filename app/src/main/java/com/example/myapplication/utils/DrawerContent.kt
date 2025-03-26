package com.example.myapplication.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.R
import kotlinx.coroutines.launch

/**
 * 왼쪽 네비바 꾸미는 컴포넌트
 */

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState) {

    Column(
        modifier = Modifier
            .width(250.dp) // Drawer 크기 조절 (전체 화면을 덮지 않도록)
            .fillMaxHeight()
            .background(colorResource(id = R.color.black).copy(alpha = 0.7f))
            .padding(16.dp)) {
        Text(text = "", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        NavigationItem("홈화면", navController, drawerState, "home")
        NavigationItem("폐기물 목록", navController, drawerState, "waste_list")
        NavigationItem("폐기물 등록", navController, drawerState, "waste_register")
        NavigationItem("주변 페기물 처리", navController, drawerState, "waste_move")
        NavigationItem("폐기물 배출", navController, drawerState, "waste_remove")

    }
}

@Composable
fun NavigationItem(title: String, navController: NavController, drawerState: DrawerState, route: String) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch { drawerState.close() } // Drawer 닫기
            navController.navigate(route) // 네비게이션 이동
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(text = title)
    }
}

@Composable
fun BackNavigationItem(title: String, navController: NavController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                drawerState.close()
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
            }
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(text = title)
    }
}
