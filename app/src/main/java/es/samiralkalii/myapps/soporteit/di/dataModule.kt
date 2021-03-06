package es.samiralkalii.myapps.soporteit.di

import es.samiralkalii.myapps.data.authlogin.RemoteUserAuthRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserRepository
import es.samiralkalii.myapps.data.authlogin.RemoteUserStorageRepository
import es.samiralkalii.myapps.data.common.RemoteCommonDataRepository
import es.samiralkalii.myapps.data.notifications.RemoteNotificationsRepository
import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository
import es.samiralkalii.myapps.filesystem.FileSystemRepository
import es.samiralkalii.myapps.notification.NotificationRepository
import es.samiralkalii.myapps.preference.PreferenceRepository
import org.koin.dsl.module

val dataModule= module {

    factory { RemoteUserAuthRepository(get()) }
    factory { RemoteUserRepository(get()) }
    factory { PreferenceRepository(get()) }
    factory { RemoteUserStorageRepository(get()) }
    factory { FileSystemRepository(get()) }
    factory { NotificationRepository(get()) }
    factory { RemoteTeamManagementRepository(get()) }
    factory { RemoteNotificationsRepository(get()) }
    factory { RemoteCommonDataRepository(get()) }
}