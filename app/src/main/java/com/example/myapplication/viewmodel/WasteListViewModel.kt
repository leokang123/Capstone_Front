package com.example.myapplication.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entity.AppUser
import com.example.myapplication.data.entity.Beacon
import com.example.myapplication.data.waste.MoveRequest
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.data.waste.WasteItem
import com.example.myapplication.data.waste.WasteItemDetails
import com.example.myapplication.data.waste.WasteStatus
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.data.waste.WasteType
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

    val wasteStorageList: StateFlow<List<WasteStorage>> = masterDataRepository.storageList
    val wasteStatusList: StateFlow<List<WasteStatus>> = masterDataRepository.wasteStatusList
    val wasteTypeList: StateFlow<List<WasteType>> = masterDataRepository.wasteTypeList
    val beaconList: StateFlow<List<Beacon>> = masterDataRepository.beaconList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var storageBeaconList = emptyList<String?>()

    // 폐기물 리스트 (전체 리스트 + 검색 결과 포함)
    private val _wasteItems = MutableStateFlow<List<WasteItem>>(emptyList())
    val wasteList: StateFlow<List<WasteItem>> = _wasteItems

    // 선택된 폐기물 상세 정보
    private val _selectedItem = MutableStateFlow<WasteItemDetails?>(null)
    val selectedItem: StateFlow<WasteItemDetails?> = _selectedItem

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            _user.value = userDataStore.getUser()
            val beaconList = userDataStore.getBeaconList()
            val storageBeaconIdList = userDataStore.getWasteStorageList().map { it.beacon }
            storageBeaconList =
                beaconList.filter { it.id in storageBeaconIdList }.map { it.deviceAddress }
        }
    }

    fun resetWasteList() {
        _wasteItems.value = emptyList()
        _selectedItem.value = null
    }

    fun isLoading(isTrue: Boolean) {
        _isLoading.value = isTrue
    }

    // 전체 리스트 가져오기
    fun fetchWasteList(mode: Int = 1, beaconList: List<String>? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response =
                    if (beaconList != null) wasteRepository.getWasteItemsByAddress(beaconList)
                    else wasteRepository.getWasteItems()

                val statusMap = wasteStatusList.value.associateBy { it.statusLevel }
                val filtered = when (mode) {
                    1 -> response?.filterNot { it.wasteStatusId == statusMap[5]?.id } ?: emptyList()
                    2 -> response?.filter { it.wasteStatusId == statusMap[1]?.id || it.wasteStatusId == statusMap[2]?.id }
                        ?: emptyList()

                    3 -> response?.filter { it.wasteStatusId == statusMap[3]?.id || it.wasteStatusId == statusMap[4]?.id }
                        ?: emptyList() // API 결과 저장
                    else -> emptyList()
                }
                _wasteItems.value = filtered.sortedByDescending { it.id }
            } catch (e: Exception) {
                _wasteItems.value = emptyList() // 오류 발생 시 초기화
                Log.e("WasteListViewModel", "API 요청 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchStorageWasteList(wasteStorageId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = wasteRepository.getStorageWasteItems(wasteStorageId)
                val targetIds = wasteStatusList.value
                    .filter { it.statusLevel == 3 || it.statusLevel == 4 }
                    .map { it.id }
                _wasteItems.value = response?.filter { it.wasteStatusId in targetIds }
                    ?: emptyList() // API 결과 저장
            } catch (e: Exception) {
                _wasteItems.value = emptyList() // 오류 발생 시 초기화
                Log.e("WasteListViewModel", "API 요청 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 서버에서 직접 검색 API 요청
    fun searchWasteItems(searchRequest: SearchRequest) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = wasteRepository.searchWasteItems(searchRequest)
                // valid가 false나 null이면 배출완료인거랑 삭제된 수집중이 같이 나오는데 수집중인거 없앰
                _wasteItems.value = result?.let {
                    if (searchRequest.isValid != true) it.filterNot { item -> item.wasteStatusId == 1 } else it
                }?.sortedByDescending { it.id } ?: emptyList()

            } catch (e: Exception) {
                _wasteItems.value = emptyList() // 오류 발생 시 빈 리스트 반환
                Log.e("WasteListViewModel", "검색 API 요청 실패", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getWasteItemDetails(itemId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = wasteRepository.getDetailWasteItem(itemId)
                _selectedItem.value = result // 선택된 아이템 업데이트
            } catch (e: Exception) {
                Log.e("WasteListViewModel", "상세 정보 요청 실패", e)
                _selectedItem.value = null // 오류 발생 시 초기화
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun registerWasteItem(wasteItem: WasteItem): String? {
        val wasteType = wasteTypeList.value.find { it.id == wasteItem.wasteTypeId }
        return if (wasteRepository.registerWaste(wasteItem)) {
            "${wasteType?.typeName} 등록 완료"
        } else {
            "${wasteType?.typeName} 등록 실패"
        }
    }

    suspend fun checkItemStatus(itemId: String): Boolean {
        return try {
            val waste = wasteRepository.getWasteItem(itemId)
            val status = wasteStatusList.value.find { it.id == waste?.wasteStatusId }
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
    suspend fun moveWasteItems(moveRequests: List<MoveRequest>) {
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
