package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.enums.Roles
import com.example.myapplication.data.user.Hospital
import com.example.myapplication.data.user.Role
import com.example.myapplication.data.user.User
import com.example.myapplication.repository.EtcRepository
import com.example.myapplication.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val  etcRepository: EtcRepository
) : ViewModel() {

    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val confirmPassword = mutableStateOf("")
    val email = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val name = mutableStateOf("")

    val selectedHospital = mutableStateOf<Hospital?>(null)
    val selectedRoles = mutableStateListOf<Roles>() // 체크박스로 여러 개 선택
    val selectedPrimaryRole = mutableStateOf<Roles?>(null) // 하나만 선택 (Radio 등)

    val hospitalList = MutableStateFlow<List<Hospital>>(emptyList())

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    val roles = listOf(
        Role(id = 1, roleName = "일반 사용자"),
        Role(id = 2, roleName = "중간 관리직"),
        Role(id = 3, roleName = "최종 관리직")
    )

    init {
        viewModelScope.launch {
            try {
                val hosList = etcRepository.getHospitalList()
                hospitalList.value = hosList?.ifEmpty { mockList() } ?: mockList()
            } catch (e: Exception) {
                hospitalList.value = mockList()
                _toastMessage.emit("병원 목록을 불러오는데 실패했습니다.")
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val request = User(
            userName = username.value.trim(),
            password = password.value,
            email = email.value.trim(),
            phoneNumber = phoneNumber.value.trim(),
            name = name.value.trim(),
            hospitalId = selectedHospital.value?.id ?: 0,
            roles = selectedRoles.toList(),
            primaryRoles = selectedPrimaryRole.value
        )
        viewModelScope.launch {
            try {
                val result = loginRepository.registerUser(request)
                _toastMessage.emit(result.toString())
                onSuccess()
            } catch (e: Exception) {
                _toastMessage.emit("회원가입 실패: ${e.message}")
            }
        }
    }

    fun isPasswordValid(): Boolean {
        val pw = password.value
        return pw.length >= 8 && pw.any { it.isDigit() } && pw.any { !it.isLetterOrDigit() }
    }

    fun isPasswordMatch(): Boolean = password.value == confirmPassword.value

    fun isEmailValid(): Boolean = email.value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))

    fun isFormValid(): Boolean =
        username.value.isNotBlank() && password.value.isNotBlank() && selectedHospital.value != null

    private fun mockList(): List<Hospital> = listOf(
        Hospital(id = 1, hospitalName = "서울병원"),
        Hospital(id = 2, hospitalName = "강남병원"),
        Hospital(id = 3, hospitalName = "구로병원"),
        Hospital(id = 4, hospitalName = "성모병원")
    )
}
