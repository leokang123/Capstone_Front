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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Cabin
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.waste.WasteItemDetails
import com.example.myapplication.data.waste.WasteLog
import com.example.myapplication.ui.screen.WasteEditDialog
import com.example.myapplication.utils.getStatusColor
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
    val wasteStorageList by viewModel.wasteStorageList.collectAsState()
    val wasteTypeList by viewModel.wasteTypeList.collectAsState()
    val wasteStatusList by viewModel.wasteStatusList.collectAsState()
    val beaconList by viewModel.beaconList.collectAsState()
    val textColor = MaterialTheme.colorScheme.onSurface

    Log.d("DETAILS", selectedItem.toString())
    val collectingStatusId = wasteStatusList.find { it.statusLevel == 1 }?.id
    val selectedItemStorage = wasteStorageList.find { it.id == selectedItem.storage }
    val selectedItemType = wasteTypeList.find { it.id == selectedItem.wasteType }
    val selectedBeacon = beaconList.find { it.id == selectedItem.beacon }
    val collectingLog = selectedItem.logs.find { it.statusId == collectingStatusId }
    val zonedDateTime = collectingLog?.createdAt.let { ZonedDateTime.parse(it) }
    val customDate = zonedDateTime?.format(formatter) ?: "날짜 정보 없음"
    val iconSize =
        with(LocalDensity.current) { MaterialTheme.typography.titleLarge.fontSize.toDp() }

    Log.d("DATE", customDate)

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        InfoRow(
            label = selectedItem.id,
            value = "",
            textStyle = MaterialTheme.typography.titleLarge,
            icon = Icons.Outlined.Info,
            iconDescription = "페기물 ID",
            color = MaterialTheme.colorScheme.primary,
            isTitle = true
        )
        Row(
            modifier = Modifier.padding(top = 4.dp)
        ) {
            IconButton(
                modifier = Modifier.size(iconSize),
                onClick = {
                    scope.launch {
                        showModDialog = true
                    }
                }) {
                Icon(
                    imageVector = Icons.Default.Edit, // 수정 아이콘
                    contentDescription = "정정",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(20.dp))

            IconButton(
                modifier = Modifier.size(iconSize),
                onClick = {
                    scope.launch {
                        showDeleteDialog = viewModel.checkItemStatus(selectedItem.id)
                        if (!showDeleteDialog) {
                            Toast.makeText(context, "삭제할 수 없는 STATUS", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Delete, // 삭제 아이콘
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }


    }
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {

            // 폐기물 기본 정보
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                InfoRow(
                    label = "폐기물 유형",
                    value = selectedItemType?.typeName ?: "",
                    textStyle = MaterialTheme.typography.titleMedium,
                    icon = Icons.Outlined.DeleteOutline,
                    iconDescription = "폐기물 유형",

                    )

                InfoRow(
                    label = "등록자",
                    value = collectingLog?.name ?: "",
                    textStyle = MaterialTheme.typography.titleMedium,
                    icon = Icons.Outlined.Person,
                    iconDescription = "등록자",
                    )

                InfoRow(
                    label = "저장위치",
                    value = selectedItemStorage?.storageName ?: "",
                    textStyle = MaterialTheme.typography.titleMedium,
                    icon = Icons.Outlined.Cabin,
                    iconDescription = "저장위치",
                    )

                InfoRow(
                    label = "발생일",
                    value = customDate,
                    textStyle = MaterialTheme.typography.titleMedium,
                    icon = Icons.Outlined.CalendarMonth,
                    iconDescription = "발생일",
                    )

                InfoRow(
                    label = "사용기기",
                    value = selectedBeacon?.label ?: "없음",
                    textStyle = MaterialTheme.typography.titleMedium,
                    icon = Icons.Outlined.Bluetooth,
                    iconDescription = "사용기기",
                    )

            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.BookmarkBorder	,
                contentDescription = "상세 내역",
                tint = textColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "상세 내역",
                fontWeight = FontWeight.Bold,
                color = textColor,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        val detailsList = selectedItem.logs.sortedByDescending { it.createdAt }

        detailsList.forEachIndexed { index, detail ->
            WasteDetailCard(
                detail = detail,
                viewModel = viewModel,
            )
            Spacer(modifier = Modifier.height(8.dp))
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
fun InfoRow(
    label: String = "",
    value: String,
    iconDescription: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    icon: ImageVector,
    textStyle: TextStyle,
    isTitle: Boolean = false,
) {
    val iconSize = with(LocalDensity.current) { textStyle.fontSize.toDp() }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = iconDescription,
            tint = color,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.Bold,
            style = if (isTitle) MaterialTheme.typography.titleLarge else textStyle.copy(fontWeight = FontWeight.Light)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value, color = color, style = textStyle)
    }
}


@Composable
fun WasteDetailCard(detail: WasteLog, viewModel: WasteListViewModel) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val zonedDateTime = detail.createdAt.let { ZonedDateTime.parse(it) }
    val customDate = zonedDateTime?.format(formatter) ?: "날짜 정보 없음"
    val wasteStatusList by viewModel.wasteStatusList.collectAsState()
    val status = wasteStatusList.find { it.id == detail.statusId }
    val textColor = MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.5.dp, getStatusColor(status)) // 최신 상태 강조
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            InfoRow(
                label = "상태",
                value = status?.description ?: "없음",
                color = getStatusColor(status),
                iconDescription = "상태",
                textStyle = MaterialTheme.typography.bodyLarge,
                icon = Icons.Outlined.Cached
            )

            Spacer(modifier = Modifier.height(4.dp))
            // 등록한 사용자 정보 추가
            InfoRow(
                label = "처리자",
                value = detail.userName,
                color = textColor,
                iconDescription = "처리자",
                textStyle = MaterialTheme.typography.bodyMedium,
                icon = Icons.Outlined.PersonPin
            )

            Spacer(modifier = Modifier.height(4.dp))
            // 상태 정보

            InfoRow(
                label = "내용",
                value = detail.description,
                color = textColor,
                iconDescription = "내용",
                textStyle = MaterialTheme.typography.bodyMedium,
                icon = Icons.Outlined.ContentPaste
            )

            Spacer(modifier = Modifier.height(4.dp))

            InfoRow(
                label = "기록 시간",
                value = customDate,
                color = textColor,
                iconDescription = "기록 시간",
                textStyle = MaterialTheme.typography.bodySmall,
                icon = Icons.Outlined.CalendarMonth
            )

        }
    }
}