package com.davcres.rickandmorty.core.di

import com.davcres.rickandmorty.core.common.DefaultDispatcherProvider
import com.davcres.rickandmorty.core.common.DispatcherProvider
import org.koin.dsl.module

val coreModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}
