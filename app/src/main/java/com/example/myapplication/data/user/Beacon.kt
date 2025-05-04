package com.example.myapplication.data.user

data class Beacon(
    val id: Int,
    val uuid: String,
    val deviceAddress: String,
    val major: Int,
    val minor: Int,
    val location: String,
    val label: String,
    val hospitalId: Int,
    val used: Boolean = false
)


//data class Beacon(
//    val id: Long,
//    val macAddress: String,
//    val hospital: Hospital? = null,
//    val isUsed: Boolean = false
//)
