package com.example.myapplication

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.component.DrawerContent
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.screen.HomeScreen
import com.example.myapplication.ui.screen.DetailScreen
import com.example.myapplication.ui.screen.PopUpScreen
import com.example.myapplication.ui.screen.LoginScreen
import com.example.myapplication.ui.screen.RegisterScreen
import com.example.myapplication.viewmodel.DetailViewModel
import com.example.myapplication.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.screen.NotificationDialog
import com.example.myapplication.ui.screen.SettingsDialog
import com.example.myapplication.ui.screen.WasteListScreen
import com.example.myapplication.ui.screen.WasteMoveScreen
import com.example.myapplication.ui.screen.WasteRegisterScreen
import com.example.myapplication.ui.screen.WasteRemoveScreen
import com.example.myapplication.viewmodel.SharedViewModel
import com.example.myapplication.viewmodel.WasteListViewModel
import com.example.myapplication.viewmodel.WasteMoveViewModel
import com.example.myapplication.viewmodel.WasteRegisterViewModel
import com.example.myapplication.viewmodel.WasteRemoveViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestBluetoothPermissions()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
    override fun onResume() {
        super.onResume()

        // finish app if the BLE is not supported
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE 미지원",Toast.LENGTH_SHORT).show()
            Log.d(TAG,"BLE 미지원")
        }
    }

    // ✅ 블루투스 권한 요청 함수
    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // ✅ Android 12(API 31) 이상에서만 필요
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )

            val permissionsToRequest = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (permissionsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 1)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // ✅ Drawer 상태 관리
    val scope = rememberCoroutineScope() // ✅ Drawer 열고 닫기 위한 CoroutineScope
    val detailViewModel: DetailViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val wasteListViewModel: WasteListViewModel = viewModel()
    val wasteRegisterViewModel: WasteRegisterViewModel = viewModel()
    val wasteMoveViewModel: WasteMoveViewModel = viewModel()
    val wasteRemoveViewModel: WasteRemoveViewModel = viewModel()
    val sharedViewModel: SharedViewModel = viewModel()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(navController, drawerState) // ✅ Drawer 내부 UI
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("애버커스") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) { // ✅ 햄버거 메뉴 클릭 시 Drawer 열기
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = { // ✅ 우측 상단 버튼 추가
                        // ✅ 알림 버튼 추가
                        IconButton(onClick = {
                            Toast.makeText(context, "알림 버튼 클릭됨!", Toast.LENGTH_SHORT).show()
                            navController.navigate("notification")
                        }) {
                            Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Notifications")
                        }

                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                        }

                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("login") {
                    homeViewModel.reset()
                    detailViewModel.reset()
                    LoginScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("home") { HomeScreen(navController, homeViewModel) }
                composable("detail") { DetailScreen(navController, detailViewModel) }
                composable("waste_list") { WasteListScreen(navController, wasteListViewModel) }
                composable("waste_register") { WasteRegisterScreen(navController, wasteRegisterViewModel) }
                composable("waste_move") { WasteMoveScreen(navController, wasteMoveViewModel) }
                composable("waste_remove") { WasteRemoveScreen(navController, wasteRemoveViewModel) }
//                dialog("bluetooth_scan") { BluetoothDialog(navController, sharedViewModel) }
                dialog("popup") { PopUpScreen(navController) }
                dialog("settings") { SettingsDialog(navController) }
                dialog("notification") { NotificationDialog(navController) }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    MyApplicationTheme {
        AppNavigation()
    }
}