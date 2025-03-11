package com.example.myapplication.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

/**
 * 팝업창 예시 (지워도 됌)
 */
@Composable
fun PopUpScreen(navController: NavController) {
    AlertDialog(
        onDismissRequest = { navController.popBackStack() }, // ✅ 다이얼로그 닫기
        title = { Text("Popup Title") },
        text = { Text("This is a popup dialog!") },
        confirmButton = {
            Button(onClick = { navController.popBackStack() }) {
                Text("OK")
            }
        }
    )
}