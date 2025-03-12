package com.example.myapplication.data

/**
 * 로그인 요청 모델
 */
data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val token: String)

data class RegisterRequest(val username: String, val password: String, val email: String, val name: String, val phoneNumber: String, val profession: String, val selectedHospital: String)

data class RegisterResponse(val message: String)

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
