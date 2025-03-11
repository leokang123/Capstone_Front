package com.example.myapplication.ui.screen

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var selectedHospital by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }

    val hospitals = listOf("서울병원", "강남병원", "부산병원", "광주병원")

    val isPasswordValid by remember(password) {
        derivedStateOf {
            password.length >= 8 && password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() }
        }
    }

    val isPasswordMatch by remember(password, confirmPassword) {
        derivedStateOf { password == confirmPassword }
    }
    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    val isEmailValid by remember(email) {
        derivedStateOf { email.matches(emailPattern) }
    }

    val isFormValid by remember(username, password, selectedHospital) {
        derivedStateOf { username.isNotBlank() && password.isNotBlank() && selectedHospital.isNotBlank() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("UserName *") },
            modifier = Modifier.fillMaxWidth()
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

        if (!isPasswordValid) {
            Text("비밀번호는 8자 이상, 숫자 및 특수문자를 포함해야 합니다.", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password *") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (!isPasswordMatch) {
            Text("비밀번호가 일치하지 않습니다.", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        if (email.isNotEmpty() && !isEmailValid) {
            Text("유효한 이메일 주소를 입력하세요.", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = profession,
            onValueChange = { profession = it },
            label = { Text("Profession") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 병원 선택 Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedHospital,
                onValueChange = {},
                label = { Text("Select Hospital *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDropdown = true }) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Dropdown")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false }
            ) {
                hospitals.forEach { hospital ->
                    DropdownMenuItem(
                        text = { Text(hospital) },
                        onClick = {
                            selectedHospital = hospital
                            showDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                /* 회원가입 로직 추가 */
                // 일단 예시 로그 (서버로 전송할 데이터)
                Log.d(TAG, "회원가입 정보:")
                Log.d(TAG, "아이디: $username")
                Log.d(TAG, "비밀번호: $password")
                Log.d(TAG, "비밀번호 확인: $confirmPassword")
                Log.d(TAG, "이메일: $email")
                Log.d(TAG, "전화번호: $phoneNumber")
                Log.d(TAG, "이름: $name")
                Log.d(TAG, "직업: $profession")
                Log.d(TAG, "선택한 병원: $selectedHospital")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid && isPasswordValid && isPasswordMatch
        ) {
            Text("Sign Up")
        }

        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already have an account? Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}
