package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.repository.impl.NotificationRepository
import com.example.myapplication.states.AuthState
import com.example.myapplication.utils.FirebaseTokenManager
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private var hasSentFCM = false

    fun checkAuth(requiredRole: Roles) {
        viewModelScope.launch {
            val token = userDataStore.getAccessToken()
            val user = userDataStore.getUser()

            when {
                token.isNullOrBlank() -> _authState.value = AuthState.NotLoggedIn
                user?.roles?.contains(requiredRole) == false -> _authState.value =
                    AuthState.Unauthorized(user.primaryRoles)

                else -> {
                    _authState.value = AuthState.Authorized(user)
                    if (!hasSentFCM) {
                        // fcm토큰 전송
                        user?.uuid.let { userId ->
                            FirebaseTokenManager.getToken()?.let { fcmToken ->
                                viewModelScope.launch {
                                    notificationRepository.sendFcmToken(user?.uuid ?: "", fcmToken)
                                    //서버로 fcm 토큰을 잘 보내고 있는지 확인하기 위한 코드, 나중에 지우기
                                    Log.d("FCM", "Sending token to server: $fcmToken")
                                }
                            }
                            hasSentFCM = true
                        }
                    }

                }
            }
        }
    }
}

