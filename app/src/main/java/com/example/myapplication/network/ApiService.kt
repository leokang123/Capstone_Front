package com.example.myapplication.network

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

    // 폐기물 리스트 조회 (창 라우팅시, 검색중일시) (검색 결과에 따라 반환
    @POST("api/trash_list")
    suspend fun getTrashList(@Body request: TrashListRequest) : List<TrashListResponse>


}

/**
 * 로그인 요청 모델
 */
data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val token: String)

// 더 생길 수 있음 (고급 검색 기능)
data class TrashListRequest(
    val registrantName: String,  // 등록자 이름
    val wasteType: String,        // 폐기물 종류
    val occurrenceDate: String,   // 발생일자
    val bluetoothAddress: String, // 블루투스 주소
    val location: String          // 발생장소
)

data class TrashListResponse(
    val trashId: Int,  // 폐기물 ID
    val registrantName: String,  // 등록자 이름
    val wasteType: String,        // 폐기물 종류
    val occurrenceDate: String,   // 발생일자
    val bluetoothAddress: String, // 블루투스 주소
    val location: String          // 발생장소
)
