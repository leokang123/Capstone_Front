package com.example.myapplication.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.entity.Beacon
import com.example.myapplication.data.entity.RealBeacon
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.utils.isEmulator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * BlueToothScreen 전용 viewModel
 * 사용할 수도 있고 안할 수도 있음 (그냥 어지간하면 다 viewmodel 만들었음)
 * Screen에서는 데이터를 보여주고, 데이터의 저장 및 변화나 갱신에 대한 연산은 거의 전부 viewModel에서 하는 느낌
 */

@HiltViewModel
class BlueToothViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val masterDataRepository: MasterDataRepository
) : ViewModel() {
    private var beaconList: List<Beacon>? = masterDataRepository.beaconList.value   

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { bluetoothManager.adapter }
    private val scanner: BluetoothLeScanner? by lazy { bluetoothAdapter?.bluetoothLeScanner }

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())

    private var scanCallback: ScanCallback? = null

    private val _mockDevices = MutableStateFlow<List<RealBeacon>>(emptyList())

    private val _serverBeacons = MutableStateFlow<List<Beacon>>(emptyList())
    val serverBeacons: StateFlow<List<Beacon>> = _serverBeacons

    private val _notUsedServerBeacons = MutableStateFlow<List<Beacon>>(emptyList())
    val notUsedServerBeacon: StateFlow<List<Beacon>> = _notUsedServerBeacons

    private val _selectedBeaconId = MutableStateFlow<Int?>(null)
    val selectedBeaconId: StateFlow<Int?> = _selectedBeaconId

    fun selectBeacon(id: Int) {
        _selectedBeaconId.value = id
    }

    fun resetSelectedBeacon() {
        _selectedBeaconId.value = null
    }

    fun clearServerBeacons() {
        _serverBeacons.value = emptyList()
    }

    suspend fun updateBeaconList() {
        beaconList = masterDataRepository.getBeaconList()
    }

    /** 🚀 블루투스 검색 시작 */
    fun startScan(scanTime: Long = 10L) {
        if (isEmulator()) {
            Log.d(TAG, "Running on Emulator - Using Mock Data")
            mockBluetoothDevices()
        } else {
            Log.d(TAG, "Starting Bluetooth scan...")
            scanRealDevices(scanTime)
        }
//
    }


    suspend fun checkStorage(beaconAddress: String, scanTime: Long = 5L): Boolean {
        startScan(scanTime)
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < scanTime * 1000L) {
            val isStorageBeacon = _serverBeacons.value.find { it.deviceAddress == beaconAddress }
            if (isStorageBeacon != null) {
                return true
            }
            delay(200L)
        }

        // 5초 내에 발견 못했을 경우
        Log.w("RELOAD", "비콘을 찾지 못했습니다.")
        return false
    }

    private fun matchBeacons() {
        val scanned = _devices.value.map { it.address }
        val serverBeacon = beaconList?.filter { it.deviceAddress in scanned }
        _serverBeacons.value = serverBeacon ?: emptyList()
        val notUsedServerBeacon = serverBeacon?.filter { it.used == false }
        _notUsedServerBeacons.value = notUsedServerBeacon ?: emptyList()

    }

    @SuppressLint("MissingPermission")
    private fun scanRealDevices(scanTime: Long) {
        resetBeacon()
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
                            matchBeacons()

                        }
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Log.e(TAG, "Scan failed with error code: $errorCode")
                }
            }
        }
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner?.startScan(null, settings, scanCallback)

        android.os.Handler(context.mainLooper).postDelayed({
            stopScan()
            Log.d("BEACON_SCAN", "스캔 자동 종료됨")
        }, scanTime * 1000) // 10초 = 10,000ms
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

    fun resetBeacon() {
        _devices.value = emptyList()
        _serverBeacons.value = emptyList()
        _notUsedServerBeacons.value = emptyList()
    }

//    fun updateBeacon(updateBeacon: Beacon) {
//        serverBeacons.value.map {
//            if (it.id == updateBeacon.id) updateBeacon else it
//        }
//    }


    private fun mockBluetoothDevices() {
        val beacon1 = RealBeacon(
            name = "123",
            deviceAddress = "asdfawef",

            )
        val beacon2 = RealBeacon(
            name = "asddsa",
            deviceAddress = "55",

            )
        val beacon3 = RealBeacon(
            name = "asd123",
            deviceAddress = "2346",

            )

        val mockDevicesList = listOf(
            beacon1, beacon2, beacon3
        )

        _mockDevices.value = mockDevicesList
    }


    private fun hasScanPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            Log.w(TAG, "BLUETOOTH_SCAN permission not granted.")
            false
        }
    }
}

