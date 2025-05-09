package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiService
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val apiService: ApiService,
) : ViewModel() {

//    fun logout(onResult: (Boolean) -> Unit) {
//        viewModelScope.launch {
//            try {
//                val token = userDataStore.getAccessToken()
//                if (!token.isNullOrEmpty()) {
//                    val res = apiService.signOut()
//                    if (res.isSuccessful) {
//                        userDataStore.clearUserData()
//                        onResult(true)
//                        return@launch
//                    }
//                }
//            } catch (e: Exception) {
//                // 로그 출력 가능
//            }
//            onResult(false)
//        }
//    }

    // 일단 테스트용
fun logout(onResult: (Boolean) -> Unit) {
    viewModelScope.launch {
        userDataStore.clearUserData()
        onResult(true)
    }
}
}
