package com.example.myapplication.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.component.CheckAuth
import com.example.myapplication.viewmodel.WasteMoveViewModel

/**
 * 폐기물 처리 창
 * 3/11(강정훈)
 * 아직 미구현 (디폴트창(DetailScreen) 넣어놓은게 고작)
 */
@Composable
fun WasteMoveScreen(navController: NavController, wasteMoveViewModel: WasteMoveViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }  // ✅ 팝업 상태 관리

    CheckAuth(navController)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("폐기물 처리", style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Back to Home")
        }
        Text("Number ${wasteMoveViewModel.number.value}")
        Button(
            onClick = {
                wasteMoveViewModel.updateNumber()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Increase")
        }
        Button(
            onClick = { showDialog = true },  // ✅ 버튼 클릭 시 다이얼로그 표시
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Show Popup")
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Alert") },
            text = { Text("This is a simple popup.") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}