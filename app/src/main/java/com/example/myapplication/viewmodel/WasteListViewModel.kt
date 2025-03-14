package com.example.myapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.WasteItemResponse
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
    // ✅ 전체 리스트 가져오기
    fun fetchWasteList() {
        viewModelScope.launch {
            try {
                val response = wasteRepository.getWasteItems()
                _wasteItems.value = response ?: emptyList() // ✅ API 결과 저장
            } catch (e: Exception) {
                _wasteItems.value = emptyList() // 오류 발생 시 초기화
                Log.e("WasteListViewModel", "API 요청 실패", e)
            }
        }
    }

    // ✅ 서버에서 직접 검색 API 요청
    fun searchWasteByName(name: String) {
        viewModelScope.launch {
            try {
                val result = wasteRepository.getWasteItemsByName(name)
                _wasteItems.value = result ?: emptyList()
            } catch (e: Exception) {
                _wasteItems.value = emptyList() // 오류 발생 시 빈 리스트 반환
                Log.e("WasteListViewModel", "검색 API 요청 실패", e)
            }
        }
    }
}
