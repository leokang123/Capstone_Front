package com.example.myapplication.data.user

import com.example.myapplication.data.enums.Roles

data class AppUser(
    val uuid: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val hospital: Hospital? = null,
    val roles: List<Roles>? = null,
    val primaryRoles: Roles? = null,
    val token: String = "",
    val fcmToken: String = ""
)
