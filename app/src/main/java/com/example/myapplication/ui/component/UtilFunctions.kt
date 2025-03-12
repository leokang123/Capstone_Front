package com.example.myapplication.ui.component

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit

/**
 * SharedPreferences에 토큰 저장
 */
fun saveToken(context: Context, token: String) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit() { putString("auth_token", token) }
}

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
