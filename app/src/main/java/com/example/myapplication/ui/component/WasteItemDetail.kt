package com.example.myapplication.ui.component

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.waste.WasteDetailResponse
import com.example.myapplication.data.waste.WasteItemDetailResponse
import com.example.myapplication.ui.screen.WasteEditDialog
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.launch


@Composable
fun WasteItemDetailComponent(selectedItem: WasteItemDetailResponse, wasteListViewModel: WasteListViewModel) {
    val scope = rememberCoroutineScope()
    var showModDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 폐기물 기본 정보
            Text(
                text = "🗑 ${selectedItem.wasteType}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow("👤 등록자", selectedItem.registrantName, Color.Blue)
            InfoRow("📍 위치", selectedItem.location, Color.DarkGray)
            InfoRow("📦 저장위치", selectedItem.wasteStorage?.storageName.toString(), Color(0xFFD2B48C))
            InfoRow("📅 발생일", selectedItem.selectedDate, Color.Red)
            InfoRow("⚙ 사용 기기", selectedItem.selectedDevice ?: "없음", Color.Green)

            Spacer(modifier = Modifier.height(16.dp))

            // 상세 내역 목록 표시
            Text(
                text = "📜 상세 내역",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.
                fillMaxWidth()
                    .fillMaxHeight(0.8f),
            ) {
                val detailsList = selectedItem.wasteDetails.sortedByDescending { it.date }

                itemsIndexed(detailsList) { index, detail ->
                    WasteDetailCard(detail, detail.status == selectedItem.status)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            showModDialog =  true

                        }
                    }
                ) {
                    Text("정정")
                }
                Button(
                    onClick = {
                        scope.launch {
                            showDeleteDialog =  wasteListViewModel.checkItemStatus(selectedItem.id)
                            if (!showDeleteDialog) {
                                Toast.makeText(context, "삭제할 수 없는 STATUS", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("삭제")
                }
            }
        }
    }

    // 정정 다이얼로그 (예제 코드, 원하는 Composable로 변경 가능)
    if (showModDialog) {
        WasteEditDialog(wasteListViewModel, selectedItem = selectedItem) {
            wasteListViewModel.getWasteItemDetails(selectedItem.id)
            showModDialog = false
        }
    }

    // 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("삭제 확인") },
            text = { Text("이 항목을 삭제하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                wasteListViewModel.deleteItem(selectedItem.id) // 삭제 처리
                                Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Log.e(TAG, e.message.toString())
                                Toast.makeText(context, "에러 발생", Toast.LENGTH_SHORT).show()
                            } finally {
                                wasteListViewModel.resetWasteList()
                            }
                            showDeleteDialog = false
                        }
                    }
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

// 개별 정보 항목을 정리하는 Composable
@Composable
fun InfoRow(label: String, value: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                    append("$label: ")
                }
                append(value)
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
    }
}


@Composable
fun WasteDetailCard(detail: WasteDetailResponse, isLatest: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLatest) Color(0xFFFFF3E0) else Color.White // 최신 상태 강조
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, if (isLatest) Color.Red else Color.Gray) // 최신 상태 강조
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 등록한 사용자 정보 추가
            Text(
                text = "👤 처리자: ${detail.user.name}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Blue
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 상태 정보
            Text(
                text = "📌 상태: ${detail.status}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isLatest) Color.Red else Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 상세 내용
            Text(
                text = "📝 내용: ${detail.wasteDetails}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 기록 시간
            Text(
                text = "📅 기록 시간: ${detail.date}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}