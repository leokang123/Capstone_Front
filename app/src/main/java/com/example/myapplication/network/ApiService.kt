package com.example.myapplication.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // 폐기물 리스트 조회 (창 라우팅시, 검색중일시) (검색 결과에 따라 반환
    @POST("api/trash_list")
    suspend fun getTrashList(@Body request: TrashListRequest) : List<TrashListResponse>


}

/**
 * 로그인 요청 모델
 */
data class LoginRequest(val email: String, val password: String)

data class LoginResponse(val token: String)

// 더 생길 수 있음 (고급 검색 기능)
data class TrashListRequest(val trashId: Int, val trashName: String,)

data class TrashListResponse(val trashId: Int, val trashName: String, val trashDetails: String)

