package com.example.myapplication.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.example.myapplication.data.waste.WasteStatus


/**
 * 에뮬레이터 판단함수
 */
fun isEmulator(): Boolean {
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


/**
 * 현재 시각 가져오는 함수
 */

fun requestAllPermissions(activity: Activity) {
    val permissionsToRequest = mutableListOf<String>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val blePermissions = mutableListOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            blePermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionsToRequest += blePermissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    if (permissionsToRequest.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            activity,
            permissionsToRequest.toTypedArray(),
            1
        )
    }
}


fun getAutoTextColor(backgroundColor: Color): Color {
    val luminance = ColorUtils.calculateLuminance(backgroundColor.toArgb())
    return if (luminance > 0.5) Color.Black else Color.White
}

fun getStatusColor(status: WasteStatus?): Color {
    return when (status?.statusLevel) {
        1 -> Color(0xFFFB8C00) // 수집중 - Orange
        2 -> Color(0xFFFBC02D) // 이송중 - Amber
        3 -> Color(0xFFC0CA33) // 이송완료 - Lime Green
        4 -> Color(0xFF8D6E63) // 저장중 - Brown
        5 -> Color(0xFFE53935) // 배출 완료 - Red
        else -> Color.Gray
    }
}
