package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.repository.impl.NotificationRepository
import com.example.myapplication.utils.FirebaseTokenManager
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val notificationRepository: NotificationRepository,
    private val loginRepository: LoginRepository,
) : ViewModel() {

    fun logout(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = try {
                withContext(NonCancellable) { // ← 코루틴 취소 방지
                    val user = userDataStore.getUser()
                    val fcmToken = FirebaseTokenManager.getToken()

                    if (user != null && fcmToken != null) {
                        notificationRepository.removeFcmToken(user.uuid, fcmToken)
                        Log.d(
                            "SettingsViewModel",
                            "FCM 토큰 삭제 요청 전송: uuid=${user.uuid}, token=$fcmToken"
                        )
                    }

                    if (user != null) {
                        val res = loginRepository.logoutUser(user.uuid)
                        if (res) {
                            userDataStore.clearUserData()
                            return@withContext true
                        }
                    }
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.e("LOGOUT", "예외 발생", e)
                false
            }

            onResult(result)
        }
    }
}

//    // 일단 테스트용
//fun logout(onResult: (Boolean) -> Unit) {
//    viewModelScope.launch {
//        userDataStore.clearUserData()
//        onResult(true)
//    }
//}
