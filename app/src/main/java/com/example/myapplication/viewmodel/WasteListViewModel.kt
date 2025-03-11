package com.example.myapplication.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel

/**
 * 예시 viewmodel
 */
class WasteListViewModel : ViewModel() {
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