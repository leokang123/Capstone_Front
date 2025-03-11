package com.example.myapplication.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferences.getString("auth_token", null)

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token") // ✅ 매 요청마다 토큰 추가
                .build()
        } else {
            chain.request()
        }
        println(request.toString())
        return chain.proceed(request)
    }
}