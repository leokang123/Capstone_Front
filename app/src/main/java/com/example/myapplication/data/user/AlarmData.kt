package com.example.myapplication.data.user

import java.time.LocalDateTime

data class AlarmData(
    val title: String,
    val message: String,
    val sendAt: LocalDateTime,
    val receiveAt: LocalDateTime
)
