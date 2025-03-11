package com.example.myapplication.ui.screen

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import androidx.core.content.edit


@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val loginService = remember { createLoginService() }
    val scope = rememberCoroutineScope()

    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val savedToken = sharedPreferences.getString("auth_token", null)

    if (savedToken != null) {
        navController.navigate("home") { popUpTo("login") { inclusive = true } }  // ✅ 자동 로그인 방지
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    val token = loginUser(email, password, loginService, context)
                    if (token != null) {
                        navController.navigate("home") // ✅ 로그인 성공 시 홈 화면으로 이동
                    } else {
                        errorMessage = "Invalid email or password"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }

        TextButton(
            onClick = { navController.navigate("register") }, // ✅ 회원가입 페이지 이동
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account? Sign up")
        }
    }
}

/**
 * 로그인 API 호출 (POST 요청)
 */
suspend fun loginUser(email: String, password: String, service: LoginService, context: Context): String? {
    return try {
        val response = service.login(LoginRequest(email, password))
        saveToken(context, response.token)
        response.token

    } catch (e: Exception) {
        Log.e("LOGIN_ERROR", "API 요청 실패: ${e.message}", e) // ✅ 로그 추가
        null
    }
}

/**
 * SharedPreferences에 토큰 저장
 */
fun saveToken(context: Context, token: String) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit() { putString("auth_token", token) }
}

/**
 * Retrofit 서비스 생성
 */
fun createLoginService(): LoginService {
    return Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") // ✅ API Base URL 설정
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(LoginService::class.java)
}

/**
 * 로그인 API 인터페이스
 */
interface LoginService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}

/**
 * 로그인 요청 모델
 */
data class LoginRequest(val email: String, val password: String)

/**
 * 로그인 응답 모델
 */
data class LoginResponse(val token: String)

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}