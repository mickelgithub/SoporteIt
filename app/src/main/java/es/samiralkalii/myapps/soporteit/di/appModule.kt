package es.samiralkalii.myapps.soporteit.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.samiralkalii.myapps.data.authlogin.IUserAccess
import es.samiralkalii.myapps.data.authlogin.IUserDatabase
import es.samiralkalii.myapps.data.authlogin.IUserStorage
import es.samiralkalii.myapps.filesystem.IFileSystemManager
import es.samiralkalii.myapps.preference.IPreferences
import es.samiralkalii.myapps.soporteit.framework.filesystem.FileSystemManager
import es.samiralkalii.myapps.soporteit.framework.firebase.auth.UserAccess
import es.samiralkalii.myapps.soporteit.framework.firebase.database.UserDatabase
import es.samiralkalii.myapps.soporteit.framework.firebase.storage.UserStorage
import es.samiralkalii.myapps.soporteit.framework.sharedpreferences.MySharedPreferences
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

    factory<IUserAccess> { UserAccess(get()) }
    factory<IUserDatabase> { UserDatabase(get()) }
    factory<IPreferences> { MySharedPreferences(get()) }
    factory<IUserStorage> { UserStorage(get()) }
    factory<IFileSystemManager> { FileSystemManager(get()) }

    viewModel { SplashViewModel(get()) }
    viewModel { LogupViewModel(get(), get()) }
    viewModel { HomeViewModel()}
    viewModel { ProfileViewModel(get()) }
}