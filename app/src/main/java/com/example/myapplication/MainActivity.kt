package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
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
import com.example.myapplication.utils.UserDataStore
import com.example.myapplication.viewmodel.LoginViewModel
import com.example.myapplication.viewmodel.SettingsViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userDataStore: UserDataStore

    // 앱 처음 생성될때 실행
    private val viewModel: LoginViewModel by viewModels()
    private var isFirstResume = true


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
        fetchFcmToken()
        // 알림 아이콘 변경을 위함
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val isActive = notificationManager.activeNotifications.isNotEmpty()

        Log.d("NOTIFICATION", "onResume - active: $isActive")

        // userDataStore에 저장
        CoroutineScope(Dispatchers.IO).launch {
            userDataStore.saveHasNotification(isActive)
        }

        if (isFirstResume) {
            isFirstResume = false
            return
        }
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
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d("FCM", "NotificationChannel 생성 완료")
    }
}
