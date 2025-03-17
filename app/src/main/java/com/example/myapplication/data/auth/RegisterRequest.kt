package com.example.myapplication.data.auth


data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val selectedHospital: String,
    val roleId : Long,
)
