package es.samiralkalii.myapps.soporteit.di

import androidx.lifecycle.LiveData
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.samiralkalii.myapps.data.authlogin.IRemoteUserAuthDataSource
import es.samiralkalii.myapps.data.authlogin.IRemoteUserDatasource
import es.samiralkalii.myapps.data.authlogin.IUserStorage
import es.samiralkalii.myapps.database.ILocalUserDatabase
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.filesystem.IFileSystemManager
import es.samiralkalii.myapps.preference.IPreferences
import es.samiralkalii.myapps.soporteit.framework.localstorage.db.database.AppDatabase
import es.samiralkalii.myapps.soporteit.framework.localstorage.db.database.DATABASE_NAME
import es.samiralkalii.myapps.soporteit.framework.localstorage.db.manager.LocalUserDatabaseManager
import es.samiralkalii.myapps.soporteit.framework.localstorage.filesystem.FileSystemManager
import es.samiralkalii.myapps.soporteit.framework.remotestorage.auth.RemoteUserAuthManager
import es.samiralkalii.myapps.soporteit.framework.remotestorage.database.RemoteRemoteUserDatasourceManager
import es.samiralkalii.myapps.soporteit.framework.remotestorage.storage.RemoteUserStorageManager
import es.samiralkalii.myapps.soporteit.framework.sharedpreferences.SharedPreferencesManager
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileViewModel
import es.samiralkalii.myapps.soporteit.ui.logup.LogupViewModel
import es.samiralkalii.myapps.soporteit.ui.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule= module {

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    single<AppDatabase> { Room.databaseBuilder(get(), AppDatabase::class.java, DATABASE_NAME).build() }
    single {get<AppDatabase>().userDao()}

    factory<IRemoteUserAuthDataSource> { RemoteUserAuthManager(get()) }
    factory<IRemoteUserDatasource> { RemoteRemoteUserDatasourceManager(get()) }
    factory<IPreferences> { SharedPreferencesManager(get()) }
    factory<IUserStorage> { RemoteUserStorageManager(get()) }
    factory<IFileSystemManager> { FileSystemManager(get()) }
    factory<ILocalUserDatabase<LiveData<User>>> { LocalUserDatabaseManager(get()) }


    viewModel { SplashViewModel(get()) }
    viewModel { LogupViewModel(get(), get()) }
    viewModel { HomeViewModel()}
    viewModel { ProfileViewModel(get(), get()) }
}