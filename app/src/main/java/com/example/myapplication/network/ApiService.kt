package com.example.myapplication.network

import com.example.myapplication.data.LoginRequest
import com.example.myapplication.data.LoginResponse
import com.example.myapplication.data.RegisterRequest
import com.example.myapplication.data.RegisterResponse
import com.example.myapplication.data.WasteItemRequest
import com.example.myapplication.data.WasteItemResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * 엔드포인트랑 사용할 api 정의하는부분
 * 아마 사용하는 api가 많아지면 리팩토링 해야할듯
 */

interface ApiService {
    @POST("auth/signin")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    // 폐기물 리스트 조회 / 일단 선택 조회는 나중에 구현
    @GET("waste/get_list")
    suspend fun getWasteItemList() : List<WasteItemResponse>

    @POST("waste/register")
    suspend fun registerWasteItem(@Body request: WasteItemRequest) : RegisterResponse



}

