package com.example.myapplication.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.entity.Beacon
import com.example.myapplication.viewmodel.BlueToothViewModel

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
    viewModel: BlueToothViewModel = hiltViewModel(),
    isRegister: Boolean = false,
    onDismiss: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.updateBeaconList()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .padding(16.dp)

        ) {
            BluetoothScreen(viewModel, isRegister, onDismiss)
        }
    }
}

/**
 * BlueToothScreen 컴포저블 (전체 화면으로 사용가능)
 * viewModel: 블루투스 기기 선택을 한 결과값을 저장하는 viewModel
 * viewModel: 블루투스 창에서 바뀐정보 저장하는 viewModel (안쓰일수도 있음, 추후에 필요없으면 없애기)
 * onDismiss: 종료 함수
 */

@Composable
fun BluetoothScreen(
    viewModel: BlueToothViewModel,
    isRegister: Boolean,
    onDismiss: () -> Unit,
) {

    // 에뮬레이터 한정
    val devices by if (isRegister) viewModel.notUsedServerBeacon.collectAsState() else viewModel.serverBeacons.collectAsState()
    var selectedDevice by remember { mutableStateOf<Beacon?>(null) }

    val isLoading by viewModel.isScanning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("근처 비콘 검색", style = MaterialTheme.typography.headlineMedium)

        // 블루투스 스캔 버튼
        Button(
            onClick = {
                viewModel.startScan()
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Scan Devices")
        }
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // 블루투스 장치 목록 표시
        LazyColumn {
            items(devices) { device ->
                DeviceItem(device) { selected ->
                    viewModel.selectBeacon(selected.id)
                    selectedDevice = selected
                    onDismiss()
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        Text("10초간 검색합니다", color = MaterialTheme.colorScheme.onSurface)

    }
}


@Composable
fun DeviceItem(device: Beacon, onClick: (Beacon) -> Unit) {
    val context = LocalContext.current

    // BLUETOOTH_CONNECT 권한 체크
    val deviceName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            device.label ?: "이름 없음"
        } else {
            "Permission Required"
        }
    } else {
        device.label ?: "이름 없음"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(device) }
            .padding(16.dp)
    ) {
        Column {
            Text(text = deviceName, style = MaterialTheme.typography.bodyLarge)
            Text(text = device.deviceAddress, style = MaterialTheme.typography.bodySmall)
        }
    }
}