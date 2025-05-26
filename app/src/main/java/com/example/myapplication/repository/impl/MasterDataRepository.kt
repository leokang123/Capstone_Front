package com.example.myapplication.repository.impl

import android.util.Log
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
        etcRepository.getAlarmList()
    } catch (_: Exception) {
        emptyList()
    }


    suspend fun initAll(hospitalId: Int) = supervisorScope {
        val hospitalDeferred = async {
            try {
                getHospitalList()
            } catch (e: Exception) {
                Log.e("INIT_DATA_ERROR", "병원 목록 실패", e)
                emptyList()
            }
        }

        val storageDeferred = async {
            try {
                getStorageList(hospitalId)
            } catch (e: Exception) {
                Log.e("INIT_DATA_ERROR", "보관소 목록 실패", e)
                emptyList()
            }
        }

        val beaconDeferred = async {
            try {
                getBeaconList()
            } catch (e: Exception) {
                Log.e("INIT_DATA_ERROR", "비콘 목록 실패", e)
                emptyList()
            }
        }

        val typeDeferred = async {
            try {
                getWasteTypeList()
            } catch (e: Exception) {
                Log.e("INIT_DATA_ERROR", "폐기물 유형 실패", e)
                emptyList()
            }
        }

        val statusDeferred = async {
            try {
                getWasteStatusList()
            } catch (e: Exception) {
                Log.e("INIT_DATA_ERROR", "폐기물 상태 실패", e)
                emptyList()
            }
        }

        hospitalList = hospitalDeferred.await()
        storageList = storageDeferred.await()
        beaconList = beaconDeferred.await()
        wasteTypeList = typeDeferred.await()
        wasteStatusList = statusDeferred.await()
    }
}
