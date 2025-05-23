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
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val masterDataRepository: MasterDataRepository,
    private val userDataStore: UserDataStore
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

                // 병원 정보 찾기
                val hospital = hospitalList.value?.find { it.id == loginUser.hospitalId }

                // 사용자 저장
                userDataStore.saveUser(loginUser, hospital)

                // 초기 데이터 로딩
                loginUser.hospitalId?.let { initData(it) }

                // 성공 알림
                _loginSuccess.emit(true)

            } catch (e: Exception) {
                Log.e("LOGIN", "Login error", e)
                errorMessage = e.message ?: "로그인 중 문제가 발생했습니다."
                _loginSuccess.emit(false)
            }
        }
    }

}
