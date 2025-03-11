package com.example.myapplication.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private var retrofit: Retrofit? = null

    fun getInstance(context: Context): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context)) // ✅ 인증 Interceptor 추가
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // ✅ 서버 주소
                .addConverterFactory(GsonConverterFactory.create()) // ✅ JSON 변환
                .client(client)
                .build()
        }
        return retrofit!!
    }
}