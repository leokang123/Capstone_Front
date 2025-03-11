package com.example.myapplication.ui.screen


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.edit
import androidx.navigation.NavController
@Composable
fun NotificationDialog(navController: NavController) {
    Dialog(onDismissRequest = {navController.popBackStack()}) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            NotificationScreen(navController)
        }
    }
}

@Composable
fun NotificationScreen(navController: NavController) {
    val notifications = listOf(
        "긴 공지 예제입니다. 이 공지는 너무 길어서 일부만 표시됩니다. 클릭하면 전체 내용을 볼 수 있습니다.",
        "두 번째 예제 공지입니다. 공지가 짧으면 그대로 표시됩니다.",
        "세 번째 공지 예제입니다. 공지의 길이가 50자를 넘어가면 잘립니다."
    )

    var selectedNotification by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Notification", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        notifications.forEach { notification ->
            NotificationItem(notification) { selectedNotification = it }
        }

        // ✅ 공지를 클릭하면 팝업으로 전체 내용 표시
        if (selectedNotification != null) {
            AlertDialog(
                onDismissRequest = { selectedNotification = null },
                title = { Text("공지사항") },
                text = { Text(selectedNotification ?: "") },
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
 * ✅ 개별 공지 아이템
 * 긴 공지는 50자로 제한하고, 클릭하면 전체 내용을 볼 수 있도록 설정
 */
@Composable
fun NotificationItem(notification: String, onClick: (String) -> Unit) {
    val maxLength = 30
    val isLongText = notification.length > maxLength
    val displayText = if (isLongText) notification.take(maxLength) + "..." else notification

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // ✅ 공지 간격 추가
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)) // ✅ 보더라인 추가
            .clickable { onClick(notification) }, // ✅ 클릭 가능
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // ✅ 카드 그림자 효과
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp) // ✅ 내부 여백 추가
        )
    }
}