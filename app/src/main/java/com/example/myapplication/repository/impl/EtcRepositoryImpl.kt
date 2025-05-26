package com.example.myapplication.repository.impl

import android.util.Log
import com.example.myapplication.data.entity.AlarmData
import com.example.myapplication.data.entity.Beacon
import com.example.myapplication.data.entity.Hospital
import com.example.myapplication.data.waste.WasteStatus
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.data.waste.WasteType
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.EtcRepository
import javax.inject.Inject

class EtcRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : EtcRepository {
    override suspend fun getHospitalList(): List<Hospital>? {
        val response = apiService.getAllHospital() // API 호출
        return if (response.isSuccessful) {
            val body = response.body()
            body
        } else {
            Log.e(
                "GET_HOSPITAL_LIST",
                "API 요청 실패: ${response.code()} - ${response.message()}"
            )
            throw Exception("API요청 실패: ${response.code()} - ${response.message()}")
        }
    }

    override suspend fun getStorageList(hospitalId: Int): List<WasteStorage>? {
        val response = apiService.getAllStorage(hospitalId) // API 호출
        return if (response.isSuccessful) {
            val body = response.body()
            body
        } else {
            Log.e(
                "GET_STORAGE_LIST",
                "API 요청 실패: ${response.code()} - ${response.message()}"
            )
            throw Exception("API요청 실패: ${response.code()} - ${response.message()}")
        }


    }

    override suspend fun getWasteTypeList(): List<WasteType>? {
        val response = apiService.getAllWasteType() // API 호출
        return if (response.isSuccessful) {
            val body = response.body()
            Log.d("TEST123", body.toString())
            body
        } else {
            Log.e(
                "GET_WASTETYPE_LIST",
                "API 요청 실패: ${response.code()} - ${response.message()}"
            )
            throw Exception("API요청 실패: ${response.code()} - ${response.message()}")
        }
    }

    override suspend fun getWasteStatusList(): List<WasteStatus>? {
        val response = apiService.getAllWasteStatus() // API 호출
        return if (response.isSuccessful) {
            val body = response.body()
            body
        } else {
            Log.e(
                "GET_WASTESTATUS_LIST",
                "API 요청 실패: ${response.code()} - ${response.message()}"
            )
            throw Exception("API요청 실패: ${response.code()} - ${response.message()}")
        }

    }

    override suspend fun getBeaconList(): List<Beacon>? {
        val response = apiService.getAllBeacons() // API 호출
        return if (response.isSuccessful) {
            val body = response.body()
            body
        } else {
            Log.e(
                "GET_BEACON_LIST",
                "API 요청 실패: ${response.code()} - ${response.message()}"
            )
            throw Exception("API요청 실패: ${response.code()} - ${response.message()}")
        }

    }

    override suspend fun getAlarmList(): List<AlarmData>? {
        val response = apiService.getAlarmList() // API 호출
        return if (response.isSuccessful) {
            val body = response.body()
            body
        } else {
            Log.e(
                "GET_ALARM_LIST",
                "API 요청 실패: ${response.code()} - ${response.message()}"
            )
            throw Exception("API요청 실패: ${response.code()} - ${response.message()}")
        }


    }
}