package com.example.myapplication.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.waste.WasteItemDetailResponse
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.repository.impl.WasteRepositoryImpl
import com.example.myapplication.viewmodel.SharedViewModel
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteEditDialog(
    wasteListViewModel: WasteListViewModel,
    sharedViewModel: SharedViewModel = hiltViewModel(),
    selectedItem: WasteItemDetailResponse,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val wasteStorageList by wasteListViewModel.wasteStorageList.collectAsState()

    // 상태 관리 (선택된 값)
    var wasteType by remember { mutableStateOf(selectedItem.wasteType) }
    var location by remember { mutableStateOf(selectedItem.location) }
    var selectedDevice by remember { mutableStateOf(selectedItem.selectedDevice) }
    var wasteStorage by remember { mutableStateOf(selectedItem.wasteStorage) }

    // DropdownMenu 상태 (하단부에서 펼쳐지도록)
    var expandedType by remember { mutableStateOf(false) }
    var expandedStorage by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) } // 블루투스 검색 다이얼로그
    // 선택 가능한 목록
    val wasteTypes = listOf(
        "격리 의료 폐기물",
        "위해 의료 폐기물 / 조직물류 폐기물",
        "위해 의료 폐기물 / 병리계 폐기물",
        "위해 의료 폐기물 / 손상성 폐기물",
        "위해 의료 폐기물 / 생물·화학 폐기물",
        "위해 의료 폐기물 / 혈액오염 폐기물",
        "일반 의료 폐기물"
    )

    val mockList = listOf(
        WasteStorage(id = 1, storageName = "기본 창고 A"),
        WasteStorage(id = 2, storageName = "기본 창고 B")
    )
    var wasteDetailsList = remember {
        mutableStateListOf(*selectedItem.wasteDetails.toTypedArray())
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("정정 요청") },
        text = {
            Column {
                // 폐기물 종류 선택
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedType = true }
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = wasteType,
                            color = if (expandedType) Color.Gray else Color.Black
                        )
                    }
                    DropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }) {
                        wasteTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    wasteType = type
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 창고 선택
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedStorage = true }
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = wasteStorage?.storageName.toString(),
                            color = if (expandedStorage) Color.Gray else Color.Black
                        )
                    }
                    DropdownMenu(
                        expanded = expandedStorage,
                        onDismissRequest = { expandedStorage = false }) {
                        wasteStorageList.forEach { storage ->
                            DropdownMenuItem(
                                text = { Text(storage.storageName.toString()) },
                                onClick = {
                                    wasteStorage = storage
                                    expandedStorage = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 블루투스 기기 선택
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("블루투스 선택: $selectedDevice")
                }

                // 블루투스 검색 다이얼로그
                if (showDialog) {
                    BluetoothDialog(sharedViewModel, onDismiss = {
                        showDialog = false
                        selectedDevice = sharedViewModel.selectedBluetoothDevice ?: "기기 없음"
                    })
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 위치 입력
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("위치") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 세부 내용 수정
                Text("세부 내용 수정")
                LazyColumn {
                    itemsIndexed(wasteDetailsList) { index, detail ->
                        OutlinedTextField(
                            value = detail.wasteDetails,
                            onValueChange = { newText ->
                                wasteDetailsList[index] = detail.copy(wasteDetails = newText)
                            },
                            label = { Text("${detail.status} 상세내용") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                scope.launch {
                    try {
                        val updatedItem = selectedItem.copy(
                            wasteType = wasteType,
                            location = location,
                            selectedDevice = selectedDevice,
                            wasteStorage = wasteStorage,
                            wasteDetails = wasteDetailsList
                        )
                        wasteListViewModel.updateItem(updatedItem)
                        Toast.makeText(context, "정정 성공", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("WasteEditDialog", e.message.toString())
                        Toast.makeText(context, "에러 발생", Toast.LENGTH_SHORT).show()
                    } finally {
                        onDismiss()
                    }
                }
            }) {
                Text("확인")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
