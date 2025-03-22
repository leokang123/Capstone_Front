package com.example.myapplication.ui.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.BluetoothViewModel
import com.example.myapplication.viewmodel.MockBluetoothDevice
import com.example.myapplication.viewmodel.SharedViewModel

/**
 * 블루투스 모달창
 * 3/11일 기준 (강정훈)
 * 안드로이드 애뮬레이터로 블루투스 기능을 지원하지 않는다해서 현재로서는 실험할 방법 없음
 * 따라서 더미데이터 사용하여 출력이 비슷하게 구현해놓음 (코드 보면 실제 폰과 더미데이터용 코드를 나누어놓음)
 * 블루투스 스캔 기능까지 구현은 했음(되는지는 모름)
 *
 */

// 아래 BlueToothScreen 컴포저블을 모달창으로 만드는 컴포저블
@Composable
fun BluetoothDialog(
    targetViewModel: SharedViewModel,
    viewModel: BluetoothViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            BluetoothScreen(targetViewModel, viewModel, onDismiss)
        }
        Button(
            onClick = { onDismiss() }, // 버튼 클릭 시 모달 닫기
        ) {
            Text("닫기")
        }
    }
}

/**
 * BlueToothScreen 컴포저블 (전체 화면으로 사용가능)
 * targetViewModel: 블루투스 기기 선택을 한 결과값을 저장하는 viewModel
 * viewModel: 블루투스 창에서 바뀐정보 저장하는 viewModel (안쓰일수도 있음, 추후에 필요없으면 없애기)
 * onDismiss: 종료 함수
 */

@Composable
fun BluetoothScreen(targetViewModel: SharedViewModel, viewModel: BluetoothViewModel = viewModel(), onDismiss: () -> Unit) {
    // 실제 폰
    // val devices by viewModel.devices.collectAsState()
    // var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    // 에뮬레이터 한정
    val devices by viewModel.mockDevices.collectAsState()
    var selectedDevice by remember { mutableStateOf<MockBluetoothDevice?>(null) }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Nearby Bluetooth Devices", style = MaterialTheme.typography.headlineMedium)

        // 블루투스 스캔 버튼
        Button(
            onClick = {

                viewModel.startScan()
                      },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Scan Devices")
        }

        // 블루투스 장치 목록 표시
        LazyColumn {
            items(devices) { device ->
                DeviceItem(device) { selected ->
                    targetViewModel.selectDevice(selected.name + " " + selected.address)
                    selectedDevice = selected
                    onDismiss()
                }
            }
        }
        Button(
            onClick = { viewModel.stopScan() },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Stop Scanning")
        }
    }
}
// 실제 폰
//@Composable
//fun DeviceItem(device: BluetoothDevice, onClick: (BluetoothDevice) -> Unit) {
//    val context = LocalContext.current
//
//    // BLUETOOTH_CONNECT 권한 체크
//    val deviceName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//            device.name ?: "Unknown Device"
//        } else {
//            "Permission Required"
//        }
//    } else {
//        device.name ?: "Unknown Device"
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick(device) }
//            .padding(16.dp)
//    ) {
//        Column {
//            Text(text = deviceName, style = MaterialTheme.typography.bodyLarge)
//            Text(text = device.address, style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}


// 에뮬레이터 용
@Composable
fun DeviceItem(device: MockBluetoothDevice, onClick: (MockBluetoothDevice) -> Unit) {
    val context = LocalContext.current

    // BLUETOOTH_CONNECT 권한 체크
    val deviceName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            device.name
        } else {
            "Permission Required"
        }
    } else {
        device.name
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(device) }
            .padding(16.dp)
    ) {
        Column {
            Text(text = deviceName, style = MaterialTheme.typography.bodyLarge)
            Text(text = device.address, style = MaterialTheme.typography.bodySmall)
        }
    }
}