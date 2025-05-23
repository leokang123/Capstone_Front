package com.example.myapplication

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screen.AuthCheckScreen
import com.example.myapplication.ui.screen.HomeScreen
import com.example.myapplication.ui.screen.LoginScreen
import com.example.myapplication.ui.screen.NotificationDialog
import com.example.myapplication.ui.screen.RegisterScreen
import com.example.myapplication.ui.screen.SettingsDialog
import com.example.myapplication.ui.screen.WasteListScreen
import com.example.myapplication.ui.screen.WasteMoveScreen
import com.example.myapplication.ui.screen.WasteRegisterScreen
import com.example.myapplication.ui.screen.WasteRemoveScreen
import com.example.myapplication.utils.AuthEventBus
import com.example.myapplication.utils.DrawerContent
import kotlinx.coroutines.launch


/**
 * 3/11일 (강정훈)
 * 여기서 viewModel 한번에 많이 정의해둔 이유는 다른 페이지갔다가 돌아왔을때
 * 데이터가 남아있길 바라는 마음? 근데 어차피 매 페이지마다 api로 데이털르 받아올텐데
 * 추후에 필요없는거같으면 전부 지워도 됨
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Drawer 상태 관리
    val scope = rememberCoroutineScope() // Drawer 열고 닫기 위한 CoroutineScope

    // 현재 네비게이션 상태 확인
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route

    // 로그인/회원가입 화면에서는 TopBar 숨김
    val shouldShowTopBar = currentDestination !in listOf("login", "register")
    val shouldShowBackButton = currentDestination !in listOf("home")
    LaunchedEffect(Unit) {
        AuthEventBus.needLoginFlow.collect {
            navController.navigate("login") {
                popUpTo("auth_check") { inclusive = true } // 이전 화면 제거
                launchSingleTop = true
            }
        }
    }
    // 왼쪽 네비바 구현
    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(navController, drawerState) // Drawer 내부 UI
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                if (shouldShowTopBar) {
                    TopAppBar(
                        title = { Text("애버커스") },
                        navigationIcon = {
                            if (shouldShowBackButton) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "뒤로 가기"
                                    )
                                }
                            } else {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) { // 햄버거 메뉴 클릭 시 Drawer 열기
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Menu"
                                    )
                                }
                            }

                        },
                        actions = { // 우측 상단 버튼 추가
                            // 알림 버튼 추가
                            IconButton(onClick = {
                                //                            Toast.makeText(context, "알림 버튼 클릭됨!", Toast.LENGTH_SHORT).show()
                                navController.navigate("notification")
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Notifications,
                                    contentDescription = "Notifications"
                                )
                            }

                            IconButton(onClick = { navController.navigate("settings") }) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Settings"
                                )
                            }

                        }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "auth_check",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("auth_check") {
                    AuthCheckScreen(navController)
                }
                composable("login") { LoginScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("home") { HomeScreen(navController) }
                composable("waste_list") { WasteListScreen(navController) }
                composable("waste_register") { WasteRegisterScreen(navController) }
                composable("waste_move") { WasteMoveScreen(navController) }
                composable("waste_remove") { WasteRemoveScreen(navController) }
                dialog("settings") { SettingsDialog(navController) }
                dialog("notification") { NotificationDialog(navController) }
            }
        }
    }
}