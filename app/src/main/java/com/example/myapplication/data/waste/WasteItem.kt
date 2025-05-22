package com.example.myapplication.data.waste

import kotlinx.coroutines.flow.StateFlow

/** 수정 완료 **/

data class  WasteItem(
    val id: String? = null,  // 서버에서 생성된 ID
    val hospitalId: Int,
    val storageId: Int?,
    val beaconId: Int?,
    val wasteTypeId: Int?,
    val wasteStatusId: Int = 1,
    val description: String,
)
