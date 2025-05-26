package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entity.AppUser
import com.example.myapplication.data.entity.Hospital
import com.example.myapplication.data.entity.User
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.repository.impl.NotificationRepository
import com.example.myapplication.utils.FirebaseTokenManager
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val masterDataRepository: MasterDataRepository,
    private val userDataStore: UserDataStore,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    val hospitalList = MutableStateFlow<List<Hospital>?>(emptyList())

    init {
        viewModelScope.launch {
            try {
                hospitalList.value = masterDataRepository.getHospitalList()
            } catch (e: Exception) {
                Log.e("INIT_HOSPITAL", e.message.toString())
            }
        }
    }

    private val _loginSuccess = MutableSharedFlow<Boolean>()
    val loginSuccess = _loginSuccess.asSharedFlow()

    fun initData(hospitalId: Int) {
        viewModelScope.launch {
            masterDataRepository.initAll(hospitalId)
        }
    }

    suspend fun checkAutoLogin(): AppUser? {
        return userDataStore.getUser()
    }


    fun login() {
        viewModelScope.launch {
            try {
                val loginUser = loginRepository.loginUser(
                    User(
                        username = username.trim(),
                        password = password
                    )
                )

                if (loginUser == null) {
                    errorMessage = "Invalid username or password"
                    _loginSuccess.emit(false)
                    return@launch
                }

                val hospital = hospitalList.value?.find { it.id == loginUser.hospitalId }
                userDataStore.saveUser(loginUser, hospital)

                // initData() 먼저 끝까지 기다림
                loginUser.hospitalId?.let { id ->
                    try {
                        initData(id)
                    } catch (e: Exception) {
                        Log.e("LOGIN", "초기 데이터 로딩 실패", e)
                    }
                }

                // fcm 토큰 전송도 기다림 (launch 말고 그냥 suspend 호출)
                loginUser.uuid.let { userId ->
                    FirebaseTokenManager.getToken()?.let { fcmToken ->
                        viewModelScope.launch {
                            notificationRepository.sendFcmToken(userId, fcmToken)
                            //서버로 fcm 토큰을 잘 보내고 있는지 확인하기 위한 코드, 나중에 지우기
                            Log.d("FCM", "Sending token to server: $fcmToken")
                            _loginSuccess.emit(true)

                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("LOGIN", "Login error", e)
                errorMessage = e.message ?: "로그인 중 문제가 발생했습니다."
                _loginSuccess.emit(false)
            }
        }
    }

}
