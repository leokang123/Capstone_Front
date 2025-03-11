package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class SharedViewModel : ViewModel() {
    var selectedBluetoothDevice by mutableStateOf<String?>(null)
        private set

    fun selectDevice(device: String) {
        selectedBluetoothDevice = device
    }

    fun clearDevice() {
        selectedBluetoothDevice = null
    }
}
