package com.example.myapplication.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.entity.User
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.utils.CheckAuth
import com.example.myapplication.viewmodel.HomeViewModel


@Composable
fun ProfileEditScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {


    val context = LocalContext.current
    val toastFlow = viewModel.toastMessage

    val user by viewModel.user.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var authChecked by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) } // 필터 팝업 상태

    val focusManager = LocalFocusManager.current


    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isEmailValid by remember(email) {
        derivedStateOf {
            email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
        }
    }

    val isPhoneNumberValid by remember(phoneNumber) {
        derivedStateOf {
            Regex("""^\d{3}-\d{4}-\d{4}$""").matches(phoneNumber)
        }
    }
    CheckAuth(navController, role = Roles.USER) {
        authChecked = true
    }

    LaunchedEffect(authChecked) {
        if (!authChecked) return@LaunchedEffect
        viewModel.getUser()
        name = user?.name ?: "정보없음"
        email = user?.email ?: "정보없음"
        phoneNumber = user?.phoneNumber ?: "정보없음"
        viewModel.updateAppBarTitle("사용자 정보 수정")

        toastFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        }

    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        if (email.isNotEmpty() && !isEmailValid) {
            Text("유효한 이메일 주소를 입력하세요.", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("전화번호") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        if (phoneNumber.isNotEmpty() && !isPhoneNumberValid) {
            Text("유효한 휴대폰번호를 입력하세요. (XXX-XXXX-XXXX)", color = MaterialTheme.colorScheme.error)
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.33f)
                    .align(Alignment.TopEnd), // ✅ 올바른 사용
                enabled = isEmailValid && isPhoneNumberValid
            ) {
                Text("저장")
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("비밀번호 입력") },
            text = {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val tmpUser = User(
                        uuid = user?.uuid ?: "",
                        username = user?.username ?: "",
                        password = password,
                        email = email,
                        name = name,
                        phoneNumber = phoneNumber,
                        hospitalId = user?.hospital?.id,
                        roles = user?.roles,
                        primaryRole = user?.primaryRoles,
                    )
                    viewModel.updateProfile(tmpUser, user?.hospital)
                    showDialog = false
                    focusManager.clearFocus()
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

