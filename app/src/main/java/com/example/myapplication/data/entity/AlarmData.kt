package com.example.myapplication.data.entity

data class AlarmData(
    val title: String,
    val message: String,
    val sendAt: String? = null,
    val receivedAt: String? = null,
)
