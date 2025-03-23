package com.example.myapplication.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.user.User
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.utils.UserDataStore

/**
 * 홈화면
 */
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val userDataStore = UserDataStore(context)
    var user by remember { mutableStateOf<User?>(null) }
    var authChecked by remember { mutableStateOf(false) }

    // 토큰 검증
    CheckAuth(navController, roleId = 1) {
        authChecked = true
    }

    // UI 로딩 시 폐기물 리스트 불러오기
    LaunchedEffect(authChecked) {
        if (!authChecked) return@LaunchedEffect
        user = userDataStore.getUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("안녕하세요! ${user?.name} 님👋", style = MaterialTheme.typography.headlineSmall)
        Text(user?.hospital?.hospitalName ?: "", style = MaterialTheme.typography.bodySmall)

        Text("${user?.role?.roleName}", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(32.dp))


        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HomeButton("폐기물 목록", Icons.AutoMirrored.Filled.List) { navController.navigate("waste_list") }
                HomeButton("폐기물 등록", Icons.Default.Add) { navController.navigate("waste_register") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HomeButton("주변 폐기물 처리", Icons.Default.LocationOn) { navController.navigate("waste_move") }
                HomeButton("폐기물 배출", Icons.Default.Delete) { navController.navigate("waste_remove") }
            }
        }
    }
}

// 공통 버튼 Composable
@Composable
fun HomeButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.LightGray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, fontSize = 16.sp)
        }
    }
}

