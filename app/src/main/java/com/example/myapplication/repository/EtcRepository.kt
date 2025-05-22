package com.example.myapplication.repository

import com.example.myapplication.data.user.AlarmData
import com.example.myapplication.data.user.Beacon
import com.example.myapplication.data.user.Hospital
import com.example.myapplication.data.waste.WasteStatus
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.data.waste.WasteType

interface EtcRepository {
    suspend fun getHospitalList(): List<Hospital>?
    suspend fun getStorageList(hospitalId: Int): List<WasteStorage>?
    suspend fun getWasteTypeList(): List<WasteType>?
    suspend fun getWasteStatusList(): List<WasteStatus>?
    suspend fun getBeaconList(): List<Beacon>?
    suspend fun getAlarmList(): List<AlarmData>?
}