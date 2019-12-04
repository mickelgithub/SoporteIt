package es.samiralkalii.myapps.soporteit.di

import es.samiralkalii.myapps.usecase.authlogin.*
import es.samiralkalii.myapps.usecase.messaging.NotifyMessagingUseCase
import es.samiralkalii.myapps.usecase.messaging.RegisterMessagingTokenUseCase
import es.samiralkalii.myapps.usecase.teammanagement.AddTeamUseCase
import org.koin.dsl.module


val useCaseModule= module {
    factory { CheckUserAuthUseCase(get(), get(), get()) }
    factory { LogupUseCase(get(), get(), get(), get(), get()) }
    factory { LoginUserCase(get(), get(), get(), get(), get()) }
    factory { Compare2ImageProfileUseCase(get()) }
    factory { SaveProfileChangeUseCase(get(), get(), get(), get()) }
    factory { RegisterMessagingTokenUseCase(get(), get()) }
    factory { NotifyMessagingUseCase(get(), get(), get()) }
    factory { AddTeamUseCase(get(), get(), get()) }
}