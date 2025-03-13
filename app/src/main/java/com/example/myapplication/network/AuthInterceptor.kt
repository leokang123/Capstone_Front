package com.example.myapplication.network

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.ui.component.UserDataStore
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 더이상 건들일 없을 코드 같긴한데
 * 그냥 ApiClient에서 Retrofit 객체 만들고 그러는 과정에 BearerToken 넣어주는 역할
 */

class AuthInterceptor(context: Context) : Interceptor {
    private val userDataStore = UserDataStore(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = userDataStore.getToken()

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token") // ✅ 매 요청마다 토큰 추가
                .build()
        } else {
            chain.request()
        }
        println(token)
        println(request.toString())
        return chain.proceed(request)
    }
}