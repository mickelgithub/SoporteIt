package es.samiralkalii.myapps.soporteit.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.samiralkalii.myapps.data.authlogin.IRemoteUserAuthDataSource
import es.samiralkalii.myapps.data.authlogin.IRemoteUserDatasource
import es.samiralkalii.myapps.data.authlogin.IRemoteUserStorageDataSource
import es.samiralkalii.myapps.data.notifications.IRemoteNotificationsDatasource
import es.samiralkalii.myapps.data.teammanagement.IRemoteTeamManagementDatasource
import es.samiralkalii.myapps.filesystem.IFileSystemManager
import es.samiralkalii.myapps.notification.INotification
import es.samiralkalii.myapps.preference.IPreferences
import es.samiralkalii.myapps.soporteit.framework.localstorage.filesystem.FileSystemManager
import es.samiralkalii.myapps.soporteit.framework.notification.NotificationManager
import es.samiralkalii.myapps.soporteit.framework.remotestorage.auth.RemoteUserAuthManager
import es.samiralkalii.myapps.soporteit.framework.remotestorage.database.RemoteNotificationsDatasourceManager
import es.samiralkalii.myapps.soporteit.framework.remotestorage.database.RemoteTeamDatasourceManager
import es.samiralkalii.myapps.soporteit.framework.remotestorage.database.RemoteUserDatasourceManager
import es.samiralkalii.myapps.soporteit.framework.remotestorage.storage.RemoteUserStorageDataSourceManager
import es.samiralkalii.myapps.soporteit.framework.sharedpreferences.SharedPreferencesManager
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.home.dialog.InviteMemberDialog
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileViewModel
import es.samiralkalii.myapps.soporteit.ui.logup.LogupViewModel
import es.samiralkalii.myapps.soporteit.ui.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule= module {

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    factory<IRemoteUserAuthDataSource> { RemoteUserAuthManager(get()) }
    factory<IRemoteUserDatasource> { RemoteUserDatasourceManager(get(), get()) }
    factory<IPreferences> { SharedPreferencesManager(get()) }
    factory<IRemoteUserStorageDataSource> { RemoteUserStorageDataSourceManager(get()) }
    factory<IFileSystemManager> { FileSystemManager(get()) }
    factory<INotification> { NotificationManager(get()) }
    factory<IRemoteTeamManagementDatasource> { RemoteTeamDatasourceManager(get()) }
    factory<IRemoteNotificationsDatasource> { RemoteNotificationsDatasourceManager(get()) }



    viewModel { SplashViewModel(get(), get()) }
    viewModel { LogupViewModel(get(), get()) }
    viewModel { HomeViewModel()}
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { HomeFragmentViewModel(get()) }
    viewModel { InviteMemberDialog.InviteMemberDialogViewModel(get(), get()) }
    viewModel { HomeNotificationsFragmentViewModel(get()) }
}