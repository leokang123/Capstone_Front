package com.example.myapplication.data

import java.io.Serializable
import java.time.LocalDate

/**
 * 로그인 요청 모델
 */
data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val user: User, val token: String)

data class RegisterRequest(val username: String, val password: String, val email: String, val name: String, val phoneNumber: String, val profession: String, val selectedHospital: String)

data class RegisterResponse(val message: String)

// 더 생길 수 있음 (고급 검색 기능)
// ✅ 폐기물 등록 요청 DTO (서버에 ID, status를 보낼 필요 없음)
data class WasteItemRequest(
    val registrantName: String,
    val wasteType: String,
    val wasteDetails: String?,
    val location: String,
    val selectedDate: String,
    val selectedDevice: String?
)

// ✅ 폐기물 응답 DTO (서버에서 ID, status도 함께 반환)
data class WasteItemResponse(
    val id: Long,  // 서버에서 생성된 ID
    val registrantName: String,
    val wasteType: String,
    val wasteDetails: String?,
    val location: String,
    val selectedDate: String,
    val selectedDevice: String?,
    val status: String // "수집", "이동", "저장", "배출"
)

data class User(
    val id: Long? = null,
    val userName: String,
    val password: String,
    val email: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val profession: String? = null,
    val selectedHospital: String? = null
): Serializable