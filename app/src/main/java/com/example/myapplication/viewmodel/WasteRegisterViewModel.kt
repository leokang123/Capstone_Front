package com.example.myapplication.viewmodel

/**
 * 예시 viewmodel
 * 인데 이건 폐기물 등록을 하러 등록창에 갔다가 다시 돌아왔을때 데이터를 갱신하기 위해서 fetchData 틀만 잡아둠
 * 아마 다른 창들도 다 필요할거 같은 함수
 */
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class WasteItem(
    val registrantName: String,
    val wasteType: String,
    val wasteDetails: String,
    val location: String,
    val selectedDate: String,
    val selectedDevice: String?
)

class WasteRegisterViewModel : ViewModel() {
    // ✅ 등록된 폐기물 목록을 관리하는 리스트
    var wasteList = mutableStateListOf<WasteItem>() // ✅ mutableStateListOf 사용
        private set


    // ✅ 서버에서 폐기물 리스트 가져오기
    fun fetchWasteList() {
        viewModelScope.launch {
            try {
                val response = fetchWasteListFromServer()
                wasteList.clear()  // ✅ 기존 리스트 초기화
                wasteList.addAll(response)  // ✅ 새로운 데이터 추가
            } catch (e: Exception) {
                wasteList.clear()  // ✅ 오류 발생 시 리스트 초기화
            }
        }
    }

    // ✅ 서버에서 데이터를 가져오는 함수 (실제 API 요청으로 변경 필요) (예시)
    private suspend fun fetchWasteListFromServer(): List<WasteItem> {
        return listOf(
            WasteItem("홍길동", "의료 폐기물", "수술 후 폐기물", "서울 병원", "2025-03-11", "기기1"),
            WasteItem("김철수", "전자 폐기물", "고장난 의료 장비", "부산 병원", "2025-03-10", "기기2")
        )
    }
}

