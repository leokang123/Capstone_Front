package com.example.myapplication.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel

/**
 * 예시 viewModel
 * reset함수는 데이터를 viewModel로 관리하니까 초기화 되면 더 좋은순간에
 * 데이터가 남아있는 상황이 생겨서 억지로 넣어놨는데, 더 좋은 방법있으면 수정해도 됨
 */
class DetailViewModel : ViewModel() {
    private val initNumber = 0
    private val _number = mutableIntStateOf(initNumber)
    val number: State<Int> = _number;

    fun updateNumber() {
        _number.intValue += 1;
    }

    fun reset() {
        _number.intValue = initNumber
    }
}