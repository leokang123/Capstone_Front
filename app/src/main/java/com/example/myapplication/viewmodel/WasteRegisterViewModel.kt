package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
/**
 * 예시 viewmodel
 * 인데 이건 폐기물 등록을 하러 등록창에 갔다가 다시 돌아왔을때 데이터를 갱신하기 위해서 fetchData 틀만 잡아둠
 * 아마 다른 창들도 다 필요할거 같은 함수
 */
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

