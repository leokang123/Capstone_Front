package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entity.AppUser
import com.example.myapplication.data.entity.Hospital
import com.example.myapplication.data.entity.User
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 초안을 홈화면에 뭔가 넣지 말자고 해서 따로 쓸일이 없을거같긴 하지만
 * 일단 만들어만 놨음
 */

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _user = MutableStateFlow<AppUser?>(null)
    val user: StateFlow<AppUser?> = _user
    private val _appBarTitle = MutableStateFlow("폐기수첩")
    val appBarTitle: StateFlow<String> = _appBarTitle

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage

    init {
        viewModelScope.launch {
            getUser()
            userDataStore.appBarTitleFlow.collect { storedTitle ->
                _appBarTitle.value = storedTitle
            }
        }
    }

    suspend fun getUser() {
        _user.value = userDataStore.getUser()
    }


    fun updateAppBarTitle(title: String) {
        _appBarTitle.value = title
        viewModelScope.launch {
            userDataStore.saveAppBarTitle(title)
        }
    }

    fun updateProfile(user: User, hospital: Hospital?) {
        viewModelScope.launch {
            try {
                loginRepository.updateUser(user)
                userDataStore.saveUser(user, hospital)
                emitToast("프로필수정이 완료되었습니다")
            } catch (e: Exception) {
                Log.e("UPDATE", "Update error", e)
                emitToast("에러발생: ${e.message}")

            }

        }

    }

    fun emitToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}
