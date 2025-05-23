package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "제목 없음"
        val body = remoteMessage.notification?.body ?: "내용 없음"

        Log.d("FCM", "알림 수신: $title - $body")

        showNotification(title, body)
    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "admin_channel"

        // 알림 채널 생성
        val channel = NotificationChannel(
            channelId,
            "기본 알림 채널",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "서버에서 오는 일반 알림"
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)


        // 알림 생성
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // 알림 표시
        notificationManager.notify(0, builder.build())
    }
}
