package com.example.myapplication.repository.impl

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
}