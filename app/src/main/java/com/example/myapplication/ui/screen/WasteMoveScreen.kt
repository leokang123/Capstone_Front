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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.data.waste.MoveRequest
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.viewmodel.BlueToothViewModel
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.launch

fun reloadWasteList(
    blueToothViewModel: BlueToothViewModel,
    wasteListViewModel: WasteListViewModel,
) {
    blueToothViewModel.startScan()
    val beaconAddressList = blueToothViewModel.serverBeacons.value.map { it.deviceAddress }.toList()
    Log.d("BEACONLIST", beaconAddressList.toString())
    wasteListViewModel.fetchWasteList(mode = 2, beaconAddressList)
}

@Composable
fun WasteMoveScreen(
    navController: NavController,
    wasteListViewModel: WasteListViewModel = hiltViewModel(),
    blueToothViewModel: BlueToothViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val user by wasteListViewModel.user.collectAsState()
    val serverBeacons by blueToothViewModel.serverBeacons.collectAsState()
    val wasteTypeList = wasteListViewModel.wasteTypeList
    val wasteStatusList = wasteListViewModel.wasteStatusList
    val beaconList = wasteListViewModel.beaconList

    val wasteItems by wasteListViewModel.wasteList.collectAsState() // 서버에서 폐기물 리스트 가져오기
    val selectedItems =
        remember { mutableStateMapOf<String, String>() } // 선택된 아이템 (id -> MoveRequest)
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var currentItemId by remember { mutableStateOf<String?>(null) }
    var currentUserId by remember { mutableStateOf("") }
    var currentDetails by remember { mutableStateOf("") }
    var currentStatusId by remember { mutableStateOf<Int?>(null) }
    var wasteItemDetails by remember { mutableStateOf("") }
    var authChecked by remember { mutableStateOf(false) }


    CheckAuth(navController, role = Roles.USER) {
        authChecked = true
    }

    // UI 로딩 시 폐기물 리스트 불러오기
    LaunchedEffect(authChecked, serverBeacons) {
        if (!authChecked) return@LaunchedEffect
        currentUserId = user?.uuid.toString()
        reloadWasteList(blueToothViewModel, wasteListViewModel)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("폐기물 이동", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))
// 새로고침 버튼
        Button(
            onClick = {
                coroutineScope.launch {
                    reloadWasteList(blueToothViewModel, wasteListViewModel)
                    Toast.makeText(context, "폐기물 목록이 새로 고침되었습니다", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("폐기물 목록 새로고침")
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
                                if (isChecked) {
                                    currentItemId = wasteItem.id
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
                            selectedItems.clear() // 요청 성공 시 체크리스트 초기화
                            Toast.makeText(context, responseMessage, Toast.LENGTH_SHORT).show()
                            reloadWasteList(blueToothViewModel, wasteListViewModel)
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
            onDismissRequest = { showDialog = false },
            title = { Text("폐기물 이동 정보 입력") },
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
                        value = currentUserId,
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
                    if (currentItemId != null && currentUserId.isNotEmpty() == true) {
                        selectedItems[currentItemId!!] = currentDetails
                        currentStatusId = null
                        currentDetails = ""
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
                    wasteItemDetails = ""
                }) {
                    Text("취소")
                }
            }
        )
    }
}
