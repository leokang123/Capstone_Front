package com.example.myapplication.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.data.waste.WasteItemDetailResponse
import com.example.myapplication.data.waste.WasteItemResponse
import com.example.myapplication.repository.WasteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 예시 viewmodel
 */
class WasteListViewModel(application: Application) : AndroidViewModel(application) {
    private val wasteRepository: WasteRepository = WasteRepository(getApplication<Application>().applicationContext)

    // ✅ 폐기물 리스트 (전체 리스트 + 검색 결과 포함)
    private val _wasteItems = MutableStateFlow<List<WasteItemResponse>>(emptyList())
    val wasteList: StateFlow<List<WasteItemResponse>> = _wasteItems

    // ✅ 선택된 폐기물 상세 정보
    private val _selectedItem = MutableStateFlow<WasteItemDetailResponse?>(null)
    val selectedItem: StateFlow<WasteItemDetailResponse?> = _selectedItem

    fun resetWasteList() {
        _wasteItems.value = emptyList()
        _selectedItem.value = null
    }

    // ✅ 전체 리스트 가져오기
    fun fetchWasteList(mode: Int = 1) {
        viewModelScope.launch {
            try {
                val response = wasteRepository.getWasteItems()
                if (mode == 1) {
                    _wasteItems.value = response?.filterNot { it.status == "DISPOSED" } ?: emptyList() // ✅ API 결과 저장
                } else if (mode == 2) {
                    _wasteItems.value = response?.filterNot { it.status == "STORED" || it.status == "DISPOSED" } ?: emptyList() // ✅ API 결과 저장
                } else if (mode == 3) {
                    _wasteItems.value = response?.filter { it.status == "STORED" } ?: emptyList() // ✅ API 결과 저장
                }
            } catch (e: Exception) {
                _wasteItems.value = emptyList() // 오류 발생 시 초기화
                Log.e("WasteListViewModel", "API 요청 실패", e)
            }
        }
    }

    // ✅ 서버에서 직접 검색 API 요청
    fun searchWasteItems(searchRequest: SearchRequest) {
        viewModelScope.launch {
            try {
                val result = wasteRepository.searchWasteItems(searchRequest)
                _wasteItems.value = result ?: emptyList()
            } catch (e: Exception) {
                _wasteItems.value = emptyList() // 오류 발생 시 빈 리스트 반환
                Log.e("WasteListViewModel", "검색 API 요청 실패", e)
            }
        }
    }

    fun getWasteItemDetails(itemId: Long) {
        viewModelScope.launch {
            try {
                val result = wasteRepository.getDetailWasteItem(itemId)
                _selectedItem.value = result // ✅ 선택된 아이템 업데이트
            } catch (e: Exception) {
                Log.e("WasteListViewModel", "상세 정보 요청 실패", e)
                _selectedItem.value = null // 오류 발생 시 초기화
            }
        }
    }

    suspend fun checkItemStatus(itemId: Long): Boolean {
        return try {
                wasteRepository.checkItemStatus(itemId)
            } catch (e: Exception) {
                Log.e("WasteListViewModel", "상세 정보 요청 실패", e)
                throw e
            }
        }
    suspend fun updateItem(updatedItem: WasteItemDetailResponse) {
        return try {
            Log.d(TAG,updatedItem.toString())
            wasteRepository.updateItem(updatedItem)
        } catch (e: Exception) {
            Log.e("WasteListViewModel", "아이템 정정 실패", e)
            throw e
        }
    }

    suspend fun deleteItem(itemId: Long): Boolean {
        return try {
            wasteRepository.deleteItem(itemId)
        } catch (e: Exception) {
            Log.e("WasteListViewModel", "아이템 삭제 실패", e)
            throw e
        }
    }
}
