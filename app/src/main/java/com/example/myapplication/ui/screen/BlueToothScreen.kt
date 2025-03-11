package com.example.myapplication.ui.screen

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.viewmodel.BluetoothViewModel
import com.example.myapplication.viewmodel.MockBluetoothDevice
import com.example.myapplication.viewmodel.SharedViewModel


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
            onClick = { onDismiss() }, // ✅ 버튼 클릭 시 모달 닫기
        ) {
            Text("닫기")
        }
    }
}

@Composable
fun BluetoothScreen(targetViewModel: SharedViewModel, viewModel: BluetoothViewModel = viewModel(), onDismiss: () -> Unit) {
    // 실제 폰
    // val devices by viewModel.devices.collectAsState()
    // var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    // 에뮬레이터 한정
    val devices by viewModel.mockDevices.collectAsState()
    var selectedDevice by remember { mutableStateOf<MockBluetoothDevice?>(null) }

    // ✅ 팝업 창 상태
    var showDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Nearby Bluetooth Devices", style = MaterialTheme.typography.headlineMedium)

        // ✅ 블루투스 스캔 버튼
        Button(
            onClick = { viewModel.startScan() },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Scan Devices")
        }

        // ✅ 블루투스 장치 목록 표시
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

//    // ✅ 장치를 클릭하면 팝업 표시
//    if (showDialog && selectedDevice != null) {
//        val context = LocalContext.current
//        val deviceName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//                selectedDevice?.name ?: "Unknown"
//            } else {
//                "Permission Required"
//            }
//        } else {
//            selectedDevice?.name ?: "Unknown"
//        }
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            title = { Text("Device: $deviceName") },
//            text = {
//                Column {
//                    Text("Address: ${selectedDevice?.address}")
//                    TextField(
//                        value = inputText,
//                        onValueChange = { inputText = it },
//                        label = { Text("Enter additional info") }
//                    )
//                }
//            },
//            confirmButton = {
//                Button(onClick = { showDialog = false }) {
//                    Text("OK")
//                }
//            }
//        )
//    }
}
// 실제 폰
//@Composable
//fun DeviceItem(device: BluetoothDevice, onClick: (BluetoothDevice) -> Unit) {
//    val context = LocalContext.current
//
//    // ✅ BLUETOOTH_CONNECT 권한 체크
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

    // ✅ BLUETOOTH_CONNECT 권한 체크
    val deviceName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            device.name ?: "Unknown Device"
        } else {
            "Permission Required"
        }
    } else {
        device.name ?: "Unknown Device"
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