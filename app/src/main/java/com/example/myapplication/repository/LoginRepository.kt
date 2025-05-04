package com.example.myapplication.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.myapplication.data.auth.LoginRequest
import com.example.myapplication.data.auth.LoginResponse
import com.example.myapplication.data.auth.RegisterRequest
import com.example.myapplication.data.user.Hospital
import org.json.JSONObject

interface LoginRepository {
    suspend fun loginUser(loginRequest: LoginRequest): LoginResponse?

    suspend fun registerUser(registerRequest: RegisterRequest): String?
    suspend fun getHospitalList(): List<Hospital>
}