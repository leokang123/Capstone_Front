package com.example.myapplication.network

import android.content.Context
import android.util.Log
import com.example.myapplication.utils.AuthEventBus
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * 더이상 건들일 없을 코드 같긴한데
 * 그냥 ApiClient에서 Retrofit 객체 만들고 그러는 과정에 BearerToken 넣어주는 역할
 */

class AuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val accessToken = runBlocking { userDataStore.getAccessToken() }

        if (!accessToken.isNullOrBlank()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }
        Log.d("REQUEST", request.toString())

        val response = chain.proceed(request)
        // accessToken 만료 등으로 401 Unauthorized 반환 시
        if (response.code == 401 || response.code == 403) {
//            response.close() // 리소스 반환
            // UI 이벤트 발생
            CoroutineScope(Dispatchers.IO).launch {
                userDataStore.clearUserData()
                AuthEventBus.notifyLoginRequired()
            }
        }
        return response
    }

    // 실제로 토큰 재발급 API를 호출하는 함수
//    private fun refreshAccessToken(refreshToken: String): String? {
//        return try {
//            val client = OkHttpClient()
//            val request = Request.Builder()
//                .url("${baseUrl}/auth/refresh") // 재발급 API 주소
//                .addHeader("Authorization", "Bearer $refreshToken")
//                .post("".toRequestBody(null)) // body 필요 없으면 빈 값
//                .build()
//
//            val response = client.newCall(request).execute()
//
//            if (response.isSuccessful) {
//                val json = JSONObject(response.body?.string() ?: "")
//                json.getString("accessToken")
//            } else {
//                Log.d("1111", "Refresh 토큰 만료, 재 로그인 바람")
//                Handler(Looper.getMainLooper()).post {
//                    Toast.makeText(context, "Refresh 토큰 만료, 재 로그인 바람", Toast.LENGTH_LONG).show()
//                }
//                null
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("2222", "Refresh 토큰 만료, 재 로그인 바람")
//            Handler(Looper.getMainLooper()).post {
//                Toast.makeText(context, "Refresh 토큰 만료, 재 로그인 바람", Toast.LENGTH_LONG).show()
//            }
//            null
//        }
//    }
}
