package com.example.myapplication.repository.impl

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.myapplication.data.auth.LoginRequest
import com.example.myapplication.data.auth.LoginResponse
import com.example.myapplication.data.auth.RegisterRequest
import com.example.myapplication.data.user.Hospital
import com.example.myapplication.network.ApiService
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.utils.UserDataStore
import org.json.JSONObject
import javax.inject.Inject

/**
 * 로그인 관련 처리 클래스
 */
class LoginRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDataStore: UserDataStore
) :
    LoginRepository {

    /**
     * 로그인 API 호출 (POST 요청)
     */
    override suspend fun loginUser(loginRequest: LoginRequest): LoginResponse? {
        return try {
            val response: LoginResponse = apiService.login(loginRequest)
            userDataStore.saveUser(response.user, response.accessToken, response.refreshToken)
            response

        } catch (e: Exception) {
            Log.e("LOGIN_ERROR", "API 요청 실패: ${e.message}", e) // 로그 추가
            null
        }
    }

    override suspend fun registerUser(registerRequest: RegisterRequest): String? {
        return try {
            val response = apiService.register(registerRequest) // API 호출

            if (response.isSuccessful) {
                val successResponse = response.body()?.message ?: "회원가입 성공" // 성공 메시지 반환
                successResponse

            } else {
                val errorBody = response.errorBody()?.string() // 에러 본문을 문자열로 변환
                val errorMessage = errorBody?.let {
                    try {
                        JSONObject(it).getString("message") // JSON에서 "message" 값 추출
                    } catch (e: Exception) {
                        Log.e(TAG, e.message.toString())
                        "알 수 없는 오류 발생" // 파싱 실패 시 기본 메시지
                    }
                }
                throw Exception(errorMessage)
            }

        } catch (e: Exception) {
            Log.e("REGISTER_ERROR", "API 요청 실패: ${e.message}", e) // 네트워크 에러 로그
            "네트워크 오류 발생: ${e.message}" // 기본 에러 메시지 반환
            throw Exception(e.message)
        }
    }

    override suspend fun getHospitalList(): List<Hospital> {
        return try {
            apiService.getHospitalList() // API 호출
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}
