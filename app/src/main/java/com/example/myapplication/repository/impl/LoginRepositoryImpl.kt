package com.example.myapplication.repository.impl

import android.content.ContentValues.TAG
import android.util.Log
import com.example.myapplication.data.user.User
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
) :
    LoginRepository {

    /**
     * 로그인 API 호출 (POST 요청)
     */
    override suspend fun loginUser(user: User): User? {
        return try {
            val response = apiService.signIn(user)
            if (response.isSuccessful){
                Log.d("LOGIN_DEBUG", response.toString())
                val user = response.body()
                user
            } else {
                Log.e("LOGIN_ERROR", "응답 실패: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("LOGIN_ERROR", "API 요청 실패: ${e.message}", e) // 로그 추가
            null
        }
    }

    override suspend fun registerUser(user: User): String? {
        return try {
            val response = apiService.signUp(user) // API 호출
            val user = response.body()
            if (response.isSuccessful) {
                val successResponse = "${user?.uuid} 회원가입 성공" // 성공 메시지 반환
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

}
