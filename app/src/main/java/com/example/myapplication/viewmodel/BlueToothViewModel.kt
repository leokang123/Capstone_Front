package com.example.myapplication.viewmodel

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.mock.MockBluetoothDevice
import com.example.myapplication.data.user.Beacon
import com.example.myapplication.data.user.RealBeacon
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.utils.isEmulator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    masterDataRepository: MasterDataRepository
) : ViewModel() {
    private val beaconList = masterDataRepository.beaconList

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { bluetoothManager.adapter }
    private val scanner: BluetoothLeScanner? by lazy { bluetoothAdapter?.bluetoothLeScanner }

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices

    private var scanCallback: ScanCallback? = null

    private val _mockDevices = MutableStateFlow<List<RealBeacon>>(emptyList())

    private val _serverBeacons = MutableStateFlow<List<Beacon>>(emptyList())
    val serverBeacons: StateFlow<List<Beacon>> = _serverBeacons

    private val _selectedBeaconId = MutableStateFlow<Int?>(null)
    val selectedBeaconId: StateFlow<Int?> = _selectedBeaconId

    fun selectBeacon(id: Int) {
        _selectedBeaconId.value = id
    }

    fun resetSelectedBeacon() {
        _selectedBeaconId.value = null
    }

    /** 🚀 블루투스 검색 시작 */
    fun startScan() {
        if (isEmulator()) {
            Log.d(TAG, "Running on Emulator - Using Mock Data")
            mockBluetoothDevices()
        } else {
            Log.d(TAG, "Starting Bluetooth scan...")
            scanRealDevices()
        }
        matchBeacons()
    }

    private fun matchBeacons() {
        // 일단 목 디바이스
        val scanned = _mockDevices.value.map { it.deviceAddress }
        // 실제 디바이스
//        val scanned = _devices.value.map {it.address}
        val serverBeacon = beaconList.filter {it.deviceAddress in scanned }
        _serverBeacons.value = serverBeacon
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
                            Log.d(
                                TAG,
                                "Device Found: ${device.name ?: "Unknown"} - ${device.address}"
                            )
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
        val beacon1 = RealBeacon(
            uuid = "fda50693-a4e2-4fb1-afcf-c6eb07647825",
            deviceAddress = "asdfawef",
            major = 1001,
            minor = 2001,
            interval = 1000,
            battery = 85,
            nearField = true
        )
        val beacon2 = RealBeacon(
            uuid = "fda50693-a4e2-4fb1-afcf-c6eb07647826",
            deviceAddress = "55",
            major = 1002,
            minor = 2002,
            interval = 900,
            battery = 76,
            nearField = false
        )
        val beacon3 = RealBeacon(
            uuid = "fda50693-a4e2-4fb1-afcf-c6eb07647827",
            deviceAddress = "2346",
            major = 1003,
            minor = 2003,
            interval = 1200,
            battery = 92,
            nearField = true
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

