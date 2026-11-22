package com.davcres.rickandmorty

import android.app.Application
import com.davcres.rickandmorty.di.appConfigModule
import com.davcres.rickandmorty.di.appModules
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RickAndMortyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@RickAndMortyApplication)
            modules(appModules + appConfigModule)

            analytics()
        }
    }
}
