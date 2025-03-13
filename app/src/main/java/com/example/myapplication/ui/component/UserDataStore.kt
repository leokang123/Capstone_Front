package com.example.myapplication.ui.component

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore("user_prefs")

class UserDataStore(private val context: Context) {
    private val gson = Gson()

    suspend fun saveUser(user: User, token: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("user")] = gson.toJson(user)
            prefs[stringPreferencesKey("token")] = token
        }
        Log.d("USER", user.toString())
    }

    fun getUser(): User? = runBlocking {
        val userJson = context.dataStore.data.first()[stringPreferencesKey("user")]
        userJson?.let { gson.fromJson(it, User::class.java) }
    }

    fun getToken(): String? = runBlocking {
        context.dataStore.data.first()[stringPreferencesKey("token")]
    }

    suspend fun clearUserData() {
        context.dataStore.edit { it.clear() }
    }

    companion object
}
