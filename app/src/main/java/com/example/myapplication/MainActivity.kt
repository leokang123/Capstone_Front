package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.FirebaseTokenManager
import com.example.myapplication.viewmodel.LoginViewModel
import com.example.myapplication.viewmodel.SettingsViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 앱 처음 생성될때 실행
    private val viewModel: LoginViewModel by viewModels()
    private var hasFetchedToken = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        createNotificationChannel()
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel() // Activity Scope

            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasFetchedToken) fetchFcmToken()
        viewModel.onResumed()
    }



    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM", "FCM Token fetch failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCM", "FCM Token: $token")
                FirebaseTokenManager.setToken(token)
                hasFetchedToken = true

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
        Log.d("FCM", "NotificationChannel 생성 완료")
    }
}
