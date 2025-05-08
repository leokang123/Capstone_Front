package com.example.myapplication.di

import com.example.myapplication.repository.EtcRepository
import com.example.myapplication.repository.LoginRepository
import com.example.myapplication.repository.WasteRepository
import com.example.myapplication.repository.impl.EtcRepositoryImpl
import com.example.myapplication.repository.impl.LoginRepositoryImpl
import com.example.myapplication.repository.impl.WasteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


// 인터페이스는 @Inject를 사용해도 자동주입이 안돼서
// 구현체 클래스랑 인터페이스를 바인딩 시켜주는 주입 클래스가 필요하다
// 레포지토리를 인터페이스로 만들경우 레포지토리 구현체랑 바인딩 해줘야
// HILT 의존성 주입이 가능하다
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindLoginRepository(
        impl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    abstract fun bindWasteRepository(
        impl: WasteRepositoryImpl
    ): WasteRepository

    @Binds
    abstract fun bindEtcRepository(
        impl: EtcRepositoryImpl
    ): EtcRepository

}