package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entity.AppUser
import com.example.myapplication.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _user = MutableStateFlow<AppUser?>(null)
    val user: StateFlow<AppUser?> = _user

    init {
        viewModelScope.launch {
            _user.value = userDataStore.getUser()
        }
    }
}
