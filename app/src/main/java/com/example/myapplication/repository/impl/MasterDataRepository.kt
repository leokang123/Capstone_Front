package com.example.myapplication.repository.impl

import android.util.Log
import com.example.myapplication.data.entity.AlarmData
import com.example.myapplication.data.entity.Beacon
import com.example.myapplication.data.entity.Hospital
import com.example.myapplication.data.waste.WasteStatus
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.data.waste.WasteType
import com.example.myapplication.repository.EtcRepository
import com.example.myapplication.utils.UserDataStore
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterDataRepository @Inject constructor(
    private val etcRepository: EtcRepository,
    private val userDataStore: UserDataStore
) {

    var hospitalList: List<Hospital> = emptyList()
    var storageList: List<WasteStorage> = emptyList()
    var beaconList: List<Beacon> = emptyList()
    var wasteTypeList: List<WasteType> = emptyList()
    var wasteStatusList: List<WasteStatus> = emptyList()
    var alarmList: List<AlarmData> = emptyList()


    // 빠른 조회용 Map
    val hospitalMap get() = hospitalList.associateBy { it.id }
    val storageMap get() = storageList.associateBy { it.id }
    val beaconMap get() = beaconList.associateBy { it.id }
    val wasteTypeMap get() = wasteTypeList.associateBy { it.id }
    val wasteStatusMap get() = wasteStatusList.associateBy { it.id }

    suspend fun getHospitalList(): List<Hospital> {
        return try {
            val list = etcRepository.getHospitalList()
            list?.let { userDataStore.saveHospitalList(it) }
            list?: emptyList()
        } catch (e: Exception) {
            userDataStore.getHospitalList()
        }
    }

    suspend fun getBeaconList(): List<Beacon> {
        return try {
            val list = etcRepository.getBeaconList()
            list?.let { userDataStore.saveBeaconList(it) }
            list?: emptyList()
        } catch (e: Exception) {
            userDataStore.getBeaconList()
        }
    }

    suspend fun getStorageList(hospitalId: Int): List<WasteStorage> {
        return try {
            val list = etcRepository.getStorageList(hospitalId)
            list?.let { userDataStore.saveStorageList(it) }
            list?: emptyList()
        } catch (e: Exception) {
            userDataStore.getWasteStorageList()
        }
    }

    suspend fun getWasteTypeList(): List<WasteType> {
        return try {
            val list = etcRepository.getWasteTypeList()
            list?.let { userDataStore.saveWasteTypeList(it) }
            list?: emptyList()
        } catch (e: Exception) {
            userDataStore.getWasteTypeList()
        }
    }

    suspend fun getWasteStatusList(): List<WasteStatus> {
        return try {
            val list = etcRepository.getWasteStatusList()
            list?.let { userDataStore.saveWasteStatusList(it) }
            list?: emptyList()
        } catch (e: Exception) {
            userDataStore.getWasteStatusList()
        }
    }

    suspend fun getAlarmList() = try {
        etcRepository.getAlarmList()
    } catch (e: Exception) {
        emptyList()
    }


    suspend fun initAll(hospitalId: Int) = supervisorScope {
        try {
            val hospitalDeferred = async { getHospitalList() }
            val storageDeferred = async { getStorageList(hospitalId) }
            val beaconDeferred = async { getBeaconList() }
            val typeDeferred = async { getWasteTypeList() }
            val statusDeferred = async { getWasteStatusList() }

            hospitalList = hospitalDeferred.await()
            storageList = storageDeferred.await()
            beaconList = beaconDeferred.await()
            wasteTypeList = typeDeferred.await()
            wasteStatusList = statusDeferred.await()

        } catch (e: Exception) {
            Log.e("INIT_DATA_ERROR", e.message.toString())
        }

    }

}
