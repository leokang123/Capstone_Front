package com.example.myapplication.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.ui.component.SearchFilterDialog
import com.example.myapplication.ui.component.WasteItemDetailComponent
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.utils.getAutoTextColor
import com.example.myapplication.utils.getStatusColor
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
    wasteListViewModel: WasteListViewModel = hiltViewModel()
) {
    val selectedItem by wasteListViewModel.selectedItem.collectAsState()
    var showDropdown by remember { mutableStateOf(false) }

    var showFilterDialog by remember { mutableStateOf(false) } // 필터 팝업 상태
    var searchFilter by remember { mutableStateOf(SearchRequest()) } // 검색 필터 데이터
    var isSelected by remember { mutableStateOf(false) }

    val filteredItems by wasteListViewModel.wasteList.collectAsState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val wasteTypeList by wasteListViewModel.wasteTypeList.collectAsState()
    val beaconList by wasteListViewModel.beaconList.collectAsState()
    val wasteStorageList by wasteListViewModel.wasteStorageList.collectAsState()
    val wasteStatusList by wasteListViewModel.wasteStatusList.collectAsState()
    val bgColor = MaterialTheme.colorScheme.primary
    val textColor = getAutoTextColor(bgColor)

    var authChecked by remember { mutableStateOf(false) }
    var wasteIdText by remember { mutableStateOf<String>("") }

    val iconSize =
        with(LocalDensity.current) { MaterialTheme.typography.displaySmall.fontSize.toDp() }

    CheckAuth(navController, role = Roles.USER) {
        authChecked = true
    }
// 최초 1회만 실행되는 로직
    LaunchedEffect(authChecked) {
        if (authChecked) {
            wasteListViewModel.resetWasteList()
            delay(500)
            wasteListViewModel.searchWasteItems(searchFilter)
        }
    }

    LaunchedEffect(wasteIdText, authChecked) {
        if (!authChecked) return@LaunchedEffect

        if (searchFilter.wasteId == null) {
            showDropdown = false
            return@LaunchedEffect
        }

        if (!showFilterDialog) {
            wasteListViewModel.resetWasteList()
            delay(500)
            wasteListViewModel.searchWasteItems(searchFilter)
        }
    }

    LaunchedEffect(filteredItems, authChecked) {
        if (!authChecked) return@LaunchedEffect
        showDropdown = filteredItems.isNotEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp) // ✅ 너비 제한
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = wasteIdText,
                onValueChange = {
                    wasteIdText = it
                    searchFilter = searchFilter.copy(wasteId = wasteIdText)
                    wasteListViewModel.resetWasteList()
                    isSelected = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                    )
                },
                label = { Text("폐기물 ID를 입력하세요") },
                modifier = Modifier.weight(1f) // 검색창이 대부분을 차지하게 함
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 필터 버튼
            IconButton(
                onClick = { showFilterDialog = true },
                ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = "필터",
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        // 검색 필터 팝업
        if (showFilterDialog) {
            SearchFilterDialog(
                searchFilter = searchFilter,
                onFilterChange = { searchFilter = it },
                wasteListViewModel,
                onDismiss = {
                    isSelected = false
                    showFilterDialog = false
                },
                onApplyFilter = {
                    wasteListViewModel.resetWasteList()
                    wasteListViewModel.searchWasteItems(searchFilter)
                    isSelected = false
                    showFilterDialog = false
                }
            )
        }
        // 검색어가 입력될 때 자동완성 하단 바 표시
        if (!isSelected && filteredItems.isNotEmpty()) {
            LazyColumn( // Column 대신 LazyColumn 사용 (스크롤 가능)
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(filteredItems) { index, item ->  // itemsIndexed 사용
                    val dropBarWasteType = wasteTypeList.find { it.id == item.wasteTypeId }
                    val dropBarWasteStatus = wasteStatusList.find { it.id == item.wasteStatusId }
                    val dropBarWasteStorage = wasteStorageList.find { it.id == item.storageId }
                    val dropBarBeacon = beaconList.find { it.id == item.beaconId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                scope.launch {
                                    wasteListViewModel.getWasteItemDetails(item.id ?: "")
                                }
                                keyboardController?.hide()  // 키보드 내리기
                                focusManager.clearFocus()  // 입력 포커스 해제
                                showDropdown = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.8f
                            )
                        ),
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // 폐기물 ID
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "폐기물 ID",
                                    tint = textColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "폐기물 아이디",
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = item.id.toString(), color = textColor)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // 폐기물 유형
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "폐기물 유형",
                                    tint = textColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "폐기물 유형",
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = dropBarWasteType?.typeName ?: "", color = textColor)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // 저장 위치
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Inventory2,
                                    contentDescription = "저장 위치",
                                    tint = textColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "저장위치",
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = dropBarWasteStorage?.storageName ?: "",
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // 현재 상태
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Cached,
                                    contentDescription = "현재 상태",
                                    tint = textColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "현재 상태",
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = dropBarWasteStatus?.description ?: "",
                                    color = getStatusColor(dropBarWasteStatus),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // 비콘
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = "비콘",
                                    tint = textColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "비콘",
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = dropBarBeacon?.label ?: "", color = textColor)
                            }
                        }

                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 기존
        selectedItem?.let {
            isSelected = true
            WasteItemDetailComponent(it, wasteListViewModel)
        }
    }
}


