package com.example.myapplication.network

import com.example.myapplication.data.auth.LoginRequest
import com.example.myapplication.data.auth.LoginResponse
import com.example.myapplication.data.auth.RegisterRequest
import com.example.myapplication.data.auth.RegisterResponse
import com.example.myapplication.data.user.Beacon
import com.example.myapplication.data.user.Hospital
import com.example.myapplication.data.waste.MoveRequests
import com.example.myapplication.data.waste.SearchRequest
import com.example.myapplication.data.waste.WasteItemDetailResponse
import com.example.myapplication.data.waste.WasteItemRequest
import com.example.myapplication.data.waste.WasteItemResponse
import com.example.myapplication.data.waste.WasteStorage
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * 엔드포인트랑 사용할 api 정의하는부분
 * 아마 사용하는 api가 많아지면 리팩토링 해야할듯
 */

interface ApiService_old {
    @POST("auth/signin")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    // 폐기물 리스트 조회 / 일단 선택 조회는 나중에 구현
    @GET("waste/get_wastelist")
    suspend fun getWasteItemList(): List<WasteItemResponse>

    @POST("waste/register")
    suspend fun registerWasteItem(@Body request: WasteItemRequest): RegisterResponse

    @POST("waste/get_wastelist_by_name")
    suspend fun getWasteItemListByName(@Body wasteType: SearchRequest): List<WasteItemResponse>

    @POST("waste/get_filtered_wastelist")
    suspend fun searchWasteItems(@Body searchRequest: SearchRequest): List<WasteItemResponse>

    @POST("waste/waste_items_next_step")
    suspend fun moveWasteItems(@Body moveRequests: MoveRequests)

    @GET("waste/get_detail_waste_item")
    suspend fun getDetailWasteItem(@Query("itemId") itemId: Long): WasteItemDetailResponse

    @GET("waste/check_item_status")
    suspend fun checkItemStatus(@Query("itemId") itemId: Long): Boolean

    @DELETE("waste/delete_item")
    suspend fun deleteItem(@Query("itemId") itemId: Long): Boolean

    @PUT("waste/update_item")
    suspend fun updateItem(@Body wasteItem: WasteItemDetailResponse)

    @GET("setting/get_storage_list")
    suspend fun getStorageList(): List<WasteStorage>

    @GET("setting/get_hospital_list")
    suspend fun getHospitalList(): List<Hospital>

    @GET("setting/get_beacon_list")
    suspend fun getBeaconList(): List<Beacon>

}

