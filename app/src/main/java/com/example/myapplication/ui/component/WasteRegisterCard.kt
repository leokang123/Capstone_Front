package com.example.myapplication.ui.component

import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.waste.WasteItem
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.ui.screen.BluetoothDialog
import com.example.myapplication.viewmodel.BlueToothViewModel
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteRegisterCard(
    wasteListViewModel: WasteListViewModel,
    beaconViewModel: BlueToothViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val user by wasteListViewModel.user.collectAsState()
    val wasteStorageList = wasteListViewModel.wasteStorageList
    val wasteTypeList = wasteListViewModel.wasteTypeList
    val wasteStatusList = wasteListViewModel.wasteStatusList
    val beaconList = wasteListViewModel.beaconList

    val heightPadding = 12.dp
    var registrantName by remember { mutableStateOf(user?.name ?: "알수없음") }

    val scope = rememberCoroutineScope()


    var selectedWasteTypeId by remember { mutableStateOf<Int>(0) } // 폐기물 종류
    var wasteDetails by remember { mutableStateOf("없음") }
    val selectedDeviceId by beaconViewModel.selectedBeaconId.collectAsState()

    var showDialog by remember { mutableStateOf(false) } // 블루투스 검색창
    var showDatePicker by remember { mutableStateOf(false) } // 날짜 선택창

    var showTimePicker by remember { mutableStateOf(false) }

    val defaultDateTime = LocalDateTime.now()
    var selectedDateTime by remember { mutableStateOf(defaultDateTime) }


    var expanded by remember { mutableStateOf(false) } // DropdownMenu 상태

    // 선택한 창고
    var selectedStorage by remember { mutableStateOf<WasteStorage?>(null) }
    // DropdownMenu 상태
    var expandedStorage by remember { mutableStateOf(false) }

    val toastMessage by wasteListViewModel.toastMessage.collectAsState(initial = null)


    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.padding(heightPadding * 2)) {
        Text("폐기물 등록", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(heightPadding))

        // 등록자 이름 입력
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(16.dp)
        ) {
            Text(text = registrantName)
        }

        Spacer(modifier = Modifier.height(heightPadding))

        // 폐기물 종류 선택 (DropdownMenu)
        val wasteType = wasteTypeList.find { it.id == selectedWasteTypeId }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = wasteType?.typeName.toString(),
                onValueChange = {},
                label = { Text("폐기물 종류") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        expanded = true
                    }) { Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown") }
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                wasteTypeList.forEach { type ->
                    DropdownMenuItem(text = { Text(type.typeName) }, onClick = {
                        selectedWasteTypeId = type.id
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(heightPadding))

        // 창고 선택 Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedStorage?.storageName ?: "창고 선택",
                onValueChange = {},
                label = { Text("저장할 창고") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expandedStorage = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expandedStorage,
                onDismissRequest = { expandedStorage = false }) {
                wasteStorageList.forEach { storage ->
                    DropdownMenuItem(
                        text = { Text(storage.storageName.toString()) },
                        onClick = {
                            selectedStorage = storage
                            expandedStorage = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(heightPadding))

        // 폐기물 부가이력 등록
        OutlinedTextField(
            value = wasteDetails,
            onValueChange = { wasteDetails = it },
            label = { Text("부가 정보") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(heightPadding))

        // 날짜 & 시간 선택 버튼을 정렬
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.weight(1f) // ✅ 버튼 크기 균등 분배
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("등록날짜")
                    Text(selectedDateTime.toLocalDate().toString())
                }
            }

            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier.weight(1f) // ✅ 버튼 크기 균등 분배
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("등록시간")
                    Text(
                        selectedDateTime.toLocalTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm"))
                    )
                }

            }
        }


        // 날짜 선택 다이얼로그
        if (showDatePicker) {
            val dateState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            val pickedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()

                            selectedDateTime =
                                LocalDateTime.of(pickedDate, selectedDateTime.toLocalTime())

                        }
                        showDatePicker = false
                    }) {
                        Text("확인")
                    }
                }
            ) {
                DatePicker(state = dateState)
            }
        }

        if (showTimePicker) {
            val context = LocalContext.current
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val newTime = LocalTime.of(hourOfDay, minute)
                    selectedDateTime = LocalDateTime.of(selectedDateTime.toLocalDate(), newTime)
                    showTimePicker = false
                },
                selectedDateTime.hour,
                selectedDateTime.minute,
                true
            ).show()
        }


        Spacer(modifier = Modifier.height(heightPadding))


        // 블루투스 검색 버튼
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("블루투스 검색")
        }

        // 블루투스 검색 다이얼로그
        if (showDialog) {
            BluetoothDialog(beaconViewModel, onDismiss = {
                showDialog = false
            })
        }

        Spacer(modifier = Modifier.height(heightPadding))

        // 선택한 블루투스 기기 표시
        val selectedBeacon = beaconList.find { it.id == selectedDeviceId }
        Text(
            text = "선택된 기기: ${selectedBeacon?.label}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(heightPadding))

        // 등록 버튼
        Button(
            onClick = {
                Log.d(
                    "WasteRegisterCard",
                    "등록자ID: ${user?.uuid} 등록자: $registrantName, 종류: ${wasteType?.typeName}, 날짜: $selectedDateTime, 기기: $selectedDeviceId"
                )
                // 여기서 서버로 데이터 보내고 처리완료 응답받으면 onDismiss

                scope.launch {
                    try {
                        val wasteItem = WasteItem(
                            wasteTypeId = selectedWasteTypeId,
                            beaconId = selectedDeviceId,
                            description = wasteDetails,
                            storageId = selectedStorage?.id ?: 0,
                            hospitalId = user?.hospital?.id ?: 0,

                            )

                        val response: String? = wasteListViewModel.registerWasteItem(wasteItem)
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show()


                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    } finally {
                        beaconViewModel.resetSelectedBeacon() // 뷰모델 데이터 초기화
                        wasteListViewModel.fetchWasteList(mode = 1)
                        onDismiss()
                    }
                }

            },
            modifier = Modifier.fillMaxWidth(),
            enabled = registrantName.isNotBlank()
                    && selectedStorage != null
                    && selectedDateTime != null
                    && selectedDeviceId != null

        ) {
            Text("등록")
        }


    }
}
