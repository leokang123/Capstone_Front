package com.example.myapplication.data.waste

data class MoveRequest(
    val itemId: Long,
    val userId: Long,
    val wasteDetails: String,
    val date: String
)