package com.example.myapplication.ui.component

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.myapplication.data.waste.WasteItemDetails
import com.example.myapplication.data.waste.WasteLog
import com.example.myapplication.ui.screen.WasteEditDialog
import com.example.myapplication.viewmodel.WasteListViewModel
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun WasteItemDetailComponent(
    selectedItem: WasteItemDetails,
    viewModel: WasteListViewModel
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val scope = rememberCoroutineScope()
    var showModDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val wasteTypeList = viewModel.wasteTypeList
    val wasteStatusList = viewModel.wasteStatusList
    val wasteStorageList = viewModel.wasteStorageList
    val beaconList = viewModel.beaconList

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 폐기물 기본 정보
            Text(
                text = "🗑 ${selectedItem.id}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))
            Log.d("DETAILS", selectedItem.toString())
            val collectingStatusId = wasteStatusList.find { it.statusLevel == 1 }?.id
            val selectedItemStorage = wasteStorageList.find { it.id == selectedItem.storage }
            val selectedItemType = wasteTypeList.find { it.id == selectedItem.wasteType }
            val selectedBeacon = beaconList.find { it.id == selectedItem.beacon }
            val collectingLog = selectedItem.logs.find { it.statusId == collectingStatusId }
            val zonedDateTime = collectingLog?.createdAt.let { ZonedDateTime.parse(it) }
            val customDate = zonedDateTime?.format(formatter) ?: "날짜 정보 없음"


            InfoRow("👤 등록자", collectingLog?.name.toString(), Color.Blue)
            InfoRow("📦 저장위치", selectedItemStorage?.storageName.toString(), Color(0xFFD2B48C))
            InfoRow("📅 발생일", customDate, Color.Red)
            InfoRow("⚙ 사용 기기", selectedBeacon?.label ?: "없음", Color.Green)
            InfoRow("🧪 폐기물 종류", selectedItemType?.typeName ?: "알 수 없음", Color.Magenta)

            Log.d("DATE", customDate)
            Spacer(modifier = Modifier.height(16.dp))

            // 상세 내역 목록 표시
            Text(
                text = "📜 상세 내역",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            val detailsList = selectedItem.logs.sortedByDescending { it.createdAt }

            detailsList.forEachIndexed { index, detail ->
                WasteDetailCard(
                    detail = detail,
                    viewModel = viewModel,
                    isLatest = detail.statusId == selectedItem.wasteStatus
                )
                Spacer(modifier = Modifier.height(8.dp))
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            showModDialog = true

                        }
                    }
                ) {
                    Text("정정")
                }
                Button(
                    onClick = {
                        scope.launch {
                            showDeleteDialog = viewModel.checkItemStatus(selectedItem.id)
                            if (!showDeleteDialog) {
                                Toast.makeText(context, "삭제할 수 없는 STATUS", Toast.LENGTH_SHORT)
                                    .show()
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
        WasteEditDialog(viewModel, selectedItem = selectedItem) {
            viewModel.getWasteItemDetails(selectedItem.id)
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
                                viewModel.deleteItem(selectedItem.id) // 삭제 처리
                                Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Log.e(TAG, e.message.toString())
                                Toast.makeText(context, "에러 발생", Toast.LENGTH_SHORT).show()
                            } finally {
                                viewModel.resetWasteList()
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
fun WasteDetailCard(detail: WasteLog, viewModel: WasteListViewModel, isLatest: Boolean) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val zonedDateTime = detail.createdAt.let { ZonedDateTime.parse(it) }
    val customDate = zonedDateTime?.format(formatter) ?: "날짜 정보 없음"
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
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            // 등록한 사용자 정보 추가
            Text(
                text = "👤 처리자: ${detail.userName}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Blue
            )

            Spacer(modifier = Modifier.height(4.dp))
            // 상태 정보
            val status = viewModel.wasteStatusList.find { it.id == detail.statusId }
            Text(
                text = "📌 상태: ${status?.description}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isLatest) Color.Red else Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 상세 내용
            Text(
                text = "📝 내용: ${detail.description}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 기록 시간
            Text(
                text = "📅 기록 시간: $customDate",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}