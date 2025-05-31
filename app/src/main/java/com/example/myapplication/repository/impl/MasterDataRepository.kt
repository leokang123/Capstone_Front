package com.example.myapplication.repository.impl

import android.util.Log
import com.example.myapplication.data.entity.Beacon
import com.example.myapplication.data.entity.Hospital
import com.example.myapplication.data.waste.WasteStatus
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.data.waste.WasteType
import com.example.myapplication.repository.EtcRepository
import com.example.myapplication.utils.UserDataStore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterDataRepository @Inject constructor(
    private val etcRepository: EtcRepository,
    private val userDataStore: UserDataStore
) {
    private val _hospitalList = MutableStateFlow<List<Hospital>>(emptyList())
    val hospitalList: StateFlow<List<Hospital>> = _hospitalList

    private val _storageList = MutableStateFlow<List<WasteStorage>>(emptyList())
    val storageList: StateFlow<List<WasteStorage>> = _storageList

    private val _beaconList = MutableStateFlow<List<Beacon>>(emptyList())
    val beaconList: StateFlow<List<Beacon>> = _beaconList

    private val _wasteStatusList = MutableStateFlow<List<WasteStatus>>(emptyList())
    val wasteStatusList: StateFlow<List<WasteStatus>> = _wasteStatusList
    private val _wasteTypeList = MutableStateFlow<List<WasteType>>(emptyList())
    val wasteTypeList: StateFlow<List<WasteType>> = _wasteTypeList

    suspend fun getHospitalList(): List<Hospital> {
        return try {
            val list = etcRepository.getHospitalList()
            list?.let { userDataStore.saveHospitalList(it) }
            list ?: emptyList()
        } catch (_: Exception) {
            userDataStore.getHospitalList()
        }
    }

    suspend fun getBeaconList(): List<Beacon> {
        return try {
            val list = etcRepository.getBeaconList()
            list?.let { userDataStore.saveBeaconList(it) }
            list ?: emptyList()
        } catch (_: Exception) {
            userDataStore.getBeaconList()
        }
    }


    suspend fun getStorageList(hospitalId: Int): List<WasteStorage> {
        return try {
            val list = etcRepository.getStorageList(hospitalId)
            list?.let { userDataStore.saveStorageList(it) }
            list ?: emptyList()
        } catch (_: Exception) {
            userDataStore.getWasteStorageList()
        }
    }

    suspend fun getWasteTypeList(): List<WasteType> {
        return try {
            val list = etcRepository.getWasteTypeList()
            list?.let { userDataStore.saveWasteTypeList(it) }
            list ?: emptyList()
        } catch (_: Exception) {
            userDataStore.getWasteTypeList()
        }
    }

    suspend fun getWasteStatusList(): List<WasteStatus> {
        return try {
            val list = etcRepository.getWasteStatusList()
            list?.let { userDataStore.saveWasteStatusList(it) }
            list ?: emptyList()
        } catch (_: Exception) {
            userDataStore.getWasteStatusList()
        }
    }

    suspend fun getAlarmList() = try {
        val alarmList = etcRepository.getAlarmList()
        alarmList
    } catch (e: Exception) {
        Log.e("ERROR_ALRAM", e.toString())
        emptyList()
    }


    suspend fun initAll(hospitalId: Int) = supervisorScope {
        val hospitalDeferred = async { getHospitalList() }
        val storageDeferred = async { getStorageList(hospitalId) }
        val beaconDeferred = async { getBeaconList() }
        val typeDeferred = async { getWasteTypeList() }
        val statusDeferred = async { getWasteStatusList() }

        _hospitalList.value = hospitalDeferred.await()
        _storageList.value = storageDeferred.await()
        _beaconList.value = beaconDeferred.await()
        _wasteTypeList.value = typeDeferred.await()
        _wasteStatusList.value = statusDeferred.await()
    }
}
