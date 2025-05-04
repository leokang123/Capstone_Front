package com.example.myapplication.data.waste

data class SearchRequest(
    val wasteType: String? = null,
    val registrantName: String? = null,
    val wasteStorageId: Long? = null,
    val selectedDate: String? = null,
    val selectedTime: String? = null,
    val combineDate: String? = null,
    val selectedDevice: String? = null,
    val wasteStatus: String? = null
)
