package es.samiralkalii.myapps.soporteit.di

import es.samiralkalii.myapps.data.authlogin.UserAccessRepository
import es.samiralkalii.myapps.data.authlogin.UserDatabaseRepository
import es.samiralkalii.myapps.data.authlogin.UserStorageRepository
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.koin.dsl.module

val dataModule= module {

    factory { UserAccessRepository(get()) }
    factory { UserDatabaseRepository(get()) }
    factory { PreferenceRepository(get()) }
    factory { UserStorageRepository(get()) }
    factory { FileSystemRepository(get()) }
}