package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.user.User
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private val _loginSuccess = MutableSharedFlow<Boolean>()
    val loginSuccess = _loginSuccess.asSharedFlow()

    fun login() {
        viewModelScope.launch {
//            val mockUser = User(
//                uuid = "123",
//                userName = username,
//                password = password,
//                email = "$username@naver.com",
//                name = username,
//                phoneNumber = "01012341234",
//                hospitalId = 1,
//                roles = listOf(Roles.USER, Roles.WAREHOUSE_MANAGER),
//                primaryRoles = Roles.USER,
//                token = "123",
//                fcmToken = "123"
//            )
//            val mockHospital = Hospital(
//                id = 1,
//                hospitalName = "서울병원",
//                hospitalCall = "01012344321"
//            )
            val loginUser =
                loginRepository.loginUser(User(username = username.trim(), password = password))
            Log.d("LOGIN", loginUser.toString())
            if (loginUser != null) {
                masterDataRepository.initAll(loginUser.hospitalId ?: 0)
                val hospital = masterDataRepository.getHospital(loginUser.hospitalId ?: 1)

                userDataStore.saveUser(loginUser, hospital!!)

                // 필요한 리스트 전부 masterDataRepository 로드
                _loginSuccess.emit(true)
            } else {
                errorMessage = "Invalid username or password"
                _loginSuccess.emit(false)
            }
        }
    }
}
