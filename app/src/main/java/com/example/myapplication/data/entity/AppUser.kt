package com.example.myapplication.data.entity

import com.example.myapplication.data.enums.Roles

data class AppUser(
    val uuid: String = "",
    val email: String = "",
    val name: String = "",
    val phoneNumber: String? = null,
    val hospital: Hospital? = null,
    val roles: List<Roles>? = null,
    val primaryRoles: Roles? = null,
    val token: String? = null,
    val fcmToken: String? = null
)
