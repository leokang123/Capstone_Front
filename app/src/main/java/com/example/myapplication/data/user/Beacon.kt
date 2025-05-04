package com.example.myapplication.data.user

data class Beacon(
    val id: Long,
    val macAddress: String,
    val hospital: Hospital? = null,
    val isUsed: Boolean = false
)
