package com.example.myapplication.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.user.AppUser
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.data.waste.WasteItem
import com.example.myapplication.data.waste.WasteItemDetails
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.repository.WasteRepository
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 예시 viewmodel
 */
@HiltViewModel
class WasteListViewModel @Inject constructor(
    private val wasteRepository: WasteRepository,
    masterDataRepository: MasterDataRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _user = MutableStateFlow<AppUser?>(null)
    val user: StateFlow<AppUser?> = _user

    val wasteStorageList = masterDataRepository.storageList
    val wasteStatusList = masterDataRepository.wasteStatusList
    val wasteTypeList = masterDataRepository.wasteTypeList
    val beaconList = masterDataRepository.beaconList

    // 폐기물 리스트 (전체 리스트 + 검색 결과 포함)
    private val _wasteItems = MutableStateFlow<List<WasteItem>>(emptyList())
    val wasteList: StateFlow<List<WasteItem>> = _wasteItems

    // 선택된 폐기물 상세 정보
    private val _selectedItem = MutableStateFlow<WasteItemDetails?>(null)
    val selectedItem: StateFlow<WasteItemDetails?> = _selectedItem

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val mockList = listOf(
        WasteStorage(id = 1, storageName = "기본 창고 A"),
        WasteStorage(id = 2, storageName = "기본 창고 B")
    )


    init {
        viewModelScope.launch {
            _user.value = userDataStore.getUser()
        }
    }

    fun resetWasteList() {
        _wasteItems.value = emptyList()
        _selectedItem.value = null
    }

    // 전체 리스트 가져오기
    fun fetchWasteList(mode: Int = 1) {
        viewModelScope.launch {
            val collectingId = wasteStatusList.find { it.statusLevel == 1 }?.id
            val movingId = wasteStatusList.find { it.statusLevel == 2 }?.id
            val storingId = wasteStatusList.find { it.statusLevel == 3 }?.id
            val disposedId = wasteStatusList.find { it.statusLevel == 4 }?.id
            try {
                val response = wasteRepository.getWasteItems()
                if (mode == 1) { // disposed가 아닌 상태 모두 출력
                    _wasteItems.value = response?.filterNot { it.wasteStatusId == disposedId }
                        ?: emptyList() // API 결과 저장
                } else if (mode == 2) { // collecting, moving 상태 출력
                    _wasteItems.value =
                        response?.filterNot { it.wasteStatusId == storingId || it.wasteStatusId == disposedId }
                            ?: emptyList() // API 결과 저장
                } else if (mode == 3) { // storing상태만 출력
                    _wasteItems.value = response?.filter { it.wasteStatusId == storingId }
                        ?: emptyList() // API 결과 저장
                }
            } catch (e: Exception) {
                _wasteItems.value = emptyList() // 오류 발생 시 초기화
                Log.e("WasteListViewModel", "API 요청 실패", e)
            }
        }
    }

    fun fetchStorageWasteList(wasteStorageId: Int) {
        viewModelScope.launch {
            try {
                val response = wasteRepository.getStorageWasteItems(wasteStorageId)
                _wasteItems.value =
                    response?.filter { it.wasteStatusId == wasteStatusList.find { it.description == "STORED" }?.id }
                        ?: emptyList() // API 결과 저장
            } catch (e: Exception) {
                _wasteItems.value = emptyList() // 오류 발생 시 초기화
                Log.e("WasteListViewModel", "API 요청 실패", e)
            }
        }
    }

    // 서버에서 직접 검색 API 요청
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

    fun getWasteItemDetails(itemId: String) {
        viewModelScope.launch {
            try {
                val result = wasteRepository.getDetailWasteItem(itemId)
                _selectedItem.value = result // 선택된 아이템 업데이트
            } catch (e: Exception) {
                Log.e("WasteListViewModel", "상세 정보 요청 실패", e)
                _selectedItem.value = null // 오류 발생 시 초기화
            }
        }
    }

    suspend fun registerWasteItem(wasteItem: WasteItem): String? {
        return wasteRepository.registerWaste(wasteItem)
    }

    suspend fun checkItemStatus(itemId: String): Boolean {
        return try {
            val waste = wasteRepository.getWasteItem(itemId)
            val status = wasteStatusList.find { it.id == waste?.wasteStatusId }
            status?.statusLevel == 1
        } catch (e: Exception) {
            Log.e("WasteListViewModel", "상세 정보 요청 실패", e)
            throw e
        }
    }

    suspend fun updateItem(updatedItem: WasteItem): WasteItem? {
        return try {
            Log.d(TAG, updatedItem.toString())
            wasteRepository.updateItem(updatedItem)
        } catch (e: Exception) {
            Log.e("WasteListViewModel", "아이템 정정 실패", e)
            throw e
        }
    }

    // List uuid 로 한번에 상태 이동 처리
    suspend fun moveWasteItems(moveRequests: List<String>) {
        return wasteRepository.moveWasteItems(moveRequests)
    }

    suspend fun deleteItem(itemId: String): Boolean {
        return try {
            wasteRepository.deleteItem(itemId)
        } catch (e: Exception) {
            Log.e("WasteListViewModel", "아이템 삭제 실패", e)
            throw e
        }
    }
}
