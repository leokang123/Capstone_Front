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
import com.example.myapplication.data.user.User
import com.example.myapplication.data.waste.WasteItemRequest
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.repository.WasteRepository
import com.example.myapplication.ui.screen.BluetoothDialog
import com.example.myapplication.utils.UserDataStore
import com.example.myapplication.viewmodel.SharedViewModel
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.text.isNotBlank


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteRegisterCard(wasteListViewModel: WasteListViewModel, sharedViewModel: SharedViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val heightPadding = 12.dp
    val userDataStore = UserDataStore(context)
    var user by remember { mutableStateOf<User?>(null) }
    var registrantName by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val wasteRepository = WasteRepository(context)
    var wasteStorageList by remember { mutableStateOf<List<WasteStorage>>(emptyList()) }
    val wasteTypes = listOf("격리 의료 폐기물",
        "위해 의료 폐기물 / 조직물류 폐기물",
        "위해 의료 폐기물 / 병리계 폐기물",
        "위해 의료 폐기물 / 손상성 폐기물",
        "위해 의료 폐기물 / 생물·화학 폐기물",
        "위해 의료 폐기물 / 혈액오염 폐기물",
        "일반 의료 폐기물") // 폐기물 종류 리스트

    val mockList = listOf(
        WasteStorage(id = 1, storageName = "기본 창고 A"),
        WasteStorage(id = 2, storageName = "기본 창고 B")
    )


    LaunchedEffect(Unit) {
        user = userDataStore.getUser()
        registrantName = user?.name ?: ""
        try {
            val storageList = wasteRepository.getWasteStorage()
            wasteStorageList = storageList.takeIf { !it.isNullOrEmpty() } ?: mockList

        } catch (e: Exception) {
            Log.e("WasteRegisterScreen", e.message.toString())
            Toast.makeText(context, "창고 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    var wasteType by remember { mutableStateOf("") } // 폐기물 종류
    var wasteDetails by remember { mutableStateOf("없음")}
    var location by remember { mutableStateOf("") } // 발생장소
    val selectedDevice = sharedViewModel.selectedBluetoothDevice // 선택된 블루투스 기기

    var showDialog by remember { mutableStateOf(false) } // 블루투스 검색창
    var showDatePicker by remember { mutableStateOf(false) } // 날짜 선택창

    var showTimePicker by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val defaultDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    val defaultTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    var selectedDate by remember { mutableStateOf(defaultDate) }
    var selectedTime by remember { mutableStateOf(defaultTime) }



    var expanded by remember { mutableStateOf(false) } // DropdownMenu 상태

    // 창고 리스트를 저장할 상태
    // 선택한 창고
    var selectedStorage by remember { mutableStateOf<WasteStorage?>(null) }
    // DropdownMenu 상태
    var expandedStorage by remember { mutableStateOf(false) }


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
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = wasteType,
                onValueChange = {},
                label = { Text("폐기물 종류") },
                readOnly = true,
                trailingIcon = { IconButton(onClick = { expanded = true }) { Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown") } },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                wasteTypes.forEach { type ->
                    DropdownMenuItem(text = { Text(type) }, onClick = {
                        wasteType = type
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(heightPadding))

        // 발생장소 입력
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("발생장소") },
            modifier = Modifier.fillMaxWidth()
        )

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

            DropdownMenu(expanded = expandedStorage, onDismissRequest = { expandedStorage = false }) {
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.weight(1f) // ✅ 버튼 크기 균등 분배
            ) {
                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("등록날짜")
                    Text(selectedDate)
                }
            }

            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier.weight(1f) // ✅ 버튼 크기 균등 분배
            ) {
                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("등록시간")
                    Text(selectedTime)
                }

            }
        }


        // 날짜 선택 다이얼로그
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = { showDatePicker = false }) {
                        Text("확인")
                    }
                }
            ) {
                val dateState = rememberDatePickerState()
                DatePicker(state = dateState)
                selectedDate = dateState.selectedDateMillis?.let { millis ->
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(millis)
                } ?: defaultDate
            }
        }

        // 시간 선택 다이얼로그
        if (showTimePicker) {
            val context = LocalContext.current
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    showTimePicker = false
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // 24시간 형식
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
            BluetoothDialog(sharedViewModel, onDismiss = {
                showDialog = false
            })
        }

        Spacer(modifier = Modifier.height(heightPadding))

        // 선택한 블루투스 기기 표시
        Text(
            text = "선택된 기기: ${selectedDevice ?: "기기 없음"}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(heightPadding))

        // 등록 버튼
        Button(
            onClick = {
                Log.d("WasteRegisterCard", "등록자ID: ${user?.id} 등록자: $registrantName, 종류: $wasteType, 날짜: $selectedDate, 장소: $location, 기기: ${selectedDevice ?: "없음"}")
                // 여기서 서버로 데이터 보내고 처리완료 응답받으면 onDismiss

                scope.launch {
                    try {
                        val wasteItem = WasteItemRequest(
                            wasteType = wasteType,
                            selectedDate = "$selectedDate $selectedTime",
                            wasteDetails = wasteDetails,
                            location = location,
                            selectedDevice = selectedDevice ?: "없음",
                            storageId = selectedStorage?.id ?: 0 // 선택한 창고의 ID 포함

                        )
                        val response: String? = wasteRepository.registerWaste(wasteItem)
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show()


                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    } finally {
                        sharedViewModel.reset() // 뷰모델 데이터 초기화
                        wasteListViewModel.fetchWasteList(mode = 1)
                        onDismiss()
                    }
                }

            },
            modifier = Modifier.fillMaxWidth(),
            enabled = registrantName.isNotBlank()
                    && wasteType.isNotBlank()
                    &&  location.isNotBlank()
                    && selectedStorage != null
                    && selectedDevice != null
                    && selectedTime.isNotBlank()
                    && selectedDate.isNotBlank()
                    && selectedDevice.isNotBlank()
        ) {
            Text("등록")
        }


    }
}
