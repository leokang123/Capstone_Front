package com.example.myapplication.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * BlueToothScreen 전용 viewModel
 * 사용할 수도 있고 안할 수도 있음 (그냥 어지간하면 다 viewmodel 만들었음)
 * Screen에서는 데이터를 보여주고, 데이터의 저장 및 변화나 갱신에 대한 연산은 거의 전부 viewModel에서 하는 느낌
 */
class BluetoothViewModel(application: Application) : AndroidViewModel(application) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val scanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices

    // ✅ 더미 블루투스 장치 목록 (에뮬레이터에서만 사용)
    private val _mockDevices = MutableStateFlow<List<MockBluetoothDevice>>(emptyList())
    val mockDevices: StateFlow<List<MockBluetoothDevice>> = _mockDevices

    fun startScan() {
        if (isEmulator()) {
            // ✅ 에뮬레이터 감지 시 더미 데이터 추가
            Log.d("BluetoothViewModel", "Running on Emulator - Using Mock Data")
            mockBluetoothDevices()
        } else {
            // ✅ 실제 스마트폰에서 블루투스 검색
            Log.d("BluetoothViewModel", "Starting Bluetooth scan...")
            scanRealDevices()
        }
    }

    @SuppressLint("MissingPermission")
    fun scanRealDevices() {
        if (scanner != null && bluetoothAdapter?.isEnabled == true) {
            if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.w("BluetoothViewModel", "BLUETOOTH_SCAN permission not granted.")
                return
            }
            Log.d("BluetoothViewModel", "Starting Bluetooth scan...")


            scanner.startScan(object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    result?.device?.let { device ->
                        val currentDevices = _devices.value.toMutableList()
                        if (!currentDevices.contains(device)) {
                            currentDevices.add(device)
                            _devices.value = currentDevices

                            // ✅ 블루투스 장치 정보 로그 출력
                            Log.d(
                                "BluetoothViewModel",
                                "Device Found: Name = ${device.name ?: "Unknown"}, Address = ${device.address}"
                            )
                        }
                    }
                }
                override fun onScanFailed(errorCode: Int) {
                    Log.e("BluetoothViewModel", "Scan failed with error code: $errorCode")
                }
            })
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        Log.d("BluetoothViewModel", "Stopping Bluetooth scan...")
        scanner?.stopScan(object : ScanCallback() {})
    }

    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.lowercase().contains("vbox")
                || Build.FINGERPRINT.lowercase().contains("test-keys")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT
                || Build.HARDWARE.contains("ranchu")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("vbox86"))
    }


    private fun mockBluetoothDevices() {
        val mockDevicesList = listOf(
            MockBluetoothDevice("Mock Device 1", "00:11:22:33:44:55"),
            MockBluetoothDevice("Mock Device 2", "66:77:88:99:AA:BB"),
            MockBluetoothDevice("Mock Device 3", "CC:DD:EE:FF:00:11")
        )

        _mockDevices.value = mockDevicesList
    }
}

// ✅ 더미 블루투스 장치 모델
data class MockBluetoothDevice(
    val name: String,
    val address: String
)