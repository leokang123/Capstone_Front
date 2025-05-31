package com.example.myapplication.data.waste

data class WasteItemDetails(
    val id: String,
    val hospital: Int,
    val storage: Int?,
    val beacon: Int,
    val wasteType: Int,
    val wasteStatus: Int,
    val description: String,
    val logs: List<WasteLog>
)
