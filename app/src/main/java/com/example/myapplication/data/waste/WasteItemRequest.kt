package com.example.myapplication.data.waste

data class WasteItemRequest(
    val userId: Long,
    val wasteType: String,
    val wasteDetails: String?,
    val location: String,
    val selectedDate: String,
    val selectedDevice: String?,
    val storageId: Long?
)