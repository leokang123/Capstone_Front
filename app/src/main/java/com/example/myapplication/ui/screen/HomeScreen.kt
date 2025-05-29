package com.example.myapplication.ui.screen


import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.utils.getAutoTextColor
import com.example.myapplication.utils.requestAllPermissions
import com.example.myapplication.viewmodel.HomeViewModel

/**
 * 홈화면
 */
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    var authChecked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity


    // 토큰 검증
    CheckAuth(navController, role = Roles.USER) {

        authChecked = true
    }

//    // UI 로딩 시 폐기물 리스트 불러오기
    LaunchedEffect(authChecked) {
        if (!authChecked) return@LaunchedEffect
        activity?.let { requestAllPermissions(it) }
        viewModel.updateAppBarTitle("폐기수첩")
        viewModel.getUser()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp), // ✅ 내부 여백 추가
                verticalArrangement = Arrangement.spacedBy(4.dp) // 텍스트 간 간격
            ) {
                Text(
                    buildAnnotatedString {
                        append("안녕하세요!")
                        append(" ")

                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp

                            ),
                        ) {
                            append("${user?.name} ")
                        }
                        append("님")
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    buildAnnotatedString {
                        append(user?.hospital?.hospitalName ?: "")
                        append(" ")

                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("${user?.primaryRoles}")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(5.dp))
                HorizontalDivider(thickness = 0.1.dp, color = MaterialTheme.colorScheme.onSurface)

                Text(
                    "폐기물 관리 시스템에 오신 것을 환영합니다. 아래 버튼을 사용하여 주요 작업을 빠르게 수행할 수 있습니다.",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))


        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HomeButton(
                    "폐기물 목록",
                    "등록된 폐기물 확인",
                    Icons.AutoMirrored.Filled.List
                ) {
                    viewModel.updateAppBarTitle("폐기물 목록")
                    navController.navigate("waste_list")
                }
                HomeButton(
                    "신규 폐기물 등록",
                    "새로운 폐기물 정보 입력",
                    Icons.Default.Add
                ) {
                    viewModel.updateAppBarTitle("신규 폐기물 등록")
                    navController.navigate("waste_register")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HomeButton(
                    "주변 폐기물 처리",
                    "유저권한으로 비콘을 검색하여 주변 폐기물 처리",
                    Icons.Default.LocationOn
                ) {
                    viewModel.updateAppBarTitle("주변 폐기물 처리")
                    navController.navigate("waste_move")
                }
                HomeButton(
                    "폐기물 배출 처리",
                    "관리자 권한으로 창고에 저장된 폐기물을 비콘 검색후 처리",
                    Icons.Default.Delete
                ) {
                    viewModel.updateAppBarTitle("폐기물 배출 처리")
                    navController.navigate("waste_remove")
                }
            }
        }
    }

}

// 공통 버튼 Composable
@Composable
fun HomeButton(text: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    val bgColor = MaterialTheme.colorScheme.primary
    val textColor = getAutoTextColor(bgColor)

    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                tint = Color.White,
                contentDescription = text,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(4.dp)) // 간격 줄이기

            Text(
                text,
                color = textColor,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(2.dp)) // 텍스트와 description 사이 간격

            Text(
                description,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                lineHeight = 10.sp,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp) // 좌우 여백 추가
            )
        }
    }
}

