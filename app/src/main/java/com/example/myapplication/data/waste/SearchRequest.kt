package com.example.myapplication.data.waste

data class SearchRequest(
    val itemId: Long = 0,
    val wasteType: String? = "",
    val registrantName: String? = "",
    val selectedDate: String? = "",
    val selectedDevice: String? = ""
)
