package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.entity.AlarmData
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 예시 viewModel
 * reset함수는 데이터를 viewModel로 관리하니까 초기화 되면 더 좋은순간에
 * 데이터가 남아있는 상황이 생겨서 억지로 넣어놨는데, 더 좋은 방법있으면 수정해도 됨
 */

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val masterDataRepository: MasterDataRepository,
) : ViewModel() {

    private val _alarmList = MutableStateFlow<List<AlarmData>>(emptyList())
    val alarmList: StateFlow<List<AlarmData>> = _alarmList

    suspend fun getAlarmList() {
        val response = masterDataRepository.getAlarmList()
        _alarmList.value = response ?: emptyList()
    }
}