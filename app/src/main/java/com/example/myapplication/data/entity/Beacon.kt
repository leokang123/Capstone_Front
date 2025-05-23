package com.example.myapplication.data.entity

/** 수정 완료 **/

data class Beacon(
    val id: Int,
    val deviceAddress: String,
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
