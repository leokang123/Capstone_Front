package com.example.myapplication.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * API 전용 싱글톤 객체
 * 사용할땐 그냥 이 객체 그대로 ApiClient.getInstance().create(원하는 서비스 클래스) 사용하면 됨
 * 예시) ApiClient.getInstance().create(ApiService::class.java)
 * 그럼 ApiService용 처리 객체가 된거라 그 안의 함수를 호출해 api요청 하면됨
 */
object ApiClient {
    private const val BASE_URL = "http://me-di-cap.kro.kr/"
//    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var retrofit: Retrofit? = null

    fun getInstance(context: Context): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context)) // ✅ 인증 Interceptor 추가
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL) // ✅ 서버 주소
                .addConverterFactory(GsonConverterFactory.create()) // ✅ JSON 변환
                .client(client)
                .build()
        }

        return retrofit!!
    }
}