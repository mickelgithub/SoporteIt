package es.samiralkalii.myapps.soporteit

import android.app.Application
import es.samiralkalii.myapps.soporteit.di.appModule
import es.samiralkalii.myapps.soporteit.di.dataModule
import es.samiralkalii.myapps.soporteit.di.useCaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, dataModule, useCaseModule))
        }

    }

}