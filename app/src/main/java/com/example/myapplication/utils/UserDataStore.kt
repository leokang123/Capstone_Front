package com.example.myapplication.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.entity.AppUser
import com.example.myapplication.data.entity.Beacon
import com.example.myapplication.data.entity.Hospital
import com.example.myapplication.data.entity.User
import com.example.myapplication.data.waste.WasteStatus
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.data.waste.WasteType
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = context.userDataStore

    private val booleanPreferencesKey = booleanPreferencesKey("hasSentFCM")
    private val darkThemeKey = booleanPreferencesKey("dark_theme_enabled")
    val appBarTitle = stringPreferencesKey("app_bar_title")
    private val notificationCountKey = intPreferencesKey("notification_count")

    private val gson = Gson()

    // 알림 갯수 저장
    suspend fun setLastReadNotificationCount(count: Int) {
        dataStore.edit { prefs ->
            prefs[notificationCountKey] = count
        }
    }

    // 알림 갯수 불러오기 (Flow)
    val lastReadNotificationCountFlow: Flow<Int> = dataStore.data
        .map { prefs -> prefs[notificationCountKey] ?: 0 }


    suspend fun saveUser(user: User, hospital: Hospital?) {
        val appUser = AppUser(
            uuid = user.uuid,
            username = user.username,
            email = user.email,
            name = user.name,
            phoneNumber = user.phoneNumber,
            hospital = hospital,
            roles = user.roles,
            primaryRoles = user.primaryRole,
            token = user.token?: getAccessToken(),
            fcmToken = user.fcmToken

        )
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("user")] = gson.toJson(appUser)
            prefs[stringPreferencesKey("accessToken")] = appUser.token!!
        }
        Log.d("USER", user.toString())
    }

    suspend fun saveHospitalList(hospitalList: List<Hospital>) {
        val json = gson.toJson(hospitalList)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("hospitalList")] = json
        }
    }

    suspend fun saveStorageList(storageList: List<WasteStorage>) {
        val json = gson.toJson(storageList)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("wasteStorageList")] = json
        }
    }
    suspend fun saveWasteTypeList(wasteTypeList: List<WasteType>) {
        val json = gson.toJson(wasteTypeList)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("wasteTypeList")] = json
        }
    }
    suspend fun saveWasteStatusList(wasteStatusList: List<WasteStatus>) {
        val json = gson.toJson(wasteStatusList)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("wasteStatusList")] = json
        }
    }
    suspend fun saveBeaconList(beaconList: List<Beacon>) {
        val json = gson.toJson(beaconList)
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey("beaconList")] = json
        }
    }

    suspend fun saveHasSentFCM() {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey] = true
        }
    }

    suspend fun saveDarkThemeEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[darkThemeKey] = enabled
        }
    }


    suspend fun saveAppBarTitle(title: String) {
        dataStore.edit { prefs ->
            prefs[appBarTitle] = title
        }
    }

    val appBarTitleFlow: Flow<String> = dataStore.data
        .map { prefs -> prefs[appBarTitle] ?: "폐기수첩" }


    val themeFlow: Flow<Boolean> = context.userDataStore.data
        .map { preferences ->
            preferences[darkThemeKey] ?: false
        }

    suspend fun saveHasNotification(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey("has_notification")] = value
        }
    }

    val hasNotification: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[booleanPreferencesKey("has_notification")] == true }

    suspend fun getHasSentFCM(): Boolean? {
        return dataStore.data.first()[booleanPreferencesKey]
    }

    suspend fun getUser(): AppUser? {
        val userJson = dataStore.data.first()[stringPreferencesKey("user")]
        return userJson?.let { gson.fromJson(it, AppUser::class.java) }
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data.first()[stringPreferencesKey("accessToken")]
    }
    suspend fun getHospitalList(): List<Hospital> {
        val json = dataStore.data.first()[stringPreferencesKey("hospitalList")]
        return if (json != null) {
            try {
                gson.fromJson(json, Array<Hospital>::class.java).toList()
            } catch (e: Exception) {
                Log.e("UserDataStore", "병원 리스트 파싱 실패: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    suspend fun getWasteTypeList(): List<WasteType> {
        val json = dataStore.data.first()[stringPreferencesKey("wasteTypeList")]
        return if (json != null) {
            try {
                gson.fromJson(json, Array<WasteType>::class.java).toList()
            } catch (e: Exception) {
                Log.e("UserDataStore", "WasteType 파싱 실패: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    suspend fun getWasteStatusList(): List<WasteStatus> {
        val json = dataStore.data.first()[stringPreferencesKey("wasteStatusList")]
        return if (json != null) {
            try {
                gson.fromJson(json, Array<WasteStatus>::class.java).toList()
            } catch (e: Exception) {
                Log.e("UserDataStore", "WasteStatus 파싱 실패: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    suspend fun getWasteStorageList(): List<WasteStorage> {
        val json = dataStore.data.first()[stringPreferencesKey("wasteStorageList")]
        return if (json != null) {
            try {
                gson.fromJson(json, Array<WasteStorage>::class.java).toList()
            } catch (e: Exception) {
                Log.e("UserDataStore", "WasteStorage 파싱 실패: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    suspend fun getBeaconList(): List<Beacon> {
        val json = dataStore.data.first()[stringPreferencesKey("beaconList")]
        return if (json != null) {
            try {
                gson.fromJson(json, Array<Beacon>::class.java).toList()
            } catch (e: Exception) {
                Log.e("UserDataStore", "BeaconList 파싱 실패: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
    }




    suspend fun clearUserData() {
        dataStore.edit { it.clear() }
    }
}
