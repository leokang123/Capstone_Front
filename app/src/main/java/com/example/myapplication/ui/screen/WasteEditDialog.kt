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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.waste.WasteItem
import com.example.myapplication.data.waste.WasteItemDetails
import com.example.myapplication.viewmodel.BlueToothViewModel
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteEditDialog(
    wasteListViewModel: WasteListViewModel,
    beaconViewModel: BlueToothViewModel = hiltViewModel(),
    selectedItem: WasteItemDetails,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val wasteStorageList = wasteListViewModel.wasteStorageList
    val wasteTypeList = wasteListViewModel.wasteTypeList
    val beaconList = wasteListViewModel.beaconList

    // 상태 관리 (선택된 값)
    var selectedWasteTypeId by remember { mutableStateOf(selectedItem.wasteType) }
    var selectedDeviceId by remember { mutableStateOf(selectedItem.beacon) }
    var selectedWasteStorageId: Int? by remember { mutableStateOf(selectedItem.storage) }

    // DropdownMenu 상태 (하단부에서 펼쳐지도록)
    var expandedType by remember { mutableStateOf(false) }
    var expandedStorage by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) } // 블루투스 검색 다이얼로그

//    var wasteDetailsList = remember {
//        mutableStateListOf(*selectedItem.logs.toTypedArray())
//    }


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
                        val wasteType = wasteTypeList.find { selectedItem.wasteType == it.id }
                        Text(
                            text = wasteType?.typeName.toString(),
                            color = if (expandedType) Color.Gray else Color.Black
                        )
                    }
                    DropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }) {
                        wasteTypeList.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.typeName) },
                                onClick = {
                                    selectedWasteTypeId = type.id
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
                        val wasteStorage = wasteStorageList.find { selectedItem.storage == it.id }
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
                                    selectedWasteStorageId = storage.id
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
                    val selectedDevice = beaconList.find { selectedDeviceId == it.id }
                    Text("블루투스 선택: ${selectedDevice?.label}")
                }

                // 블루투스 검색 다이얼로그
                if (showDialog) {
                    BluetoothDialog(beaconViewModel, onDismiss = {
                        showDialog = false
                        selectedDeviceId = beaconViewModel.selectedBeaconId.value ?: 0
                    })
                }


                Spacer(modifier = Modifier.height(12.dp))

//                // 세부 내용 수정
//                Text("세부 내용 수정")
//                LazyColumn {
//                    itemsIndexed(wasteDetailsList) { index, detail ->
//                        val status = wasteStatusList.find{ detail.statusId == it.id}
//                        OutlinedTextField(
//                            value = status?.description ?: "",
//                            onValueChange = { newText ->
//                                wasteDetailsList[index] = detail.copy(wasteDetails = newText)
//                            },
//                            label = { Text("${detail.status} 상세내용") },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }
            }
        },
        confirmButton = {
            Button(onClick = {
                scope.launch {
                    try {
                        val updatedItem = WasteItem(
                            id = selectedItem.id,
                            hospitalId = selectedItem.hospital,
                            storageId = selectedWasteStorageId,
                            beaconId = selectedDeviceId,
                            wasteTypeId = selectedWasteTypeId,
                            wasteStatusId = selectedItem.wasteStatus,
                            description = selectedItem.description,
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
