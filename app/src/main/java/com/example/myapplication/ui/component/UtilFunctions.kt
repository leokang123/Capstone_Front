package com.example.myapplication.ui.component

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * SharedPreferences에 토큰 저장
 */
fun saveToken(context: Context, token: String) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit() { putString("auth_token", token) }
}
