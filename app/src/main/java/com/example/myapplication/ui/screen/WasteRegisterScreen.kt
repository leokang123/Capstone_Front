package com.example.myapplication.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.waste.WasteItemResponse
import com.example.myapplication.ui.component.WasteRegisterCard
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.viewmodel.SharedViewModel
import com.example.myapplication.viewmodel.WasteListViewModel

/**
 * 폐기물 등록창
 * 3/11(강정훈)
 * 팝업창 버튼 누를시 등록 화면뜨고 내용기입후 등록 버튼 누를시 로그가 뜨는것 까지 구현
 * 로그 내용 그대로 정제해서 서버로 보내고 fetchData를 통해 폐기물 등록창을 다시 로드하여 폐기물관리 상태를 볼수있게하면될듯
 */
@Composable
fun WasteRegisterCardDialog(
    wasteListViewModel: WasteListViewModel,
    sharedViewModel: SharedViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
        ) {
            WasteRegisterCard(wasteListViewModel, sharedViewModel) { onDismiss() }
        }
    }
}

@Composable
fun WasteRegisterScreen(
    navController: NavController,
    wasteListViewModel: WasteListViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }  // 팝업 상태 관리
    val sharedViewModel: SharedViewModel = viewModel()
    val wasteList by wasteListViewModel.wasteList.collectAsState()
    var authChecked by remember { mutableStateOf(false) }
    CheckAuth(navController, roleId = 1) {
        authChecked = true
    }

    // UI 로딩 시 폐기물 리스트 불러오기
    LaunchedEffect(authChecked) {
        if (!authChecked) return@LaunchedEffect

        wasteListViewModel.fetchWasteList(mode = 1)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("폐기물 등록", style = MaterialTheme.typography.headlineMedium)
        // 새로고침 버튼
        Button(
            onClick = { wasteListViewModel.fetchWasteList(mode = 1) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("새로고침")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showDialog = true },  // 버튼 클릭 시 다이얼로그 표시
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("등록")
        }

        // 등록된 폐기물 리스트 표시
        Text("등록된 폐기물 목록", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn(modifier = Modifier.fillMaxSize()) {

            items(wasteList.size) { index ->
                val waste: WasteItemResponse? = wasteList.getOrNull(index)
                Log.d("wasteItem", waste.toString())

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "등록자: ${waste?.registrantName}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text("종류: ${waste?.wasteType}")
                        Text("부가 정보: ${waste?.wasteDetails}")
                        Text("날짜: ${waste?.selectedDate}")
                        Text("장소: ${waste?.location}")
                        Text("저장장소: ${waste?.storageName}")
                        Text("기기: ${waste?.selectedDevice ?: "없음"}")
                        Text("상태: ${waste?.status ?: "없음"}")

                    }
                }
            }
        }

    }
    if (showDialog) {
        WasteRegisterCardDialog(wasteListViewModel, sharedViewModel) { showDialog = false }
        wasteListViewModel.fetchWasteList(mode = 1)
    }
}
