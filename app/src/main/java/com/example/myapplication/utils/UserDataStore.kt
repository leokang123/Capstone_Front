package com.example.myapplication.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.entity.AppUser
import com.example.myapplication.data.entity.Hospital
import com.example.myapplication.data.entity.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject


val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = context.userDataStore

    private val gson = Gson()

    suspend fun saveUser(user: User, hospital: Hospital?) {
        val appUser = AppUser(
            uuid = user.uuid,
            email = user.email,
            name = user.name,
            phoneNumber = user.phoneNumber,
            hospital = hospital,
            roles = user.roles,
            primaryRoles = user.primaryRoles,
            token = user.token,
            fcmToken = user.fcmToken

        )
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("user")] = gson.toJson(appUser)
            prefs[stringPreferencesKey("accessToken")] = appUser.token!!
        }
        Log.d("USER", user.toString())
    }

    suspend fun getUser(): AppUser? {
        val userJson = dataStore.data.first()[stringPreferencesKey("user")]
        return userJson?.let { gson.fromJson(it, AppUser::class.java) }
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data.first()[stringPreferencesKey("accessToken")]
    }


    suspend fun clearUserData() {
        dataStore.edit { it.clear() }
    }
}
