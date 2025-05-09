package com.example.myapplication.repository.impl

import android.util.Log
import com.example.myapplication.data.user.Beacon
import com.example.myapplication.data.user.Hospital
import com.example.myapplication.data.waste.WasteStatus
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.data.waste.WasteType
import com.example.myapplication.repository.EtcRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterDataRepository @Inject constructor(
    private val etcRepository: EtcRepository
) {
    var hospitalList: List<Hospital> = emptyList()
    var storageList: List<WasteStorage> = emptyList()
    var beaconList: List<Beacon> = emptyList()
    var wasteTypeList: List<WasteType> = emptyList()
    var wasteStatusList: List<WasteStatus> = emptyList()

    // 빠른 조회용 Map
    val hospitalMap get() = hospitalList.associateBy { it.id }
    val storageMap get() = storageList.associateBy { it.id }
    val beaconMap get() = beaconList.associateBy { it.id }
    val wasteTypeMap get() = wasteTypeList.associateBy { it.id }
    val wasteStatusMap get() = wasteStatusList.associateBy { it.id }

    suspend fun getHospitalList() = etcRepository.getHospitalList()

    suspend fun initAll(hospitalId: Int) = supervisorScope {
        try {
            val hospitalDeferred = async { getHospitalList() }
            val storageDeferred = async { etcRepository.getStorageList(hospitalId) }
            val beaconDeferred = async { etcRepository.getBeaconList() }
            val typeDeferred = async { etcRepository.getWasteTypeList() }
            val statusDeferred = async { etcRepository.getWasteStatusList() }

            hospitalList = hospitalDeferred.await() ?: emptyList()
            storageList = storageDeferred.await() ?: emptyList()
            beaconList = beaconDeferred.await() ?: emptyList()
            wasteTypeList = typeDeferred.await() ?: emptyList()
            wasteStatusList = statusDeferred.await() ?: emptyList()
        } catch (e: Exception) {
            Log.e("INIT_DATA_ERROR", e.message.toString())
        }

    }

    fun getHospital(id: Int): Hospital? = hospitalMap[id]
    fun getStorage(id: Int): WasteStorage? = storageMap[id]
    fun getBeacon(id: Int): Beacon? = beaconMap[id]
    fun getWasteType(id: Int): WasteType? = wasteTypeMap[id]
    fun getWasteStatus(id: Int): WasteStatus? = wasteStatusMap[id]
}
