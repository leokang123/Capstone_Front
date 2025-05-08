package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.network.ApiService
import com.example.myapplication.network.AuthInterceptor
import com.example.myapplication.repository.EtcRepository
import com.example.myapplication.repository.impl.MasterDataRepository
import com.example.myapplication.utils.UserDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideUserDataStore(
        @ApplicationContext context: Context
    ): UserDataStore {
        return UserDataStore(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        @ApplicationContext context: Context,
        userDataStore: UserDataStore
    ): AuthInterceptor {
        return AuthInterceptor(context, userDataStore)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // 인증 Interceptor 추가
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // 서버 주소
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}