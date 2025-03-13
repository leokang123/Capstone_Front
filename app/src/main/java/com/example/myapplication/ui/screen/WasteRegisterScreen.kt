package com.example.myapplication.ui.screen

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.WasteItemRequest
import com.example.myapplication.data.WasteItemResponse
import com.example.myapplication.repository.WasteRepository
import com.example.myapplication.ui.component.CheckAuth
import com.example.myapplication.viewmodel.SharedViewModel
import com.example.myapplication.viewmodel.WasteRegisterViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 폐기물 등록창
 * 3/11(강정훈)
 * 팝업창 버튼 누를시 등록 화면뜨고 내용기입후 등록 버튼 누를시 로그가 뜨는것 까지 구현
 * 로그 내용 그대로 정제해서 서버로 보내고 fetchData를 통해 폐기물 등록창을 다시 로드하여 폐기물관리 상태를 볼수있게하면될듯
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WasteRegisterScreen(navController: NavController, wasteRegisterViewModel: WasteRegisterViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }  // 팝업 상태 관리
    val sharedViewModel: SharedViewModel = viewModel()

    CheckAuth(navController)

    // ✅ 화면이 열릴 때 서버에서 데이터 가져오기
    LaunchedEffect(Unit) {
        wasteRegisterViewModel.fetchWasteList()
        println(wasteRegisterViewModel.wasteList)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("폐기물 등록", style = MaterialTheme.typography.headlineMedium)
        // ✅ 새로고침 버튼
        Button(onClick = { wasteRegisterViewModel.fetchWasteList() }, modifier = Modifier.padding(top = 8.dp)) {
            Text("새로고침")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showDialog = true },  // 버튼 클릭 시 다이얼로그 표시
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("등록")
        }

        // ✅ 등록된 폐기물 리스트 표시
        Text("등록된 폐기물 목록", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn(modifier = Modifier.fillMaxSize()) {

            items(wasteRegisterViewModel.wasteList.size) { index ->
                val waste: WasteItemResponse? = wasteRegisterViewModel.wasteList.getOrNull(index)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("등록자: ${waste?.registrantName}", style = MaterialTheme.typography.bodyLarge)
                        Text("종류: ${waste?.wasteType}")
                        Text("부가 정보: ${waste?.wasteDetails}")
                        Text("날짜: ${waste?.selectedDate}")
                        Text("장소: ${waste?.location}")
                        Text("기기: ${waste?.selectedDevice ?: "없음"}")
                        Text("상태: ${waste?.status ?: "없음"}")

                    }
                }
            }
        }

    }
    if (showDialog) {
        WasteRegisterCard(wasteRegisterViewModel, sharedViewModel) { showDialog = false }
        wasteRegisterViewModel.fetchWasteList()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteRegisterCard(wasteRegisterViewModel: WasteRegisterViewModel, sharedViewModel: SharedViewModel, onDismiss: () -> Unit) {
    var registrantName by remember { mutableStateOf("") } // 등록자 이름
    var wasteType by remember { mutableStateOf("") } // 폐기물 종류
    var wasteDetails by remember { mutableStateOf("없음")}
    var location by remember { mutableStateOf("") } // 발생장소
    var selectedDate by remember { mutableStateOf("날짜 선택") } // 발생일
    val selectedDevice = sharedViewModel.selectedBluetoothDevice // 선택된 블루투스 기기
    var showDialog by remember { mutableStateOf(false) } // 블루투스 검색창
    var showDatePicker by remember { mutableStateOf(false) } // 날짜 선택창
    val wasteTypes = listOf("일반 폐기물", "의료 폐기물", "전자 폐기물", "건설 폐기물") // 폐기물 종류 리스트
    var expanded by remember { mutableStateOf(false) } // DropdownMenu 상태
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val wasteRepository = WasteRepository(context)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("폐기물 등록", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // 등록자 이름 입력
            OutlinedTextField(
                value = registrantName,
                onValueChange = { registrantName = it },
                label = { Text("등록자 이름") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            // 발생장소 입력
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("발생장소") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 폐기물 부가이력 등록
            OutlinedTextField(
                value = wasteDetails,
                onValueChange = { wasteDetails = it },
                label = { Text("부가 정보") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 발생일 선택 버튼
            Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedDate)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 블루투스 검색 버튼
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("블루투스 검색")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 선택한 블루투스 기기 표시
            Text(
                text = "선택된 기기: ${selectedDevice ?: "기기 없음"}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 등록 버튼
            Button(
                onClick = {
                    Log.d("WasteRegisterCard", "등록자: $registrantName, 종류: $wasteType, 날짜: $selectedDate, 장소: $location, 기기: ${selectedDevice ?: "없음"}")
                    // 여기서 서버로 데이터 보내고 처리완료 응답받으면 onDismiss

                    scope.launch {
                        try {
                            val wasteItem = WasteItemRequest(
                                registrantName = registrantName,
                                wasteType = wasteType,
                                selectedDate = selectedDate,
                                wasteDetails = wasteDetails,
                                location = location,
                                selectedDevice = selectedDevice ?: "없음",
                            )
                            val response: String? = wasteRepository.registerWaste(wasteItem)
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show()


                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        } finally {
                            sharedViewModel.reset() // 뷰모델 데이터 초기화
                            wasteRegisterViewModel.fetchWasteList()
                            onDismiss()
                        }
                    }

                    // 서버로 데이터 전송 가능
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = registrantName.isNotBlank() && wasteType.isNotBlank() && selectedDevice != null
            ) {
                Text("등록")
            }

        }
    }

    // 블루투스 검색 다이얼로그
    if (showDialog) {
        BluetoothDialog(sharedViewModel, onDismiss = {
            showDialog = false
        })
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
            } ?: "날짜 선택"
        }
    }
}
