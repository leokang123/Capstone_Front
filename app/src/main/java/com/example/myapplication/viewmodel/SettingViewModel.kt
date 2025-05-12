package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.fcmtoken.FcmLogout
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.repository.impl.NotificationRepository
import com.example.myapplication.utils.FirebaseTokenManager
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val apiService: ApiService,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    fun logout(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                
                //FcmToken 삭제 기능 추가
                val user = userDataStore.getUser()
                val fcmToken = FirebaseTokenManager.getToken()

                if (user != null && fcmToken != null) {
                    notificationRepository.removeFcmToken(user.uuid ?:"", fcmToken)
                }
                
                
                
                val token = userDataStore.getAccessToken()
                if (!token.isNullOrEmpty()) {
                    val res = apiService.signOut()
                    if (res.isSuccessful) {
                        userDataStore.clearUserData()
                        onResult(true)
                        return@launch
                    }
                }
            } catch (e: Exception) {
                // 로그 출력 가능
            }
            onResult(false)
        }
    }
}
