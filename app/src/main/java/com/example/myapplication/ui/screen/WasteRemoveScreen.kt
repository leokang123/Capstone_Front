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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.user.User
import com.example.myapplication.data.waste.MoveRequest
import com.example.myapplication.data.waste.MoveRequests
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.viewmodel.WasteListViewModel
import com.example.myapplication.repository.WasteRepository
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.utils.UserDataStore
import com.example.myapplication.utils.getCurrentTime
import kotlinx.coroutines.launch

@Composable
fun WasteRemoveScreen(navController: NavController,
                    wasteListViewModel: WasteListViewModel = viewModel()
) {
    val context = LocalContext.current
    val userDataStore = UserDataStore(context)
    var user by remember { mutableStateOf<User?>(null) }
    val wasteItems by wasteListViewModel.wasteList.collectAsState() // 서버에서 폐기물 리스트 가져오기
    val selectedItems = remember { mutableStateMapOf<Long, MoveRequest>() } // 선택된 아이템 (id -> MoveRequest)
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var currentItemId by remember { mutableStateOf<Long?>(null) }
    var currentUserId by remember { mutableStateOf("") }
    var currentDetails by remember { mutableStateOf("") }
    var currentStatus by remember { mutableStateOf("") }
    var wasteItemDetails by remember { mutableStateOf("") }

    var wasteStorageList by remember { mutableStateOf<List<WasteStorage>>(emptyList()) }
    var selectedStorage by remember { mutableStateOf<WasteStorage?>(null) }
    // DropdownMenu 상태
    var expandedStorage by remember { mutableStateOf(false) }
    val wasteRepository = WasteRepository(context)

    val mockList = listOf(
        WasteStorage(id = 1, storageName = "기본 창고 A"),
        WasteStorage(id = 2, storageName = "기본 창고 B")
    )

    var authChecked by remember { mutableStateOf(false) }
    CheckAuth(navController, roleId = 2) {
        authChecked = true
    }

    // UI 로딩 시 폐기물 리스트 불러오기
    LaunchedEffect(authChecked) {
        if (!authChecked) return@LaunchedEffect
        user = userDataStore.getUser()
        currentUserId = user?.id.toString();
        try {
            val storageList = wasteRepository.getWasteStorage()
            wasteStorageList = storageList.takeIf { !it.isNullOrEmpty() } ?: mockList

        } catch (e: Exception) {
            Log.e("WasteRemoveScreen", e.message.toString())
            Toast.makeText(context, "배출화면 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
            DropdownMenu(expanded = expandedStorage, onDismissRequest = { expandedStorage = false }) {
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
        )   {
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
                                    currentStatus = wasteItem.status // 현재 상태 저장
                                    wasteItemDetails = wasteItem.wasteDetails.toString()
                                    showDialog = true // 팝업창 띄우기
                                } else {
                                    selectedItems.remove(wasteItem.id) // 체크 해제 시 삭제
                                }
                            }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = wasteItem.registrantName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = wasteItem.wasteType  + " (" + wasteItem.selectedDate + ")",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "비콘이름: ${wasteItem.selectedDevice}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "상세내역: ${wasteItem.wasteDetails}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "상태: ${wasteItem.status}",
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
                    val moveRequests = MoveRequests(stepId = 2, wasteMoveRequests = selectedItems.values.toList())
                    var responseMessage = ""
                    if (selectedItems.isNotEmpty()) {
                        try {
                            wasteRepository.moveWasteItems(moveRequests)
                            responseMessage = "폐기물 마지막단계 처리 완료"
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
                Column {
                    Text(
                        text = "현재 상태: $currentStatus",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "이전 상세내역: $wasteItemDetails",
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
                    if (currentItemId != null && currentUserId.isNotEmpty()) {
                        selectedItems[currentItemId!!] = MoveRequest(
                            itemId = currentItemId!!,
                            wasteDetails = currentDetails,
                            date = getCurrentTime() // 현재 시간 갱신
                        )
                        currentStatus = ""
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
                    currentStatus = ""
                    currentDetails = ""
                    wasteItemDetails = ""
                }) {
                    Text("취소")
                }
            }
        )
    }
}
