package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.user.AppUser
import com.example.myapplication.data.user.Hospital
import com.example.myapplication.data.user.User
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
//            val mockUser = User(
//                uuid = "123",
//                username = username,
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
                loginRepository.loginUser(
                    User(
                        username = username.trim(),
                        password = password
                    )
                )//?: mockUser
            Log.d("LOGIN", loginUser.toString())
            if (loginUser != null) {
                val hospital = hospitalList.value?.find { it.id == loginUser.hospitalId }
                userDataStore.saveUser(loginUser, hospital)
                initData(loginUser.hospitalId ?: 0)

                // 필요한 리스트 전부 masterDataRepository 로드
                _loginSuccess.emit(true)
            } else {
                errorMessage = "Invalid username or password"
                _loginSuccess.emit(false)
            }
        }
    }
}
