package es.samiralkalii.myapps.soporteit.di

import es.samiralkalii.myapps.usecase.authlogin.CheckUserAuthUseCase
import es.samiralkalii.myapps.usecase.authlogin.RegisterUseCase
import org.koin.dsl.module


val useCaseModule= module {
    factory { CheckUserAuthUseCase(get(), get()) }
    factory { RegisterUseCase(get(), get(), get(), get(), get()) }
}