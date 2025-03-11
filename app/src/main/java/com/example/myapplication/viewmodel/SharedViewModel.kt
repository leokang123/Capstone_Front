package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * 많은 창에서 공통적으로 사용되는 데이터를 관리하기위해 따로 만들어놓은 viewModel
 * 블루투스 검색 결과는 필요한 창이 여러개일수도 있어서 이렇게 해놨고, 여타 다른 비슷한 데이터들도
 * 여러창에서 공용으로 사용될거같으면 여기다 적으면 됨
 */
class SharedViewModel : ViewModel() {
    var selectedBluetoothDevice by mutableStateOf<String?>(null)
        private set

    fun selectDevice(device: String) {
        selectedBluetoothDevice = device
    }

    fun reset() {
        selectedBluetoothDevice = null
    }

}
