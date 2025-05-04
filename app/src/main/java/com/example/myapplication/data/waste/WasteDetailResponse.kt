package com.example.myapplication.data.waste

import com.example.myapplication.data.user.User

data class WasteDetailResponse(
    val id: Long,
    val wasteDetails: String,
    val date: String,
    val status: String,
    val user:  User
)
