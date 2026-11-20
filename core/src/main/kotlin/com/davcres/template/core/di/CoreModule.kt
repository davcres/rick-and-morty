package com.davcres.template.core.di

import com.davcres.template.core.common.DefaultDispatcherProvider
import com.davcres.template.core.common.DispatcherProvider
import org.koin.dsl.module

val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}
