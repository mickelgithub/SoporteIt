package es.samiralkalii.myapps.soporteit.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.authlogin.IUserAccess
import es.samiralkalii.myapps.data.authlogin.IUserDatabase
import es.samiralkalii.myapps.preference.IPreferences
import es.samiralkalii.myapps.soporteit.framework.firebase.auth.UserAccess
import es.samiralkalii.myapps.soporteit.framework.firebase.database.UserDatabase
import es.samiralkalii.myapps.soporteit.framework.sharedpreferences.MySharedPreferences
import es.samiralkalii.myapps.soporteit.ui.register.RegisterViewModel
import es.samiralkalii.myapps.soporteit.ui.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule= module {

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    factory<IUserAccess> { UserAccess(get()) }
    factory<IUserDatabase> { UserDatabase(get(), get()) }
    factory<IPreferences> { MySharedPreferences(get()) }

    viewModel { SplashViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}