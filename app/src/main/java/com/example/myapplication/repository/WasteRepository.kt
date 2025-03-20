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

class WasteRepository(context: Context) {
    private val apiService = ApiClient.getInstance(context).create(ApiService::class.java)

    suspend fun registerWaste(wasteRegisterRequest: WasteItemRequest): String? {
        return try {
            val response: RegisterResponse = apiService.registerWasteItem(wasteRegisterRequest)
            response.message

        } catch (e: Exception) {
            Log.e("REGISTER_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }

    suspend fun searchWasteItems(searchRequest: SearchRequest) : List<WasteItemResponse>? {
        return try {
            val response = apiService.searchWasteItems(searchRequest)
            response

        } catch (e: Exception) {
            Log.e("GET_WASTE_LIST_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }

    suspend fun getWasteItems() : List<WasteItemResponse>? {
        return try {
            val response = apiService.getWasteItemList()
            response

        } catch (e: Exception) {
            Log.e("GET_WASTE_LIST_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }
    suspend fun getWasteItemsByName(wasteType: String) : List<WasteItemResponse>? {
        return try {
            val response = apiService.getWasteItemListByName(SearchRequest(wasteType = wasteType))
            response

        } catch (e: Exception) {
            Log.e("GET_WASTE_LIST_BY_NAME_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }

    suspend fun getWasteStorage() : List<WasteStorage>? {
        return try {
            val response = apiService.getStorageList()
            response

        } catch (e: Exception) {
            Log.e("GET_WASTE_STORAGE", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }


    suspend fun moveWasteItems(moveRequests: MoveRequests) {
        try {
            apiService.moveWasteItems(moveRequests)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getDetailWasteItem(itemId: Long): WasteItemDetailResponse {
        return try {
            apiService.getDetailWasteItem(itemId)
        } catch (e: Exception) {
            throw e
        }
    }
    suspend fun checkItemStatus(itemId: Long): Boolean {
        return try {
            apiService.checkItemStatus(itemId)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateItem(wasteItem: WasteItemDetailResponse) {
        return try {
            apiService.updateItem(wasteItem)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteItem(itemId: Long): Boolean {
        return try {
            apiService.deleteItem(itemId)
        } catch (e: Exception) {
            throw e
        }
    }
}