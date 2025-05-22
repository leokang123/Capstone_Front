package com.example.myapplication.data.waste

/** 수정 완료 **/

data class WasteLog(
    val id: Int,
    val description: String,
    val statusId: Int,
    val wasteId: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String,
)
