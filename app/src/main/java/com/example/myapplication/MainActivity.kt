package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.FirebaseTokenManager
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // 앱 처음 생성될때 실행
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
//        requestNotificationPermission()
        enableEdgeToEdge()
//        requestBLEPermissions()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }

        //Firebase Token 받기
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "FCM Token: $token")
                FirebaseTokenManager.setToken(token)
            } else {
                Log.e("FCM", "FCM Token fetch failed", task.exception)
            }
        }

    }

    private fun createNotificationChannel() {
        val channelId = "admin_channel"
        val channelName = "기본 알림 채널"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "일반 알림용 기본 채널입니다."
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d("FCM", "✅ NotificationChannel 생성 완료")
    }


    // 폰에서 앱에 다시 돌아올떄 실행됨 (비교적 자주실행)
    override fun onResume() {
        super.onResume()
        // finish app if the BLE is not supported
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE 미지원", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "BLE 미지원")
        }
    }
//
//    private fun requestNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                    1002
//                )
//            }
//        }
//    }
//    private fun requestBLEPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12(API 31) 이상에서만 필요
//            val permissions = arrayOf(
//                Manifest.permission.BLUETOOTH_SCAN,
//                Manifest.permission.BLUETOOTH_CONNECT,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//            )
//
//            val permissionsToRequest = permissions.filter {
//                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
//            }
//
//            if (permissionsToRequest.isNotEmpty()) {
//                ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 1)
//            }
//        }
//    }
}
