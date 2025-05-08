package com.example.myapplication.data.waste

import java.time.LocalDateTime

data class SearchRequest(
    val wasteId: String? = null,
    val beaconId: Int? = null,
    val wasteTypeId: Int? = null,
    val wasteStatusId: Int? = null,
    val wasteStorageId: Int? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
)
