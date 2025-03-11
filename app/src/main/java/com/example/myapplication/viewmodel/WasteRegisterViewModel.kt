package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WasteRegisterViewModel : ViewModel() {
    var serverData by mutableStateOf<String?>(null)
        private set

    fun fetchData() {
        viewModelScope.launch {
//            try {
//                val response = fetchDataFromServer()
//                serverData = response
//            } catch (e: Exception) {
//                serverData = "데이터 가져오기 실패"
//            }
        }
    }

//    private suspend fun fetchDataFromServer(): List<WasteItem> {
//        return "서버에서 받은 최신 데이터"
//    }
}

