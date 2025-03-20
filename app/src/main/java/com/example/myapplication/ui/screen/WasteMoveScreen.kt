package com.example.myapplication.ui.screen
/**
 * 폐기물 처리 창
 * 3/11(강정훈)
 * 아직 미구현 (디폴트창(DetailScreen) 넣어놓은게 고작)
 */

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.waste.MoveRequest
import com.example.myapplication.data.waste.MoveRequests
import com.example.myapplication.viewmodel.WasteListViewModel
import com.example.myapplication.repository.WasteRepository
import com.example.myapplication.ui.component.CheckAuth
import com.example.myapplication.ui.component.UserDataStore
import com.example.myapplication.ui.component.getCurrentTime
import kotlinx.coroutines.launch

@Composable
fun WasteMoveScreen(navController: NavController,
    wasteListViewModel: WasteListViewModel = viewModel()
) {
    val context = LocalContext.current
    val userDataStore = UserDataStore(context)
    val user = userDataStore.getUser()
    val wasteItems by wasteListViewModel.wasteList.collectAsState() // 서버에서 폐기물 리스트 가져오기
    val selectedItems = remember { mutableStateMapOf<Long, MoveRequest>() } // 선택된 아이템 (id -> MoveRequest)
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var currentItemId by remember { mutableStateOf<Long?>(null) }
    var currentUserId by remember { mutableStateOf(user?.id.toString()) }
    var currentDetails by remember { mutableStateOf("") }
    var currentStatus by remember { mutableStateOf("") }
    var wasteItemDetails by remember { mutableStateOf("") }




    val wasteRepository = WasteRepository(context)
    CheckAuth(navController)

    // UI 로딩 시 폐기물 리스트 불러오기
    LaunchedEffect(Unit) {
        wasteListViewModel.fetchWasteList(mode = 2)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("폐기물 이동", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // 체크리스트 UI
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f) // ✅ 최대 높이 지정
        )  {
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
                                    currentStatus = wasteItem.status // ✅ 현재 상태 저장
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
                    val moveRequests = MoveRequests(stepId = 1, wasteMoveRequests = selectedItems.values.toList())
                    var responseMessage = ""
                    try {
                        wasteRepository.moveWasteItems(moveRequests)
                        responseMessage = "폐기물 다음단계 처리 완료"
                        Log.d("WasteMoveScreen", "이동 성공")
                    } catch (e: Exception) {
                        responseMessage = "처리 실패"
                        Log.e("WasteMoveScreen", responseMessage, e)
                    } finally {
                        selectedItems.clear() // 요청 성공 시 체크리스트 초기화
                        Toast.makeText(context, responseMessage, Toast.LENGTH_SHORT).show()
                        wasteListViewModel.fetchWasteList(mode = 2)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("선택한 폐기물 이동")
        }
    }

    // ✅ 팝업창 (다이얼로그)
    if (showDialog && currentItemId != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("폐기물 등록 정보 입력") },
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
                            userId = currentUserId.toLong(),
                            wasteDetails = currentDetails,
                            date = getCurrentTime() // ✅ 현재 시간 갱신
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
