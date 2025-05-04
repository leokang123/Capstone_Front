package com.example.myapplication.data.waste

data class WasteItemDetailResponse(
    val id: Long,
    val wasteType: String,
    val location: String,
    val registrantName: String,
    val selectedDate: String,
    val selectedDevice: String?,
    val status: String,
    val wasteStorage: WasteStorage?,
    val wasteDetails: List<WasteDetailResponse> // ✅ 상세 정보 리스트 포함
)