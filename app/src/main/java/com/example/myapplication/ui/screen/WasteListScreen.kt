package com.example.myapplication.ui.screen

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.delay

@Composable
fun WasteListScreen(navController: NavController, wasteListViewModel: WasteListViewModel = viewModel ()) {
    var showDialog by remember { mutableStateOf(false) }  // ✅ 팝업 상태 관리

    val allItems = listOf("Waste A", "Waste B", "Waste C", "Waste D", "Waste E") // ✅ 검색할 데이터 목록

    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedItem by remember { mutableStateOf<String?>(null) }
    var filteredItems by remember { mutableStateOf(listOf<String>()) }
    var showDropdown by remember { mutableStateOf(false) }

    // ✅ 사용자가 입력을 멈춘 후 0.5초 뒤에 자동완성 바 표시
    LaunchedEffect(searchText.text) {
        if (searchText.text.length == 1 || searchText.text.isBlank()) filteredItems = emptyList()
        showDropdown = false  // ✅ 새로운 입력이 발생하면 하단바 숨기기
        delay(500)  // ✅ 0.5초 대기 (사용자가 입력을 멈춘 후 실행)
        filteredItems = allItems.filter { it.contains(searchText.text, ignoreCase = true) }
        showDropdown = filteredItems.isNotEmpty()  // ✅ 검색 결과가 있을 때만 표시
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
                    filteredItems.forEach { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedItem = item // ✅ 선택된 아이템 저장
                                    searchText = TextFieldValue("") // ✅ 선택하면 입력창 초기화
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 결과 목록을 스크롤 가능한 형식으로 표시
        selectedItem?.let {
            Text(
                text = "조회된 정보: $it",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResultList(it)
        }
    }
}

// ✅ 선택한 항목의 상세 정보를 스크롤 가능한 리스트로 출력
@Composable
fun ResultList(selectedItem: String) {
    val details = List(20) { "$selectedItem - 상세 정보 $it" } // ✅ 샘플 데이터 (20개)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(details.size) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray)
            ) {
                Text(
                    text = details[index],
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}