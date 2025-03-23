package com.example.myapplication.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.auth.LoginRequest
import com.example.myapplication.data.auth.LoginResponse
import com.example.myapplication.data.user.User
import kotlinx.coroutines.launch
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.utils.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * 로그인 화면
 * 백엔드랑 통신까지 완료
 * 디자인이나 토큰 관리하는거 바꾸는거 아니면 건들일 없을듯
 */

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val loginRepository = LoginRepository(context)
    val scope = rememberCoroutineScope()

    val userDataStore = UserDataStore(context)

    LaunchedEffect(Unit) {
        val token = userDataStore.getAccessToken()
        if (token != null) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(), // 키보드가 올라오면 자동으로 공간 확보
        verticalArrangement = Arrangement.Center // 중앙 정렬

    ) {
        Text("애버커스", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("UserName") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // password가 아니면 이상하게 키보드가 안뜸
            modifier = Modifier.fillMaxWidth()// 포커스 가능하도록 설정

        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password *") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    // 서버 돌아갈시
                    val loginRequest = LoginRequest(username.trim(), password)
                    val response: LoginResponse? = loginRepository.loginUser(loginRequest)
                    if (response != null) {
                        navController.navigate("home") // 로그인 성공 시 홈 화면으로 이동
                        Toast.makeText(context, "Login Succeed", Toast.LENGTH_SHORT).show()
                    } else {
                        errorMessage = "Invalid username or password"

                        // 서버없이 테스트
                        val user = User(userName = "test", password = "test", name = "test")
                        CoroutineScope(Dispatchers.IO).launch {
                            userDataStore.saveUser(user = user, accessToken = "token123", refreshToken = "token123")
                        }
                        navController.navigate("home")
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
            onClick = { navController.navigate("register") }, // 회원가입 페이지 이동
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account? Sign up")
        }
    }
}





@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}