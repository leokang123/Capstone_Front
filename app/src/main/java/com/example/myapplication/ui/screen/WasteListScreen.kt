package com.example.myapplication.ui.screen

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.waste.WasteDetailResponse
import com.example.myapplication.data.waste.WasteItemDetailResponse
import com.example.myapplication.ui.component.CheckAuth
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 폐기물 목록 조회 창
 * 3/11일 기준 (강정훈)
 * 아직 api를 통해 폐기물 조회 및 리스트업하는건 미구현
 * 검색 기능만 구현
 * 고급 검색기능도 아직 미구현
 * 상세 조회 창 띄우는것도 아직 미구현
 */

@Composable
fun WasteListScreen(
    navController: NavController,
    wasteListViewModel: WasteListViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val selectedItem by wasteListViewModel.selectedItem.collectAsState()
    var showDropdown by remember { mutableStateOf(false) }


    val filteredItems by wasteListViewModel.wasteList.collectAsState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    CheckAuth(navController) // 인증 체크

    LaunchedEffect(searchText.text) {
        if (searchText.text.isBlank()) {
            showDropdown = false
            return@LaunchedEffect
        }

        delay(500) // 0.5초 대기 후 검색 실행
        wasteListViewModel.searchWasteByName(searchText.text)
        showDropdown = filteredItems.isNotEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 검색 입력창
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            label = { Text("검색어를 입력하세요") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 검색어가 입력될 때 자동완성 하단 바 표시
        if (searchText.text.isNotEmpty() && filteredItems.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    filteredItems.forEachIndexed { index, item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    scope.launch {
                                        wasteListViewModel.getWasteItemDetails(item.id)
                                    }
                                    keyboardController?.hide()  // 키보드 내리기
                                    focusManager.clearFocus()  // 입력 포커스 해제
                                    searchText = TextFieldValue("") // 선택하면 입력창 초기화
                                    showDropdown = false
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                                        append("🗑 폐기물 유형: ")
                                    }
                                    append(item.wasteType + "\n")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Blue)) {
                                        append("👤 처리자: ")
                                    }
                                    append(item.registrantName + "\n")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = Color.DarkGray)) {
                                        append("📍 발생위치: ")
                                    }
                                    append(item.location + "\n")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = Color(0xFFD2B48C))) {
                                        append("📦 저장위치: ")
                                    }
                                    append(item.storageName + "\n")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = Color.Red)) {
                                        append("📅 날짜: ")
                                    }
                                    append(item.selectedDate)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 선택된 폐기물 상세 정보 표시
        selectedItem?.let {
            ResultList(it, wasteListViewModel)
        }
    }
}

@Composable
fun ResultList(selectedItem: WasteItemDetailResponse, wasteListViewModel: WasteListViewModel) {
    val scope = rememberCoroutineScope()
    var showModDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 폐기물 기본 정보
            Text(
                text = "🗑 ${selectedItem.wasteType}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow("👤 등록자", selectedItem.registrantName, Color.Blue)
            InfoRow("📍 위치", selectedItem.location, Color.DarkGray)
            InfoRow("📦 저장위치", selectedItem.wasteStorage?.storageName.toString(), Color(0xFFD2B48C))
            InfoRow("📅 발생일", selectedItem.selectedDate, Color.Red)
            InfoRow("⚙ 사용 기기", selectedItem.selectedDevice ?: "없음", Color.Green)

            Spacer(modifier = Modifier.height(16.dp))

            // 상세 내역 목록 표시
            Text(
                text = "📜 상세 내역",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                val detailsList = selectedItem.wasteDetails.sortedByDescending { it.date }

                itemsIndexed(detailsList) { index, detail ->
                    WasteDetailCard(detail, detail.status == selectedItem.status)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            showModDialog =  true

                        }
                    }
                ) {
                    Text("정정")
                }
                Button(
                    onClick = {
                        scope.launch {
                            showDeleteDialog =  wasteListViewModel.checkItemStatus(selectedItem.id)
                            if (!showDeleteDialog) {
                                Toast.makeText(context, "COLLECTING 상태에서만 수정 및 삭제가 가능합니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("삭제")
                }
            }
        }
    }

    // 정정 다이얼로그 (예제 코드, 원하는 Composable로 변경 가능)
    if (showModDialog) {
        WasteEditDialog(wasteListViewModel, selectedItem = selectedItem) {
            wasteListViewModel.getWasteItemDetails(selectedItem.id)
            showModDialog = false
        }
    }

    // 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("삭제 확인") },
            text = { Text("이 항목을 삭제하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                wasteListViewModel.deleteItem(selectedItem.id) // 삭제 처리
                                Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Log.e(TAG, e.message.toString())
                                Toast.makeText(context, "에러 발생", Toast.LENGTH_SHORT).show()
                            } finally {
                                wasteListViewModel.resetWasteList()
                            }
                            showDeleteDialog = false
                        }
                    }
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}


// 개별 정보 항목을 정리하는 Composable
@Composable
fun InfoRow(label: String, value: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                    append("$label: ")
                }
                append(value)
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
    }
}

@Composable
fun WasteDetailCard(detail: WasteDetailResponse, isLatest: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLatest) Color(0xFFFFF3E0) else Color.White // 최신 상태 강조
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, if (isLatest) Color.Red else Color.Gray) // 최신 상태 강조
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 등록한 사용자 정보 추가
            Text(
                text = "👤 처리자: ${detail.user.name}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Blue
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 상태 정보
            Text(
                text = "📌 상태: ${detail.status}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isLatest) Color.Red else Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 상세 내용
            Text(
                text = "📝 내용: ${detail.wasteDetails}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 기록 시간
            Text(
                text = "📅 기록 시간: ${detail.date}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}