package com.example.myapplication.data.waste

import java.time.LocalDate

data class SearchRequest(
    val wasteId: String? = null,
    val beaconId: Int? = null,
    val wasteTypeId: Int? = null,
    val wasteStatusId: Int? = null,
    val wasteStorageId: Int? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isValid: Boolean? = false,
)
