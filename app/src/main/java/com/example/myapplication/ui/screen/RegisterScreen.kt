package com.example.myapplication.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.viewmodel.RegisterViewModel

// RegisterScreen.kt

@Composable
fun RegisterScreen(
    navController: NavController,
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val hospitalList by registerViewModel.hospitalList.collectAsState()

    val username = registerViewModel.username
    val password = registerViewModel.password
    val confirmPassword = registerViewModel.confirmPassword
    val email = registerViewModel.email
    val phoneNumber = registerViewModel.phoneNumber
    val name = registerViewModel.name
    val selectedHospital = registerViewModel.selectedHospital

    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }
    val showHospitalDropdown = remember { mutableStateOf(false) }

    val isPasswordValid = registerViewModel.isPasswordValid()
    val isPasswordMatch = registerViewModel.isPasswordMatch()
    val isEmailValid = registerViewModel.isEmailValid()
    val isPhoneValid = registerViewModel.isPhoneNumberValid()
    val isFormValid = registerViewModel.isFormValid()

// 병원 검색용 상태
    var searchHospitalQuery by remember { mutableStateOf("") }

// 병원 리스트 필터링
    val filteredHospitalList = hospitalList.filter {
        it.hospitalName.contains(searchHospitalQuery, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        registerViewModel.toastMessage.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // 스크롤 가능하게
            .padding(16.dp)
            .imePadding() // 키보드에 안 가리도록
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("UserName *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password *") },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(
                        imageVector = if (passwordVisible.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
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
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            label = { Text("Confirm Password *") },
            visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    confirmPasswordVisible.value = !confirmPasswordVisible.value
                }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
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
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        if (email.value.isNotEmpty() && !isEmailValid) {
            Text("유효한 이메일 주소를 입력하세요.", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        if (phoneNumber.value.isNotEmpty() && !isPhoneValid) {
            Text("유효한 휴대폰번호를 입력하세요. (XXX-XXXX-XXXX)", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 병원 Dropdown

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedHospital.value?.hospitalName ?: "",
                onValueChange = {},
                label = { Text("Select Hospital *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showHospitalDropdown.value = true }) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Dropdown")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = showHospitalDropdown.value,
                onDismissRequest = {
                    showHospitalDropdown.value = false
                    searchHospitalQuery = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                // 검색창 추가
                OutlinedTextField(
                    value = searchHospitalQuery,
                    onValueChange = { searchHospitalQuery = it },
                    label = { Text("병원 검색") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                // 필터링된 병원 목록
                if (filteredHospitalList.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("검색 결과 없음") },
                        onClick = {},
                        enabled = false
                    )
                } else {
                    filteredHospitalList.forEach { hospital ->
                        DropdownMenuItem(
                            text = { Text(hospital.hospitalName) },
                            onClick = {
                                selectedHospital.value = hospital
                                showHospitalDropdown.value = false
                                searchHospitalQuery = ""
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                registerViewModel.register {
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
                    && isPasswordValid
                    && isPasswordMatch
                    && isPhoneValid
                    && isEmailValid
                    && name.value.isNotBlank()
                    && selectedHospital.value != null
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
