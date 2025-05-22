package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.data.user.AppUser
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    fun checkAuth(requiredRole: Roles) {
        viewModelScope.launch {
            val token = userDataStore.getAccessToken()
            val user = userDataStore.getUser()

            when {
                token.isNullOrBlank() -> _authState.value = AuthState.NotLoggedIn
                user?.roles?.contains(requiredRole) == false -> _authState.value = AuthState.Unauthorized(user.primaryRoles)
                else -> _authState.value = AuthState.Authorized(user)
            }
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object NotLoggedIn : AuthState()
    data class Unauthorized(val actualRole: Roles?) : AuthState()
    data class Authorized(val user: AppUser?) : AuthState()
}
