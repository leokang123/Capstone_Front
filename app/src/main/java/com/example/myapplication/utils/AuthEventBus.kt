package com.example.myapplication.utils

import kotlinx.coroutines.flow.MutableSharedFlow

object AuthEventBus {
    val needLoginFlow = MutableSharedFlow<Unit>(replay = 0)

    suspend fun notifyLoginRequired() {
        needLoginFlow.emit(Unit)
    }
}
