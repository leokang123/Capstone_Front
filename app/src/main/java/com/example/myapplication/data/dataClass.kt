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
// 폐기물 등록 요청 DTO (서버에 ID, status를 보낼 필요 없음)
data class WasteItemRequest(
    val userId: Long,
    val wasteType: String,
    val wasteDetails: String?,
    val location: String,
    val selectedDate: String,
    val selectedDevice: String?,
    val storageId: Long?
)

// 폐기물 응답 DTO (서버에서 ID, status도 함께 반환)
data class WasteItemResponse(
    val id: Long,  // 서버에서 생성된 ID
    val userId: Long,  // 서버에서 생성된 ID
    val registrantName: String,
    val wasteType: String,
    val wasteDetails: String?,
    val location: String,
    val selectedDate: String,
    val selectedDevice: String?,
    val status: String, // "수집", "이동", "저장", "배출"
    val storageId: Long,
    val storageName: String
)
data class WasteItemDetailResponse(
    val id: Long,
    val wasteType: String,
    val location: String,
    val registrantName: String,
    val selectedDate: String,
    val selectedDevice: String?,
    val status: String,
    val wasteStorage: WasteStorage,
    val wasteDetails: List<WasteDetailResponse> // ✅ 상세 정보 리스트 포함
)

data class SearchRequest(
    val itemId: Long = 0,
    val wasteType: String? = "",
    val registrantName: String? = "",
    val selectedDate: String? = "",
    val selectedDevice: String? = ""
)


data class User(
    val id: Long = 0,
    val userName: String,
    val password: String,
    val email: String? = null,
    val name: String = "",
    val phoneNumber: String? = null,
    val profession: String? = null,
    val selectedHospital: String? = null
): Serializable

data class SafeUser(
    val id: Long = 0,
    val email: String? = null,
    val name: String = "",
    val phoneNumber: String? = null,
    val profession: String? = null,
    val selectedHospital: String? = null
)

data class WasteStorage(
    val id: Long? = null,
    val storageName: String? = null
)

data class MoveRequest(
    val itemId: Long,
    val userId: Long,
    val wasteDetails: String,
    val date: String
)

data class WasteDetailResponse(
    val wasteDetails: String,
    val date: String,
    val status: String,
    val user:  User
)