package com.davcres.template

import android.app.Application
import com.davcres.template.di.appModules
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TemplateApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TemplateApplication)
            modules(appModules)

            analytics() // kotzilla analytics
        }
    }
}
