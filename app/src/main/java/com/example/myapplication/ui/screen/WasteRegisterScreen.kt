package com.example.myapplication.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.data.waste.WasteItem
import com.example.myapplication.ui.component.WasteRegisterCardDialog
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.utils.getStatusColor
import com.example.myapplication.viewmodel.BlueToothViewModel
import com.example.myapplication.viewmodel.WasteListViewModel

/**
 * 폐기물 등록창
 * 3/11(강정훈)
 * 팝업창 버튼 누를시 등록 화면뜨고 내용기입후 등록 버튼 누를시 로그가 뜨는것 까지 구현
 * 로그 내용 그대로 정제해서 서버로 보내고 fetchData를 통해 폐기물 등록창을 다시 로드하여 폐기물관리 상태를 볼수있게하면될듯
 */


@Composable
fun WasteRegisterScreen(
    navController: NavController,
    wasteListViewModel: WasteListViewModel = hiltViewModel(),
    beaconViewModel: BlueToothViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }  // 팝업 상태 관리
    val wasteList by wasteListViewModel.wasteList.collectAsState()

    val beaconList by wasteListViewModel.beaconList.collectAsState()
    val wasteStorageList by wasteListViewModel.wasteStorageList.collectAsState()
    val wasteStatusList by wasteListViewModel.wasteStatusList.collectAsState()
    val wasteTypeList by wasteListViewModel.wasteTypeList.collectAsState()


    var authChecked by remember { mutableStateOf(false) }
    CheckAuth(navController, role = Roles.USER) {
        authChecked = true
    }

    // UI 로딩 시 폐기물 리스트 불러오기
    LaunchedEffect(authChecked) {
        if (!authChecked) return@LaunchedEffect
        wasteListViewModel.searchWasteItems(SearchRequest(isValid = true))
//        wasteListViewModel.fetchWasteList(mode = 1)
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Button(
            onClick = { showDialog = true },  // 버튼 클릭 시 다이얼로그 표시
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(0.5f)
        ) {
            Text("등록")
        }

        // 등록된 폐기물 리스트 표시
        Text("등록된 폐기물 목록", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn(modifier = Modifier.fillMaxSize()) {

            items(wasteList.size) { index ->
                val waste: WasteItem? = wasteList.getOrNull(index)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    val wasteType = wasteTypeList.find { it.id == waste?.wasteTypeId }
                    val wasteStatus = wasteStatusList.find { it.id == waste?.wasteStatusId }
                    val beacon = beaconList.find { it.id == waste?.beaconId }
                    val storage = wasteStorageList.find { it.id == waste?.storageId }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            buildAnnotatedString {
                                append("종류: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(wasteType?.typeName ?: "")
                                }
                            }
                        )

                        Text(
                            buildAnnotatedString {
                                append("폐기물 정보: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(waste?.description ?: "")
                                }
                            }
                        )

                        Text(
                            buildAnnotatedString {
                                append("저장장소: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(storage?.storageName ?: "")
                                }
                            }
                        )

                        Text(
                            buildAnnotatedString {
                                append("기기: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(beacon?.label ?: "이름 없음")
                                }
                            }
                        )

                        Text(
                            buildAnnotatedString {
                                append("상태: ")
                                withStyle(
                                    style = SpanStyle(
                                        color = getStatusColor(wasteStatus),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(wasteStatus?.description ?: "없음")
                                }
                            },
                        )
                    }
                }
            }
        }

    }
    if (showDialog) {
        WasteRegisterCardDialog(wasteListViewModel, beaconViewModel) { showDialog = false }
    }
}
