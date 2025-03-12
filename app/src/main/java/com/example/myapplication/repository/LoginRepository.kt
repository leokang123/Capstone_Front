package com.example.myapplication.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.data.LoginRequest
import com.example.myapplication.data.LoginResponse
import com.example.myapplication.data.RegisterRequest
import com.example.myapplication.data.RegisterResponse
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.ApiService
import com.example.myapplication.ui.component.saveToken

/**
 * 로그인 관련 처리 클래스
 */
class LoginRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context).create(ApiService::class.java)

    /**
     * 로그인 API 호출 (POST 요청)
     */
    suspend fun loginUser(loginRequest: LoginRequest, context: Context): String? {
        return try {
            val response: LoginResponse = apiService.login(loginRequest)
            saveToken(context, response.token)
            response.token

        } catch (e: Exception) {
            Log.e("LOGIN_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }

    suspend fun registerUser(registerRequest: RegisterRequest): String? {
        return try {
            val response: RegisterResponse = apiService.register(registerRequest)
            response.message

        } catch (e: Exception) {
            Log.e("REGISTER_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }
}