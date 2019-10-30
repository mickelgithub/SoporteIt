package es.samiralkalii.myapps.soporteit.di

import androidx.lifecycle.LiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.usecase.authlogin.*
import org.koin.dsl.module


val useCaseModule= module {
    factory { CheckUserAuthUseCase(get(), get(), get()) }
    factory { LogupUseCase<LiveData<User>>(get(), get(), get(), get(), get()) }
    factory { LoginUserCase(get(), get(), get(), get(), get()) }
    factory { Compare2ImageProfileUseCase(get()) }
    factory { SaveProfileImageChangeUseCase(get(), get(), get(), get()) }
}