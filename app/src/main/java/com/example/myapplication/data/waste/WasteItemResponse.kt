package com.example.myapplication.data.waste

data class WasteItemResponse(
    val id: Long,  // 서버에서 생성된 ID
    val userId: Long,  // 서버에서 생성된 ID
    val registrantName: String,
    val wasteType: String,
    val wasteDetails: String?,
    val location: String,
    val selectedDate: String,
    val selectedDevice: String?,
    val status: String, // "수집", "이동", "저장", "배출"
    val storageId: Long,
    val storageName: String
)