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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val masterDataRepository: MasterDataRepository,
    private val userDataStore: UserDataStore,
) : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    val hospitalList = MutableStateFlow<List<Hospital>?>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginSuccess = MutableSharedFlow<Boolean>()
    val loginSuccess = _loginSuccess.asSharedFlow()


    fun onResumed() {
        viewModelScope.launch {
            val userHospitalId = userDataStore.getUser()?.hospital?.id
            Log.d("USER", userDataStore.getUser().toString())
            userHospitalId?.let { masterDataRepository.initAll(it) }
            delay(1000)
        }
    }

    suspend fun getHospitalList() {
        hospitalList.value = masterDataRepository.getHospitalList()
    }

    suspend fun initData(hospitalId: Int) {
        masterDataRepository.initAll(hospitalId)
    }

    suspend fun checkAutoLogin(): AppUser? {
        return userDataStore.getUser()
    }


    fun login() {
        viewModelScope.launch {
            try {
                _isLoading.value = true  // 로딩 시작

                val loginUser = loginRepository.loginUser(
                    User(
                        username = username.trim(),
                        password = password
                    )
                )

                if (loginUser == null) {
                    errorMessage = "Invalid username or password"
                    _isLoading.value = false
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
                _loginSuccess.emit(true)


            } catch (e: Exception) {
                Log.e("LOGIN", "Login error", e)
                errorMessage = e.message ?: "로그인 중 문제가 발생했습니다."
                _isLoading.value = false
                _loginSuccess.emit(false)
            }
        }
    }

}
