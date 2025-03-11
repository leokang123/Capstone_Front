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
import com.example.myapplication.ui.screen.BluetoothDialog
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.screen.HomeScreen
import com.example.myapplication.ui.screen.DetailScreen
import com.example.myapplication.ui.screen.PopUpScreen
import com.example.myapplication.ui.screen.LoginScreen
import com.example.myapplication.ui.screen.RegisterScreen
import com.example.myapplication.ui.screen.SettingsScreen
import com.example.myapplication.viewmodel.DetailViewModel
import com.example.myapplication.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.screen.NotificationScreen
import com.example.myapplication.ui.screen.TrashListScreen
import com.example.myapplication.ui.screen.TrashMoveScreen
import com.example.myapplication.ui.screen.TrashRegisterScreen
import com.example.myapplication.ui.screen.TrashRemoveScreen
import com.example.myapplication.viewmodel.TrashListViewModel
import com.example.myapplication.viewmodel.TrashMoveViewModel
import com.example.myapplication.viewmodel.TrashRegisterViewModel
import com.example.myapplication.viewmodel.TrashRemoveViewModel


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
    val trashListViewModel: TrashListViewModel = viewModel()
    val trashRegisterViewModel: TrashRegisterViewModel = viewModel()
    val trashMoveViewModel: TrashMoveViewModel = viewModel()
    val trashRemoveViewModel: TrashRemoveViewModel = viewModel()
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
                composable("trash_list") { TrashListScreen(navController, trashListViewModel) }
                composable("trash_register") { TrashRegisterScreen(navController, trashRegisterViewModel) }
                composable("trash_move") { TrashMoveScreen(navController, trashMoveViewModel) }
                composable("trash_remove") { TrashRemoveScreen(navController, trashRemoveViewModel) }
                dialog("bluetooth_scan") { BluetoothDialog {navController.popBackStack()} }
                dialog("popup") { PopUpScreen(navController) }
                dialog("settings") { SettingsScreen(navController) }
                dialog("notification") { NotificationScreen(navController) }
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