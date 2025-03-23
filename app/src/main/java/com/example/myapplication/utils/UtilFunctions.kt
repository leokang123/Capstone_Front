package com.example.myapplication.utils

import android.content.Context
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * 에뮬레이터 판다함수
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
 * 로그아웃
 */
fun logout(context: Context) {
    val userDataStore = UserDataStore(context)
    CoroutineScope(Dispatchers.IO).launch {  // 비동기 처리 (IO 작업에 적합)
        userDataStore.clearUserData()
    }
}

/**
 * 현재 시각 가져오는 함수
 */
fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date()) // 현재 시간 반환
}
