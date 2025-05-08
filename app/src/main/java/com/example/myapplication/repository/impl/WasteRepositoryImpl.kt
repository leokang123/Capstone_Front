package com.example.myapplication.repository.impl

import android.util.Log
import com.example.myapplication.data.waste.MoveRequests
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.data.waste.WasteItem
import com.example.myapplication.data.waste.WasteItemDetails
import com.example.myapplication.data.waste.WasteItemResponse
import com.example.myapplication.data.waste.WasteLog
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.WasteRepository
import java.time.LocalDate
import javax.inject.Inject

class WasteRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    WasteRepository {

    override suspend fun registerWaste(wasteItem: WasteItem): String? {
        return try {
            val response = apiService.createWaste(wasteItem)
            val data = response.body()
            "${data?.id} 등록 완료"

        } catch (e: Exception) {
            Log.e("REGISTER_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }


    override suspend fun getWasteItems(): List<WasteItem>? {
        return try {
            val response = apiService.getAllWaste()
            response.body()

        } catch (e: Exception) {
            Log.e("GET_WASTE_LIST_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }

    override suspend fun getStorageWasteItems(storageId: Int): List<WasteItem>? {
        return try {
            val response = apiService.getAllWasteHs(storageId = storageId)
            response.body()

        } catch (e: Exception) {
            Log.e("GET_STORAGE_WASTE_LIST_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }

//    override suspend fun getDetailWasteItem(itemId: String): List<WasteLog>? {
//        return try {
//            val response = apiService.getWasteLog(itemId)
//            response.body()
//        } catch (e: Exception) {
//            throw e
//        }
//    }

    override suspend fun getDetailWasteItem(itemId: String): WasteItemDetails? {
        return try {
            val response = apiService.getAllData(itemId)
            response.body()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateItem(wasteItem: WasteItem): WasteItem? {
        return try {
            val wasteItemId = wasteItem.id ?: ""
            val response = apiService.updateWaste(wasteItemId, wasteItem)
            response.body()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteItem(itemId: String): Boolean {
        return try {
            apiService.deleteWaste(itemId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun searchWasteItems(searchRequest: SearchRequest): List<WasteItem>? {
        return try {
            val response = apiService.getAllWasteHs(
                wasteId = searchRequest.wasteId,
                beaconId = searchRequest.beaconId,
                wasteTypeId = searchRequest.wasteTypeId,
                wasteStatusId = searchRequest.wasteStatusId,
                storageId = searchRequest.wasteStorageId,
                startDate = searchRequest.startDate,
                endDate = searchRequest.endDate,
            )
            response.body()

        } catch (e: Exception) {
            Log.e("GET_WASTE_LIST_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }

    override suspend fun getWasteItem(itemId: String): WasteItem? {
        return try {
            val response = apiService.getWaste(itemId)
            response.body()
        } catch (e: Exception) {
            throw e
        }
    }

    // getWasteItem으로 폐기물정보 받아오고 statusId를 미리 불러왔을 statusList랑 매핑하여
    // 삭제 가능한 상태인지 아닌지 "예외적으로" 프론트에서 판단
    override suspend fun checkItemStatus(itemId: Long): Boolean {
        return try {
//            apiService.checkItemStatus(itemId)
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getWasteItemsByName(wasteId: String): List<WasteItem>? {
        return try {
            val response = apiService.getAllWasteHs(wasteId = wasteId)
            response.body()

        } catch (e: Exception) {
            Log.e("GET_WASTE_LIST_BY_NAME_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
            null
        }
    }

    override suspend fun moveWasteItem(wasteItemId: String) {
        try {
            apiService.transportStatus(wasteItemId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun moveWasteItems(wasteItemIdList: List<String>) {
        try {
            wasteItemIdList.forEach {
                apiService.transportStatus(it)
            }
        } catch (e: Exception) {
            throw e
        }
    }


}