package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.LoginRequest
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")

    private val _loginSuccess = MutableSharedFlow<Boolean>()
    val loginSuccess = _loginSuccess.asSharedFlow()

    fun login() {
        viewModelScope.launch {
            val response = loginRepository.loginUser(LoginRequest(username.trim(), password))
            if (response != null) {
                userDataStore.saveUser(
                    user = response.user,
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
                _loginSuccess.emit(true)
            } else {
                errorMessage = "Invalid username or password"
                _loginSuccess.emit(false)
            }
        }
    }
}
