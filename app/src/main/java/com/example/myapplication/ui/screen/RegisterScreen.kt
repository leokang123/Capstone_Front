package com.example.myapplication.ui.screen

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.auth.RegisterRequest
import com.example.myapplication.data.user.Hospital
import com.example.myapplication.data.user.Role
import com.example.myapplication.data.waste.WasteStorage
import com.example.myapplication.repository.LoginRepository
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedHospital by remember { mutableStateOf<Hospital?>(null) }
    var selectedRoleName by remember { mutableStateOf("") }

    var selectedRoleId by remember { mutableLongStateOf(1L) }

    val scope = rememberCoroutineScope()
    val context: Context = LocalContext.current
    val loginRepository = LoginRepository(context)

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showHospitalDropdown by remember { mutableStateOf(false) }
    var showRoleDropdown by remember { mutableStateOf(false) }
    var hospitalList by remember { mutableStateOf<List<Hospital>>(emptyList()) }

    val mockHospitalList = listOf(
        Hospital(id = 1, hospitalName = "서울병원"),
        Hospital(id = 2, hospitalName = "강남병원"),
        Hospital(id = 3, hospitalName = "구로병원"),
        Hospital(id = 4, hospitalName = "성모병원")
        )
    val roles = listOf(
        Role(id = 1, roleName = "일반 사용자"),
        Role(id = 2, roleName = "중간 관리직"),
        Role(id = 3, roleName = "최종 관리직")
    )
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
        derivedStateOf { username.isNotBlank() && password.isNotBlank() && selectedHospital != null }
    }
    LaunchedEffect(Unit) {
        try {
            val hosList = loginRepository.getHospitalList()
            hospitalList = hosList.takeIf { !it.isNullOrEmpty() } ?: mockHospitalList

        } catch (e: Exception) {
            Log.e("RegisterScreen", e.message.toString())
            Toast.makeText(context, "병원 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
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
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
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

        // 병원 선택 Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedRoleName,
                onValueChange = {},
                label = { Text("Select Role *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showRoleDropdown = true }) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Dropdown")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = showRoleDropdown,
                onDismissRequest = { showRoleDropdown = false }
            ) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.roleName) },
                        onClick = {
                            selectedRoleName = role.roleName
                            selectedRoleId = role.id
                            showRoleDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 병원 선택 Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedHospital?.hospitalName ?: "",
                onValueChange = {},
                label = { Text("Select Hospital *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showHospitalDropdown = true }) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Dropdown")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = showHospitalDropdown,
                onDismissRequest = { showHospitalDropdown = false }
            ) {
                hospitalList.forEach { hospital ->
                    DropdownMenuItem(
                        text = { Text(hospital.hospitalName) },
                        onClick = {
                            selectedHospital = hospital
                            showHospitalDropdown = false
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
                Log.d(TAG, "선택한 병원: $selectedHospital")
                val registerRequest = RegisterRequest(
                    username = username.trim(),
                    password = password,
                    email = email.trim(),
                    phoneNumber = phoneNumber.trim(),
                    name = name.trim(),
                    selectedHospitalId = selectedHospital?.id!!,
                    roleId = selectedRoleId,
                )
                scope.launch {
                    try {
                        val response = loginRepository.registerUser(registerRequest)
                        Toast.makeText(context,response,Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    } finally {
                        navController.popBackStack()
                    }
                }

            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
                    && isPasswordValid
                    && isPasswordMatch
                    && name.isNotBlank()
                    && selectedHospital != null
                    && selectedRoleName.isNotBlank()
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
