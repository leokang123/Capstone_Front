package com.example.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
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
    private val _filteredItems = MutableStateFlow<List<WasteItemResponse>>(emptyList()) // ✅ 검색 결과 상태
    val filteredItems: StateFlow<List<WasteItemResponse>> = _filteredItems

    fun searchWasteByName(name: String){
        viewModelScope.launch {
            try {
                val result = wasteRepository.getWasteItemsByName(name) // ✅ API 요청
                _filteredItems.value = result ?: emptyList() // ✅ 검색 결과를 그대로 저장 (null 방지)
            } catch (e: Exception) {
                _filteredItems.value = emptyList() // ✅ 에러 발생 시 빈 리스트 반환
            }
        }
    }
}