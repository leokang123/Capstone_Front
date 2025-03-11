package com.example.myapplication.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.ApiService
import com.example.myapplication.network.LoginRequest
import com.example.myapplication.network.LoginResponse
import com.example.myapplication.ui.screen.saveToken

class LoginRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context).create(ApiService::class.java)

    /**
     * 로그인 API 호출 (POST 요청)
     */
    suspend fun loginUser(email: String, password: String, context: Context): String? {
        return try {
            val response: LoginResponse = apiService.login(LoginRequest(email, password))
            saveToken(context, response.token)
            response.token

        } catch (e: Exception) {
            Log.e("LOGIN_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }
}