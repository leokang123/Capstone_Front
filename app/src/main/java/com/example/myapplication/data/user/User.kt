package com.example.myapplication.data.user


data class User(
    val id: Long = 0,
    val userName: String,
    val password: String,
    val email: String? = null,
    val name: String = "",
    val phoneNumber: String? = null,
    val selectedHospital: String? = null,
    val role: Role? = null,
)