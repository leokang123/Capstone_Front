package com.example.myapplication.ui.screen


import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.entity.AlarmData
import com.example.myapplication.viewmodel.AlarmViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * 알림 화면
 * 3/11 (강정훈)
 * 알림화면, 블루투스화면, 설정화면 모두 비슷한 방식으로 화면 구성을 함
 * 더미데이터로 방향성은 구현해놨는데, 공지를 api로 받아오는거, 새 공지가 왔을때 업데이트하는거 등 미구현된부분들이 아직 많음
 */

@Composable
fun NotificationDialog(
    navController: NavController,
    alarmViewModel: AlarmViewModel = hiltViewModel()
) {

    Dialog(onDismissRequest = { navController.popBackStack() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        ) {
            NotificationScreen(navController, alarmViewModel)
        }
    }
}

@Composable
fun NotificationScreen(navController: NavController, viewModel: AlarmViewModel) {

    val notifications: List<AlarmData> by viewModel.alarmList.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getAlarmList()
        viewModel.setNotificationState(false)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val activeNotifications = notificationManager.activeNotifications

        if (activeNotifications.isNotEmpty()) {
            notificationManager.cancelAll()
        }
    }

    var selectedNotification by remember { mutableStateOf<AlarmData?>(null) }
    val parsedTime = selectedNotification?.receivedAt?.let { LocalDateTime.parse(it) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Notification", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f) // 남은 영역 전부 차지
                .fillMaxWidth()
        ) {
            items(notifications) { notification ->
                NotificationItem(notification) { selectedNotification = it }
            }
        }

        // 공지를 클릭하면 팝업으로 전체 내용 표시
        selectedNotification?.let {
            AlertDialog(
                onDismissRequest = { selectedNotification = null },
                title = { Text(selectedNotification?.title ?: "") },
                text = {
                    Column {
                        Text(selectedNotification?.message ?: "")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = parsedTime?.format(formatter) ?: "날짜정보 없음",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { selectedNotification = null }) {
                        Text("닫기")
                    }
                }
            )
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Back")
        }
    }
}

/**
 * 개별 공지 아이템
 * 긴 공지는 50자로 제한하고, 클릭하면 전체 내용을 볼 수 있도록 설정
 */
@Composable
fun NotificationItem(notification: AlarmData, onClick: (AlarmData) -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val maxLength = 15
    val isLongText = notification.message.length > maxLength
    val displayTitle = notification.title
    val parsedTime = LocalDateTime.parse(notification.receivedAt)

    val displayText = if (isLongText) {
        notification.message.take(maxLength) + "..."
    } else {
        notification.message
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .clickable { onClick(notification) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = parsedTime?.format(formatter) ?: "날짜정보 없음",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
