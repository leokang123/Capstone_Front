package com.example.myapplication.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.data.auth.RegisterResponse
import com.example.myapplication.data.waste.MoveRequests
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.data.waste.WasteItemDetailResponse
import com.example.myapplication.data.waste.WasteItemRequest
import com.example.myapplication.data.waste.WasteItemResponse
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.ApiService

interface WasteRepository {
    suspend fun registerWaste(wasteRegisterRequest: WasteItemRequest): String?
    suspend fun searchWasteItems(searchRequest: SearchRequest): List<WasteItemResponse>?

    suspend fun getWasteItems(): List<WasteItemResponse>?

    suspend fun getStorageWasteItems(storageId: Long): List<WasteItemResponse>?
    suspend fun getWasteItemsByName(wasteType: String): List<WasteItemResponse>?
    suspend fun getWasteStorage(): List<WasteStorage>?


    suspend fun moveWasteItems(moveRequests: MoveRequests)

    suspend fun getDetailWasteItem(itemId: Long): WasteItemDetailResponse
    suspend fun checkItemStatus(itemId: Long): Boolean

    suspend fun updateItem(wasteItem: WasteItemDetailResponse)

    suspend fun deleteItem(itemId: Long): Boolean
}