package com.example.myapplication.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.user.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.first


val Context.dataStore by preferencesDataStore("user_prefs")

class UserDataStore(private val context: Context) {
    private val gson = Gson()

    suspend fun saveUser(user: User, accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("user")] = gson.toJson(user)
            prefs[stringPreferencesKey("accessToken")] = accessToken
            prefs[stringPreferencesKey("refreshToken")] = refreshToken
        }
        Log.d("USER", user.toString())
    }

    suspend fun getUser(): User? {
        val userJson = context.dataStore.data.first()[stringPreferencesKey("user")]
        return userJson?.let { gson.fromJson(it, User::class.java) }
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.first()[stringPreferencesKey("accessToken")]
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.first()[stringPreferencesKey("refreshToken")]
    }

    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("accessToken")] = token
        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { it.clear() }
    }
}
