package com.example.myapplication.utils

object FirebaseTokenManager {
    private var cachedToken: String? = null

    fun setToken(token: String) {
        cachedToken = token
    }

    fun getToken(): String? = cachedToken
}