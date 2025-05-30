package com.example.myapplication.viewmodel

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entity.AlarmData
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 예시 viewModel
 * reset함수는 데이터를 viewModel로 관리하니까 초기화 되면 더 좋은순간에
 * 데이터가 남아있는 상황이 생겨서 억지로 넣어놨는데, 더 좋은 방법있으면 수정해도 됨
 */

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val masterDataRepository: MasterDataRepository,
    private val userDataStore: UserDataStore,
) : ViewModel() {

    private val _alarmList = MutableStateFlow<List<AlarmData>>(emptyList())
    val alarmList: StateFlow<List<AlarmData>> = _alarmList

    private val _hasNotification = MutableStateFlow(false)
    val hasNotification: StateFlow<Boolean> = _hasNotification.asStateFlow()

    init {
        viewModelScope.launch {
            userDataStore.hasNotification.collect {
                _hasNotification.value = it
            }
        }
    }

    suspend fun getAlarmList() {
        val response = masterDataRepository.getAlarmList()
        _alarmList.value = response ?: emptyList()
    }


    fun setNotificationState(active: Boolean) {
        _hasNotification.value = active
        viewModelScope.launch {
            userDataStore.saveHasNotification(active)
        }
    }

}