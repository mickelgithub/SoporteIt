package es.samiralkalii.myapps.soporteit.di

import es.samiralkalii.myapps.usecase.authlogin.CheckUserAuthUseCase
import es.samiralkalii.myapps.usecase.authlogin.Compare2ImageProfileUseCase
import es.samiralkalii.myapps.usecase.authlogin.LoginUserCase
import es.samiralkalii.myapps.usecase.authlogin.LogupUseCase
import org.koin.dsl.module


val useCaseModule= module {
    factory { CheckUserAuthUseCase(get(), get(), get()) }
    factory { LogupUseCase(get(), get(), get(), get(), get()) }
    factory { LoginUserCase(get(), get(), get(), get(), get()) }
    factory { Compare2ImageProfileUseCase(get()) }
}