package com.example.myapplication.repository

import com.example.myapplication.data.entity.User

interface LoginRepository {
    suspend fun loginUser(user: User): User?
    suspend fun registerUser(user: User): String?
    suspend fun logoutUser(uuid: String): Boolean

}
