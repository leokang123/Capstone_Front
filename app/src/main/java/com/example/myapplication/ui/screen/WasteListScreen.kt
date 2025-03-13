package com.example.myapplication.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.WasteItemResponse
import com.example.myapplication.ui.component.CheckAuth
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.delay

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
    var showDialog by remember { mutableStateOf(false) } // ✅ 팝업 상태 관리
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedItem by remember { mutableStateOf<WasteItemResponse?>(null) }
    var showDropdown by remember { mutableStateOf(false) }

    val filteredItems by wasteListViewModel.filteredItems.collectAsState() // ✅ ViewModel에서 데이터 가져오기

    CheckAuth(navController) // ✅ 인증 체크

    /**
     *  사용자 입력을 특정시간동안 기다렸다가 검색 실행 (API 최적화)
     */
    LaunchedEffect(searchText.text) {
        if (searchText.text.isBlank()) {
            showDropdown = false
            return@LaunchedEffect
        }

        delay(500) // ✅ 0.5초 대기 후 검색 실행
        wasteListViewModel.searchWasteByName(searchText.text)
        showDropdown = filteredItems.isNotEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ✅ 검색 입력창
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            label = { Text("검색어를 입력하세요") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ 검색어가 입력될 때 자동완성 하단 바 표시
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
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)), // 연한 초록색 배경
                            border = BorderStroke(1.dp, Color.Gray) // ✅ 테두리 추가
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                                        append("🗑 폐기물 유형: ")
                                    }
                                    append(item.wasteType + "\n")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Blue)) {
                                        append("👤 등록자: ")
                                    }
                                    append(item.registrantName + "\n")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = Color.DarkGray)) {
                                        append("📍 위치: ")
                                    }
                                    append(item.location + "\n")

                                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = Color.Red)) {
                                        append("📅 날짜: ")
                                    }
                                    append(item.selectedDate)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedItem = item
                                        searchText = TextFieldValue("") // ✅ 선택하면 입력창 초기화
                                        showDropdown = false
                                    }
                                    .padding(16.dp)
                            )
                        }

                        // ✅ 각 리스트 항목 아래 구분선(Delimiter) 추가 (마지막 항목 제외)
                        if (index != filteredItems.lastIndex) {
                            Divider(
                                color = Color.Gray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 선택된 폐기물 상세 정보 표시
        selectedItem.let {
            Text(
                text = "조회된 정보: ${it?.wasteType}",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResultList(it)
        }
    }
}

@Composable
fun ResultList(selectedItem: WasteItemResponse?) {
    selectedItem?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.Gray) // ✅ 테두리 추가
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // ✅ 폐기물 기본 정보
                Text(
                    text = "🗑 ${selectedItem.wasteType}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow("👤 등록자", selectedItem.registrantName, Color.Blue)
                InfoRow("📍 위치", selectedItem.location, Color.DarkGray)
                InfoRow("📅 수거일", selectedItem.selectedDate, Color.Red)
                InfoRow("🔍 상세 정보", selectedItem.wasteDetails ?: "없음", Color.Gray)
                InfoRow("⚙ 사용 기기", selectedItem.selectedDevice ?: "없음", Color.Green)
                InfoRow("📌 상태", selectedItem.status, Color.Magenta)
            }
        }
    }
}

// ✅ 개별 정보 항목을 정리하는 Composable
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
        Divider(color = Color.LightGray, thickness = 0.5.dp)
    }
}