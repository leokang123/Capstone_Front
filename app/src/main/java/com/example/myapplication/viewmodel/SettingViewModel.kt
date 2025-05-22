package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.myapplication.data.fcmtoken.FcmLogout
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.repository.impl.NotificationRepository
import com.example.myapplication.utils.FirebaseTokenManager
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val apiService: ApiService,
    private val notificationRepository: NotificationRepository,
    private val loginRepository: LoginRepository,
) : ViewModel() {

    fun logout(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                //FcmToken 삭제 기능 추가
                val user = userDataStore.getUser()
                val fcmToken = FirebaseTokenManager.getToken()
                val token = userDataStore.getAccessToken()
                
                if (user != null && fcmToken != null) {
                    notificationRepository.removeFcmToken(user.uuid ?:"", fcmToken)
                    Log.d("SettingsViewModel", "FCM 토큰 삭제 요청 전송: uuid=${user.uuid}, token=$fcmToken")
                }
                
                
                
                if (!token.isNullOrEmpty()) {
                    val res = apiService.signOut()
                    if (res.isSuccessful) {
                if (user != null) {
                    val res = loginRepository.logoutUser(user.uuid)
                    Log.d("LOGIN_BOOL", res.toString())
                    if (res) {
                        userDataStore.clearUserData()
                        onResult(true)
                        return@launch
                    }
                }
            } catch (e: Exception) {
                // 로그 출력 가능
                Log.e("LOGOUT", e.message.toString())
            }
            onResult(false)
        }
    }

//    // 일단 테스트용
//fun logout(onResult: (Boolean) -> Unit) {
//    viewModelScope.launch {
//        userDataStore.clearUserData()
//        onResult(true)
//    }
//}
}
