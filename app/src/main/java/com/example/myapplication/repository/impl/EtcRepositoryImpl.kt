package com.example.myapplication.repository.impl

import com.example.myapplication.data.user.Beacon
import com.example.myapplication.data.user.Hospital
import com.example.myapplication.data.waste.WasteStatus
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.data.waste.WasteType
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.EtcRepository
import javax.inject.Inject

class EtcRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): EtcRepository {
    override suspend fun getHospitalList(): List<Hospital>? {
        return try {
            val response = apiService.getAllHospital() // API 호출
            response.body()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun getStorageList(hospitalId: Int): List<WasteStorage>? {
        return try {
            val response = apiService.getAllStorage(hospitalId) // API 호출
            response.body()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun getWasteTypeList(): List<WasteType>? {
        return try {
            val response = apiService.getAllWasteType() // API 호출
            response.body()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
    override suspend fun getWasteStatusList(): List<WasteStatus>? {
        return try {
            val response = apiService.getAllWasteStatus() // API 호출
            response.body()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun getBeaconList(): List<Beacon>? {
        return try {
            val response = apiService.getAllBeacons() // API 호출
            response.body()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}