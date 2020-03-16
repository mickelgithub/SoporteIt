package es.samiralkalii.myapps.soporteit

import android.app.Application
import android.util.Log
import es.samiralkalii.myapps.soporteit.di.appModule
import es.samiralkalii.myapps.soporteit.di.dataModule
import es.samiralkalii.myapps.soporteit.di.useCaseModule
import es.samiralkalii.myapps.soporteit.framework.notification.createNotificationChannel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber



class App: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, dataModule, useCaseModule))
        }
        createNotificationChannel(this)
    }
}