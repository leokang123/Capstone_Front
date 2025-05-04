package com.example.myapplication.data.waste

data class WasteItemRequest(
    val wasteTypeId: String,
    val wasteDetails: String?,
    val location: String,
    val selectedDate: String,
    val selectedDevice: String?,
    val storageId: Long?
)