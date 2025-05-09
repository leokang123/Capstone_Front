package com.example.myapplication.data.user

data class RealBeacon(
    val uuid: String,
    val deviceAddress: String,
    val major: Int,
    val minor: Int,
    val interval: Int,
    val battery: Int,
    val nearField: Boolean
)
