package com.example.myapplication.ui.screen

/**
 * 폐기물 처리 창
 * 3/11(강정훈)
 * 아직 미구현 (디폴트창(DetailScreen) 넣어놓은게 고작)
 */

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.data.waste.MoveRequest
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.viewmodel.BlueToothViewModel
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SelectedWasteItem(
    val details: String,
    val beaconAddress: String?
)

@Composable
fun WasteRemoveScreen(
    navController: NavController,
    wasteListViewModel: WasteListViewModel = hiltViewModel(),
    blueToothViewModel: BlueToothViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user by wasteListViewModel.user.collectAsState()
    val wasteItems by wasteListViewModel.wasteList.collectAsState() // 서버에서 폐기물 리스트 가져오기
    val selectedItems =
        remember { mutableStateMapOf<String, SelectedWasteItem>() } // 선택된 아이템 (id -> MoveRequest)
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var currentItemId by remember { mutableStateOf<String?>(null) }
    var currentUserId by remember { mutableStateOf<String?>(null) }
    var currentDetails by remember { mutableStateOf("") }
    var currentDeviceAddress by remember { mutableStateOf<String?>("") }

    var currentStatusId by remember { mutableStateOf<Int?>(null) }
    var wasteItemDetails by remember { mutableStateOf("") }

    var selectedStorage by remember { mutableStateOf<WasteStorage?>(null) }
    // DropdownMenu 상태
    var expandedStorage by remember { mutableStateOf(false) }
    val wasteStorageList = wasteListViewModel.wasteStorageList
    val wasteStatusList = wasteListViewModel.wasteStatusList
    val wasteTypeList = wasteListViewModel.wasteTypeList
    val beaconList = wasteListViewModel.beaconList
    var isScanning by remember { mutableStateOf(false) }

    var authChecked by remember { mutableStateOf(false) }
    CheckAuth(navController, role = Roles.WAREHOUSE_MANAGER) {
        authChecked = true
    }

    // UI 로딩 시 폐기물 리스트 불러오기
    LaunchedEffect(authChecked, Unit) {
        if (!authChecked) return@LaunchedEffect
        currentUserId = user?.uuid.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("폐기물 배출", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))
        // 폐기물 종류 선택 (DropdownMenu)
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedStorage = true }
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedStorage?.storageName ?: "창고 선택",
                    color = if (selectedStorage == null || expandedStorage) Color.Gray else Color.Black
                )
            }
            DropdownMenu(
                expanded = expandedStorage,
                onDismissRequest = { expandedStorage = false }) {
                wasteStorageList.forEach { storage ->
                    DropdownMenuItem(text = { Text(storage.storageName.toString()) }, onClick = {
                        selectedStorage = storage
                        wasteListViewModel.fetchStorageWasteList(selectedStorage!!.id!!)
                        expandedStorage = false
                    })
                }
            }
        }

        // 체크리스트 UI
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f) // 최대 높이 지정
        ) {
            items(wasteItems) { wasteItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            currentItemId = wasteItem.id
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
                                val deviceAddress =
                                    wasteListViewModel.beaconList.find { it.id == wasteItem.beaconId }?.deviceAddress
                                if (isChecked) {
                                    currentItemId = wasteItem.id
                                    currentDeviceAddress = deviceAddress
                                    currentStatusId = wasteItem.wasteStatusId // 현재 상태 저장
                                    wasteItemDetails = wasteItem.description
                                    showDialog = true // 팝업창 띄우기
                                } else {
                                    selectedItems.remove(wasteItem.id) // 체크 해제 시 삭제
                                }
                            }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            val wasteType = wasteTypeList.find { it.id == wasteItem.wasteStatusId }
                            val beacon = beaconList.find { it.id == wasteItem.beaconId }
                            val status = wasteStatusList.find { it.id == wasteItem.wasteStatusId }
                            Text(
                                text = wasteType?.typeName ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "비콘이름: ${beacon?.label}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "상세내역: ${wasteItem.description}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "상태: ${status?.description}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                if (isScanning) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("비콘 검색 중...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // 버튼: 선택한 폐기물 이동 요청
        Button(
            onClick = {
                coroutineScope.launch {
                    isScanning = true

                    blueToothViewModel.clearServerBeacons()
                    blueToothViewModel.startScan()
                    delay(2000)
                    isScanning = false

                    val scannedAddresses =
                        blueToothViewModel.serverBeacons.value.map { it.deviceAddress }

                    val moveRequests = selectedItems
                        .filter { it.value.beaconAddress in scannedAddresses }
                        .map { MoveRequest(uuid = it.key, wasteDetails = it.value.details) }

                    val matchedUuids = moveRequests.map { it.uuid }.toSet()

                    val leftRequest = selectedItems
                        .filterNot { it.key in matchedUuids }
                        .map { it.key } // 또는 .map { it.value.details } 등 원하는 정보

                    Log.d("REMOVE_BEACONTEST", "남은거: $leftRequest 이동할거: $moveRequests")
                    if (leftRequest.isNotEmpty()) {
                        Toast.makeText(
                            context,
                            "${leftRequest}은 인식 범위 밖에 있습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                        selectedItems.clear()
                    }

                    var responseMessage = ""
                    Log.d("SELECTED_ITEM", scannedAddresses.toString())
                    if (moveRequests.isNotEmpty()) {
                        try {
                            wasteListViewModel.moveWasteItems(moveRequests)
                            responseMessage = "처리 완료"
                            Log.d("WasteRemoveScreen", "배출 성공")
                        } catch (e: Exception) {
                            responseMessage = "처리 실패"
                            Log.e("WasteRemoveScreen", responseMessage, e)
                        } finally {
                            selectedItems.clear() // 요청 성공 시 체크리스트 초기화
                            Toast.makeText(context, responseMessage, Toast.LENGTH_SHORT).show()
                            wasteListViewModel.fetchWasteList(mode = 3)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("선택한 폐기물 배출")
        }

    }


    // 팝업창 (다이얼로그)
    if (showDialog && currentItemId != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("폐기물 배출 정보 입력") },
            text = {
                val status = wasteStatusList.find { it.id == currentStatusId }
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
                        value = currentUserId.toString(),
                        onValueChange = { currentUserId = it },
                        label = { Text("등록한 사용자 ID") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = currentDetails,
                        onValueChange = { currentDetails = it },
                        label = { Text("상세 내용") }
                    )
                }

            },
            confirmButton = {
                Button(onClick = {
                    if (currentItemId != null && currentUserId?.isNotEmpty() == true) {
                        selectedItems[currentItemId!!] =
                            SelectedWasteItem(currentDetails, currentDeviceAddress)
                        currentStatusId = null
                        currentDetails = ""
                        currentDeviceAddress = ""
                        wasteItemDetails = ""
                        showDialog = false
                    }
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    currentStatusId = null
                    currentDetails = ""
                    currentDeviceAddress = ""
                    wasteItemDetails = ""
                }) {
                    Text("취소")
                }
            }
        )
    }
}


