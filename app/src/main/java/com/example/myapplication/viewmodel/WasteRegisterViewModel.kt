package com.example.myapplication.viewmodel

/**
 * 예시 viewmodel
 * 인데 이건 폐기물 등록을 하러 등록창에 갔다가 다시 돌아왔을때 데이터를 갱신하기 위해서 fetchData 틀만 잡아둠
 * 아마 다른 창들도 다 필요할거 같은 함수
 */
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.WasteItemRequest
import com.example.myapplication.data.WasteItemResponse
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.WasteRepository
import kotlinx.coroutines.launch


class WasteRegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val wasteRepository = WasteRepository(getApplication<Application>().applicationContext)

    // ✅ mutableStateListOf 사용하여 리스트 변경 감지
    var wasteList = mutableStateListOf<WasteItemResponse>()
        private set

    // ✅ 서버에서 폐기물 리스트 가져오기
    fun fetchWasteList() {
        viewModelScope.launch {
            try {
                val response = wasteRepository.getWasteItems()
                Log.d("123", response.toString())
                // ✅ response가 null이 아닐 경우에만 addAll 실행

                response?.let {
                    wasteList.clear()
                    wasteList.addAll(it)
                }
            } catch (e: Exception) {
                wasteList.clear()  // ✅ 오류 발생 시 리스트 초기화
                Log.e("WasteRegisterViewModel", "API 요청 실패", e)
            }
        }
    }
}

