package com.example.myapplication.data.auth

import com.example.myapplication.data.user.User


data class LoginResponse(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)