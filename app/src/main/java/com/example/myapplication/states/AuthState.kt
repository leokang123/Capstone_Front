package com.example.myapplication.states

import com.example.myapplication.data.entity.AppUser
import com.example.myapplication.data.enums.Roles

sealed class AuthState {
    object Loading : AuthState()
    object NotLoggedIn : AuthState()
    data class Unauthorized(val actualRole: Roles?) : AuthState()
    data class Authorized(val user: AppUser?) : AuthState()
}