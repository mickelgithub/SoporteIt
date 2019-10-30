package es.samiralkalii.myapps.soporteit.di

import androidx.lifecycle.LiveData
import es.samiralkalii.myapps.data.authlogin.RemoteUserAuthRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserDatabaseRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.database.LocalUserDatabaseRepository
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.koin.dsl.module

val dataModule= module {

    factory { RemoteUserAuthRepository(get()) }
    factory { RemoteUserDatabaseRepository(get()) }
    factory { PreferenceRepository(get()) }
    factory { RemoteUserStorageRepository(get()) }
    factory { FileSystemRepository(get()) }
    factory { LocalUserDatabaseRepository<LiveData<User>>(get()) }
}