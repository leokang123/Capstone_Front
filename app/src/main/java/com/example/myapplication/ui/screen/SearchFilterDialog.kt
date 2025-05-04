package com.example.myapplication.ui.screen

import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.repository.impl.WasteRepositoryImpl
import com.example.myapplication.viewmodel.WasteListViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterDialog(
    searchFilter: SearchRequest,
    onFilterChange: (SearchRequest) -> Unit,
    wasteListViewModel: WasteListViewModel,
    onDismiss: () -> Unit,
    onApplyFilter: () -> Unit
) {
    var expandedWasteType by remember { mutableStateOf(false) }
    var expandedStatusType by remember { mutableStateOf(false) }
    var expandedStorageType by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val defaultDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    val defaultTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    var selectedStorage by remember { mutableStateOf<WasteStorage?>(null) }
    // DropdownMenu 상태

    // ✅ 체크박스로 입력 활성화 여부 관리
    var isWasteTypeChecked by remember { mutableStateOf(false) }
    var isRegistrantChecked by remember { mutableStateOf(false) }
    var isWasteStorageChecked by remember { mutableStateOf(false) }
    var isDeviceChecked by remember { mutableStateOf(false) }
    var isDateChecked by remember { mutableStateOf(false) }
    var isStatusChecked by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(searchFilter.selectedDate ?: defaultDate) }
    var selectedTime by remember { mutableStateOf(searchFilter.selectedTime ?: defaultTime) }

    val wasteStorageList by wasteListViewModel.wasteStorageList.collectAsState()

    // ✅ 선택 가능한 폐기물 유형 목록
    val wasteTypes = listOf(
        "격리 의료 폐기물",
        "위해 의료 폐기물 / 조직물류 폐기물",
        "위해 의료 폐기물 / 병리계 폐기물",
        "위해 의료 폐기물 / 손상성 폐기물",
        "위해 의료 폐기물 / 생물·화학 폐기물",
        "위해 의료 폐기물 / 혈액오염 폐기물",
        "일반 의료 폐기물"
    )

    // ✅ 선택 가능한 폐기물 유형 목록
    val statusTypes = listOf(
        "COLLECTING",
        "MOVING",
        "STORED",
        "DISPOSED"
    )

    val mockList = listOf(
        WasteStorage(id = 1, storageName = "기본 창고 A"),
        WasteStorage(id = 2, storageName = "기본 창고 B")
    )

    LaunchedEffect(Unit) {

        if (searchFilter.wasteStorageId != null) {
            selectedStorage = wasteStorageList.find { it.id == searchFilter.wasteStorageId }
            isWasteStorageChecked = true
        }
        if (searchFilter.wasteStatus != null) isStatusChecked = true
        if (searchFilter.wasteType != null) isWasteTypeChecked = true
        if (searchFilter.registrantName != null) isRegistrantChecked = true
        if (searchFilter.selectedDevice != null) isDeviceChecked = true
        if (searchFilter.combineDate != null) isDateChecked = true
        if (searchFilter.selectedDate == null) onFilterChange(searchFilter.copy(selectedDate = defaultDate))
        if (searchFilter.selectedTime == null) onFilterChange(searchFilter.copy(selectedTime = defaultTime))

    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("상세 검색") },
        text = {
            Column {
                // 폐기물 유형
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isWasteTypeChecked, onCheckedChange = {
                        isWasteTypeChecked = it
                        if (!isWasteTypeChecked) onFilterChange(searchFilter.copy(wasteType = null))
                    })
                    Text("폐기물 유형 선택")
                }
                if (isWasteTypeChecked) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedWasteType = true }
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = searchFilter.wasteType ?: "폐기물 유형 선택",
                                color = if (searchFilter.wasteType == null) Color.Gray else Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = expandedWasteType,
                            onDismissRequest = { expandedWasteType = false }
                        ) {
                            wasteTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        onFilterChange(searchFilter.copy(wasteType = type))
                                        expandedWasteType = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 처리자 입력
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isRegistrantChecked, onCheckedChange = {
                        isRegistrantChecked = it
                        if (!isRegistrantChecked) onFilterChange(searchFilter.copy(registrantName = null))
                    })
                    Text("처리자 입력")
                }
                if (isRegistrantChecked) {
                    OutlinedTextField(
                        value = searchFilter.registrantName ?: "",
                        onValueChange = { onFilterChange(searchFilter.copy(registrantName = it)) },
                        label = { Text("처리자") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isRegistrantChecked
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isWasteStorageChecked, onCheckedChange = {
                        isWasteStorageChecked = it
                        selectedStorage = null
                        if (!isWasteStorageChecked) onFilterChange(searchFilter.copy(wasteStorageId = null))
                    })
                    Text("저장창고 선택")
                }
                if (isWasteStorageChecked) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedStorageType = true }
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = selectedStorage?.storageName ?: "저장창고 선택",
                                color = if (selectedStorage == null) Color.Gray else Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = expandedStorageType,
                            onDismissRequest = { expandedStorageType = false }
                        ) {
                            wasteStorageList.forEach { storage ->
                                DropdownMenuItem(
                                    text = { Text(storage.storageName.toString()) },
                                    onClick = {
                                        onFilterChange(searchFilter.copy(wasteStorageId = storage.id))
                                        selectedStorage = storage
                                        expandedStorageType = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isStatusChecked, onCheckedChange = {
                        isStatusChecked = it
                        if (!isStatusChecked) onFilterChange(searchFilter.copy(wasteStatus = null))
                    })
                    Text("상태 선택")
                }
                if (isStatusChecked) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedStatusType = true }
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = searchFilter.wasteStatus ?: "상태 선택",
                                color = if (searchFilter.wasteStatus == null) Color.Gray else Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = expandedStatusType,
                            onDismissRequest = { expandedStatusType = false }
                        ) {
                            statusTypes.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        onFilterChange(searchFilter.copy(wasteStatus = status))
                                        expandedStatusType = false
                                    }
                                )
                            }
                        }
                    }
                }
                // 기기 입력
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDeviceChecked, onCheckedChange = {
                        isDeviceChecked = it
                        if (!isDeviceChecked) onFilterChange(searchFilter.copy(selectedDevice = null))
                    })
                    Text("기기 입력")
                }
                if (isDeviceChecked) {
                    OutlinedTextField(
                        value = searchFilter.selectedDevice ?: "",
                        onValueChange = { onFilterChange(searchFilter.copy(selectedDevice = it)) },
                        label = { Text("기기 ID") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isDeviceChecked
                    )
                }


                // 날짜 & 시간 선택 체크박스
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDateChecked, onCheckedChange = {
                        isDateChecked = it
                        if (!isDateChecked) {
                            onFilterChange(searchFilter.copy(combineDate = null))
                        }

                    })
                    Text("시간 선택")
                }

                if (isDateChecked) {
                    Spacer(Modifier.height(12.dp))
                    // 날짜 & 시간 선택 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.weight(1f),
                            enabled = isDateChecked
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("날짜")
                                Text(selectedDate)
                            }
                        }

                        Button(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.weight(1f),
                            enabled = isDateChecked
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("시간")
                                Text(selectedTime)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("입력한 시간 기준 10일을 검색합니다", color = MaterialTheme.colorScheme.secondary)
                }

                // 날짜 선택 다이얼로그
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            Button(onClick = {
                                showDatePicker = false
                            }) {
                                Text("확인")
                            }
                        }
                    ) {
                        val dateState = rememberDatePickerState()
                        val newDate = dateState.selectedDateMillis?.let { millis ->
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(millis)
                        } ?: selectedDate

                        DatePicker(state = dateState)
                        selectedDate = newDate
                        onFilterChange(
                            searchFilter.copy(
                                selectedDate = newDate,
                                combineDate = "$newDate $selectedTime"
                            )
                        )
                    }
                }

                // 시간 선택 다이얼로그
                if (showTimePicker) {
                    val context = LocalContext.current
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            val newTime =
                                String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                            selectedTime = newTime
                            onFilterChange(
                                searchFilter.copy(
                                    selectedTime = newTime,
                                    combineDate = "$selectedDate $newTime"
                                )
                            )
                            showTimePicker = false
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true // 24시간 형식
                    ).show()
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isDateChecked) onFilterChange(searchFilter.copy(combineDate = "${searchFilter.selectedDate} ${searchFilter.selectedTime}"))
                onApplyFilter()
            }) {
                Text("검색")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
