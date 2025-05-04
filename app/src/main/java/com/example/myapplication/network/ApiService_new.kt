package com.example.myapplication.network

import com.example.myapplication.data.user.Beacon
import com.example.myapplication.data.user.Hospital
import com.example.myapplication.data.user.User
import com.example.myapplication.data.waste.WasteItem
import com.example.myapplication.data.waste.WasteLog
import com.example.myapplication.data.waste.WasteStatus
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.data.waste.WasteType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 엔드포인트랑 사용할 api 정의하는부분
 * 아마 사용하는 api가 많아지면 리팩토링 해야할듯
 */

interface ApiService_new {

    @GET("beacon")
    suspend fun getAllBeacons(): Response<List<Beacon>>

    @GET("beacon/{id}")
    suspend fun getBeaconById(@Path("id") beaconId: Int): Response<Beacon>

    @GET("hospital")
    suspend fun getAllHospital(): Response<List<Hospital>>

    @GET("storage")
    suspend fun getAllStorage(): Response<List<WasteStorage>>

    @POST("auth/signin")
    suspend fun signIn(@Body user: User): Response<User>

    @POST("auth/signup")
    suspend fun signUp(@Body user: User): Response<User>

    @POST("waste/createWaste")
    suspend fun createWaste(@Body wasteItem: WasteItem): Response<WasteItem>

    @DELETE("waste/deleteWaste/{id}")
    suspend fun deleteWaste(@Path("id") wasteItemId: String): Response<*>

    @GET("waste/getAllWaste")
    suspend fun getAllWaste(): Response<List<WasteItem>>

    @GET("waste/getAllWasteHs")
    suspend fun getAllWasteHs(
        @Query("valid") valid: Boolean? = null,
        @Query("needUser") needUser: Boolean? = null,
        @Query("wasteId") wasteId: String? = null,
        @Query("beaconId") beaconId: Int? = null,
        @Query("wasteTypeId") wasteTypeId: Int? = null,
        @Query("wasteStatusId") wasteStatusId: Int? = null,
        @Query("storageId") storageId: Int? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<WasteItem>>

    @GET("waste")
    suspend fun getWaste(@Query("wasteId") wasteId: String): Response<WasteItem>

    @PUT("waste/updateWaste/{id}")
    suspend fun updateWaste(
        @Path("id") wasteId: String,
        @Body wasteItem: WasteItem
    ): Response<WasteItem>

    @GET("waste/log/{id}")
    suspend fun getWasteLog(@Path("id") wasteId: String): Response<List<WasteLog>>


    @GET("wsStatus")
    suspend fun getWasteStatus(): Response<List<WasteStatus>>

    @GET("wsType")
    suspend fun getWasteType(): Response<List<WasteType>>

}

