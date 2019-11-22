package es.samiralkalii.myapps.soporteit.di

import es.samiralkalii.myapps.usecase.authlogin.*
import es.samiralkalii.myapps.usecase.messaging.RegisterMessagingTokenUseCase
import messaging.NotifyMessagingUseCase
import org.koin.dsl.module


val useCaseModule= module {
    factory { CheckUserAuthUseCase(get(), get(), get()) }
    factory { LogupUseCase(get(), get(), get(), get(), get()) }
    factory { LoginUserCase(get(), get(), get(), get(), get()) }
    factory { Compare2ImageProfileUseCase(get()) }
    factory { SaveProfileImageChangeUseCase(get(), get(), get(), get()) }
    factory { RegisterMessagingTokenUseCase(get(), get()) }
    factory { NotifyMessagingUseCase(get(), get(), get()) }
}