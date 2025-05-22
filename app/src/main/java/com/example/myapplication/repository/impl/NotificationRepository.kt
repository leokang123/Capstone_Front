package com.example.myapplication.repository.impl

import com.example.myapplication.data.fcmtoken.FcmLogout
import com.example.myapplication.network.ApiService
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun sendFcmToken(userId: String, token: String): Boolean {
        val body = mapOf("userId" to userId, "token" to token)
        return try {
            val response = apiService.registerFcmToken(body)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }


    // 로그아웃 시 FCM 토큰 삭제
    suspend fun removeFcmToken(userId: String, token: String): Boolean {
        val dto = FcmLogout(userId, token)
        return try {
            val response = apiService.logoutFcmToken(dto)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}