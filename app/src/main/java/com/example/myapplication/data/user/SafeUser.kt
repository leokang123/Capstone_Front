package com.example.myapplication.data.user

data class SafeUser(
    val id: Long = 0,
    val email: String? = null,
    val name: String = "",
    val phoneNumber: String? = null,
    val profession: String? = null,
    val selectedHospital: String? = null,
    val roleId: Long = 1
)