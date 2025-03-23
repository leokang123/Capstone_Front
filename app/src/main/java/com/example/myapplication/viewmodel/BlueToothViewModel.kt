package com.example.myapplication.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.mock.MockBluetoothDevice
import com.example.myapplication.utils.isEmulator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * BlueToothScreen 전용 viewModel
 * 사용할 수도 있고 안할 수도 있음 (그냥 어지간하면 다 viewmodel 만들었음)
 * Screen에서는 데이터를 보여주고, 데이터의 저장 및 변화나 갱신에 대한 연산은 거의 전부 viewModel에서 하는 느낌
 */
class BlueToothViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothManager: BluetoothManager by lazy {
        application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { bluetoothManager.adapter }
    private val scanner: BluetoothLeScanner? by lazy { bluetoothAdapter?.bluetoothLeScanner }

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices

    private var scanCallback: ScanCallback? = null

    private val _mockDevices = MutableStateFlow<List<MockBluetoothDevice>>(emptyList())
    val mockDevices: StateFlow<List<MockBluetoothDevice>> = _mockDevices

    /** 🚀 블루투스 검색 시작 */
    fun startScan() {
        if (isEmulator()) {
            Log.d(TAG, "Running on Emulator - Using Mock Data")
            mockBluetoothDevices()
        } else {
            Log.d(TAG, "Starting Bluetooth scan...")
            scanRealDevices()
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanRealDevices() {
        if (scanner == null || bluetoothAdapter?.isEnabled != true) {
            Log.w(TAG, "Bluetooth is disabled or scanner is null.")
            return
        }

        if (!hasScanPermission()) return

        if (scanCallback == null) {
            scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    result?.device?.let { device ->
                        val currentDevices = _devices.value.toMutableList()
                        if (!currentDevices.contains(device)) {
                            currentDevices.add(device)
                            _devices.value = currentDevices
                            Log.d(TAG, "Device Found: ${device.name ?: "Unknown"} - ${device.address}")
                        }
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Log.e(TAG, "Scan failed with error code: $errorCode")
                }
            }
        }

        scanner?.startScan(scanCallback)
    }

    /** 🚀 블루투스 검색 중지 */
    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (scanner != null && scanCallback != null) {
            Log.d(TAG, "Stopping Bluetooth scan...")
            scanner?.stopScan(scanCallback)
            scanCallback = null
        } else {
            Log.w(TAG, "Scanner or callback is null, cannot stop scan.")
        }
    }



    private fun mockBluetoothDevices() {
        val mockDevicesList = listOf(
            MockBluetoothDevice("Mock Device 1", "00:11:22:33:44:55"),
            MockBluetoothDevice("Mock Device 2", "66:77:88:99:AA:BB"),
            MockBluetoothDevice("Mock Device 3", "CC:DD:EE:FF:00:11")
        )

        _mockDevices.value = mockDevicesList
    }

    private fun hasScanPermission(): Boolean {
        val context = getApplication<Application>().applicationContext
        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            Log.w(TAG, "BLUETOOTH_SCAN permission not granted.")
            false
        }
    }
}

