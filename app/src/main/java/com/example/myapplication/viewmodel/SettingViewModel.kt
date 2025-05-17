package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val loginRepository: LoginRepository,
) : ViewModel() {

    fun logout(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val user = userDataStore.getUser()
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
