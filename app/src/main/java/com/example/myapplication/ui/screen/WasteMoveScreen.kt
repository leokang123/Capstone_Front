package com.example.myapplication.ui.screen

/**
 * 폐기물 처리 창
 * 3/11(강정훈)
 * 아직 미구현 (디폴트창(DetailScreen) 넣어놓은게 고작)
 */

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.data.waste.MoveRequest
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.viewmodel.BlueToothViewModel
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun reloadWasteList(
    blueToothViewModel: BlueToothViewModel,
    wasteListViewModel: WasteListViewModel,
) {
    blueToothViewModel.startScan()

    // 최대 5초 동안 200ms 간격으로 체크
    val startTime = System.currentTimeMillis()

    val storageBeaconList = wasteListViewModel.storageBeaconList
    while (System.currentTimeMillis() - startTime < 5000L) {
        val serverBeacon = blueToothViewModel.serverBeacons.value.filterNot { it.deviceAddress in storageBeaconList }
        if (serverBeacon.isNotEmpty()) {
            // 혹시 모르니까 1초 더 기다림
            delay(1000L)
            val beaconAddressList =
                blueToothViewModel.serverBeacons.value.map { it.deviceAddress }.toList()
            Log.d("BEACONLIST", beaconAddressList.toString())
            // 혹시나 더 있을수도 있으니까 1.5초 더 기다림
            wasteListViewModel.fetchWasteList(mode = 2, beaconAddressList)
            return
        }
        delay(200L)
    }

    // 5초 내에 발견 못했을 경우
    Log.w("RELOAD", "비콘을 찾지 못했습니다.")
}

@Composable
fun WasteMoveScreen(
    navController: NavController,
    wasteListViewModel: WasteListViewModel = hiltViewModel(),
    blueToothViewModel: BlueToothViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val user by wasteListViewModel.user.collectAsState()
    val wasteTypeList by wasteListViewModel.wasteTypeList.collectAsState()
    val beaconList by wasteListViewModel.beaconList.collectAsState()
    val storageList by wasteListViewModel.wasteStorageList.collectAsState()
    val wasteStatusList by wasteListViewModel.wasteStatusList.collectAsState()


    val wasteItems by wasteListViewModel.wasteList.collectAsState() // 서버에서 폐기물 리스트 가져오기
    val selectedItems =
        remember { mutableStateMapOf<String, String>() } // 선택된 아이템 (id -> MoveRequest)
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var currentItemStorageId by remember { mutableStateOf<Int?>(null) }
    var currentItemId by remember { mutableStateOf<String?>(null) }
    var currentUserId by remember { mutableStateOf("") }
    var currentDetails by remember { mutableStateOf("") }
    var currentStatusId by remember { mutableStateOf<Int?>(null) }
    val currentStatus by remember(currentStatusId, wasteStatusList) {
        derivedStateOf {
            wasteStatusList.find { it.id == currentStatusId }
        }
    }
    var wasteItemDetails by remember { mutableStateOf("") }
    var authChecked by remember { mutableStateOf(false) }
    var isStorageMatch by remember { mutableStateOf<Boolean?>(null) }
    val isLoading by wasteListViewModel.isLoading.collectAsState()

    CheckAuth(navController, role = Roles.USER) {
        authChecked = true
    }

    // UI 로딩 시 폐기물 리스트 불러오기
    LaunchedEffect(authChecked) {
        if (!authChecked) return@LaunchedEffect
        currentUserId = user?.uuid.toString()
        wasteListViewModel.isLoading(true)
        reloadWasteList(blueToothViewModel, wasteListViewModel)
        wasteListViewModel.isLoading(false)
    }

    LaunchedEffect(showDialog) {
        val storage = storageList.find { it.id == currentItemStorageId }
        val storageBeacon = beaconList.find { it.id == storage?.beacon }?.deviceAddress
        val statusLevel = currentStatus?.statusLevel
        Log.d("STORAGE_MATCH", storage.toString())

        if (showDialog && storageBeacon?.isNotBlank() == true && statusLevel == 2) {
            isStorageMatch = blueToothViewModel.checkStorage(storageBeacon)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(
            onClick = {
                coroutineScope.launch {
                    wasteListViewModel.isLoading(true)
                    reloadWasteList(blueToothViewModel, wasteListViewModel)
                    wasteListViewModel.isLoading(false)
                    Toast.makeText(context, "폐기물 목록이 새로 고침되었습니다", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.5f)
        ) {
            Text("새로고침")
        }

        if (isLoading)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))

        // 체크리스트 UI
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f) // 최대 높이 지정
        ) {
            if (!isLoading) {
                items(wasteItems) { wasteItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                currentItemId = wasteItem.id
                                currentItemStorageId = wasteItem.storageId
                                currentItemId = wasteItem.id
                                currentStatusId = wasteItem.wasteStatusId // 현재 상태 저장
                                Log.d("TEST_STATUS2",currentStatusId.toString())
                                wasteItemDetails = wasteItem.description
                                showDialog = true // 팝업창 띄우기
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedItems.containsKey(wasteItem.id),
                                onCheckedChange = { isChecked ->
                                    if (!isChecked){
                                        selectedItems.remove(wasteItem.id) // 체크 해제 시 삭제
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                val wasteType =
                                    wasteTypeList.find { it.id == wasteItem.wasteTypeId }
                                val beacon = beaconList.find { it.id == wasteItem.beaconId }
                                val status =
                                    wasteStatusList.find { it.id == wasteItem.wasteStatusId }
                                Text(
                                    text = wasteType?.typeName ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "비콘이름: ${beacon?.label}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "상세내역: ${wasteItem.description}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "상태: ${status?.description}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // 버튼: 선택한 폐기물 이동 요청
        Button(
            onClick = {
                coroutineScope.launch {
                    val moveRequests: List<MoveRequest> =
                        selectedItems.map { MoveRequest(it.key, it.value) }
                    var responseMessage = ""
                    if (selectedItems.isNotEmpty()) {
                        try {
                            wasteListViewModel.moveWasteItems(moveRequests)
                            responseMessage = "폐기물 다음단계 처리 완료"
                            Log.d("WasteMoveScreen", "이동 성공")
                        } catch (e: Exception) {
                            responseMessage = "처리 실패"
                            Log.e("WasteMoveScreen", responseMessage, e)
                        } finally {
                            wasteListViewModel.resetWasteList()
                            wasteListViewModel.isLoading(true)
                            reloadWasteList(blueToothViewModel, wasteListViewModel)
                            wasteListViewModel.isLoading(false)
                            selectedItems.clear() // 요청 성공 시 체크리스트 초기화
                            Toast.makeText(context, responseMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("선택한 폐기물 이동")
        }
    }

    // 팝업창 (다이얼로그)
    if (showDialog && currentItemId != null) {
        AlertDialog(
            onDismissRequest = {
                isStorageMatch = null
                showDialog = false
            },
            title = { Text("폐기물 이동 정보 입력") },
            text = {
                val status = currentStatus
                Log.d("TEST_STATUS", currentStatusId.toString())
                Column {
                    Text(
                        text = "현재 상태: ${status?.description}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "상세내역: $wasteItemDetails",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        enabled = false,
                        value = currentUserId,
                        onValueChange = { currentUserId = it },
                        label = { Text("등록한 사용자 ID") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = currentDetails,
                        onValueChange = { currentDetails = it },
                        label = { Text("처리 내용") }
                    )
                    if (status?.statusLevel == 2) {
                        Text(
                            text = when (isStorageMatch) {
                                true -> "창고 일치 여부: 일치함"
                                false -> "창고 일치 여부: 불일치"
                                null -> "창고 일치 여부 확인 중..."
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentItemId != null && currentUserId.isNotEmpty() == true) {
                            selectedItems[currentItemId!!] = currentDetails
                            currentStatusId = null
                            currentDetails = ""
                            wasteItemDetails = ""
                            isStorageMatch = null
                            showDialog = false
                        }
                    },
                    enabled = if (currentStatus?.statusLevel == 2) isStorageMatch == true else true,
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = {
                    currentStatusId = null
                    currentDetails = ""
                    wasteItemDetails = ""
                    isStorageMatch = null
                    showDialog = false
                }) {
                    Text("취소")
                }
            }
        )
    }
}
