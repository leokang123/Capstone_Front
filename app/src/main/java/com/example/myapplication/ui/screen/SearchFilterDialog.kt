package com.example.myapplication.ui.screen

import android.app.TimePickerDialog
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
import com.example.myapplication.viewmodel.WasteListViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    var isWasteStorageChecked by remember { mutableStateOf(false) }
    var isDeviceChecked by remember { mutableStateOf(false) }
    var isDateChecked by remember { mutableStateOf(false) }
    var isStatusChecked by remember { mutableStateOf(false) }
    val defaultDateTime = LocalDateTime.now()
    var selectedDateTime by remember { mutableStateOf(searchFilter.startDate ?: defaultDateTime) }

    val wasteStorageList = wasteListViewModel.wasteStorageList
    val wasteTypeList = wasteListViewModel.wasteTypeList
    val wasteStatusList = wasteListViewModel.wasteStatusList

    LaunchedEffect(Unit) {

        if (searchFilter.wasteStorageId != null) {
            selectedStorage = wasteStorageList.find { it.id == searchFilter.wasteStorageId }
            isWasteStorageChecked = true
        }
        if (searchFilter.wasteStatusId != null) isStatusChecked = true
        if (searchFilter.wasteTypeId != null) isWasteTypeChecked = true
        if (searchFilter.beaconId != null) isDeviceChecked = true
        if (searchFilter.startDate != null) isDateChecked = true
//        if (searchFilter.start == null) onFilterChange(searchFilter.copy(selectedDate = defaultDate))
//        if (searchFilter.selectedTime == null) onFilterChange(searchFilter.copy(selectedTime = defaultTime))

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
                        if (!isWasteTypeChecked) onFilterChange(searchFilter.copy(wasteTypeId = null))
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
                            val wasteType = wasteTypeList.find { it.id == searchFilter.wasteTypeId }

                            Text(

                                text = wasteType?.typeName ?: "폐기물 유형 선택",
                                color = if (wasteType?.typeName == null) Color.Gray else Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = expandedWasteType,
                            onDismissRequest = { expandedWasteType = false }
                        ) {
                            wasteTypeList.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.typeName) },
                                    onClick = {
                                        onFilterChange(searchFilter.copy(wasteTypeId = type.id))
                                        expandedWasteType = false
                                    }
                                )
                            }
                        }
                    }
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
                        if (!isStatusChecked) onFilterChange(searchFilter.copy(wasteStatusId = null))
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
                            val wasteStatus =
                                wasteStatusList.find { it.id == searchFilter.wasteStatusId }
                            Text(
                                text = wasteStatus?.description ?: "상태 선택",
                                color = if (wasteStatus?.description == null) Color.Gray else Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = expandedStatusType,
                            onDismissRequest = { expandedStatusType = false }
                        ) {
                            wasteStatusList.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.description) },
                                    onClick = {
                                        onFilterChange(searchFilter.copy(wasteStatusId = status.id))
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
                        if (!isDeviceChecked) onFilterChange(searchFilter.copy(beaconId = null))
                    })
                    Text("기기 입력")
                }
                if (isDeviceChecked) {
                    OutlinedTextField(
                        value = searchFilter.beaconId.toString(),
                        onValueChange = {
                            val newValue = it.toIntOrNull()
                            onFilterChange(searchFilter.copy(beaconId = newValue))
                        },
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
                            onFilterChange(searchFilter.copy(startDate = null, endDate = null))
                        }
                    })
                    Text("시간 선택")
                }
                if (isDateChecked) {
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("날짜")
                                Text(selectedDateTime.toLocalDate().toString())
                            }
                        }

                        Button(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("시간")
                                Text(
                                    selectedDateTime.toLocalTime()
                                        .format(DateTimeFormatter.ofPattern("HH:mm"))
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text("입력한 시간 기준 10일을 검색합니다", color = MaterialTheme.colorScheme.secondary)
                }

            }

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

                                onFilterChange(
                                    searchFilter.copy(
                                        startDate = selectedDateTime,
                                        endDate = selectedDateTime.plusDays(10)
                                    )
                                )
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

                        onFilterChange(
                            searchFilter.copy(
                                startDate = selectedDateTime,
                                endDate = selectedDateTime.plusDays(10)
                            )
                        )
                        showTimePicker = false
                    },
                    selectedDateTime.hour,
                    selectedDateTime.minute,
                    true
                ).show()
            }
        },
        confirmButton = {
            Button(onClick = onApplyFilter) {
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
