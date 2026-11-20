package com.davcres.template.data.di

import com.davcres.template.data.cache.CharacterMemoryCache
import com.davcres.template.data.cache.CharacterMemoryCacheImpl
import com.davcres.template.data.repository.CharacterRepositoryImpl
import com.davcres.template.domain.repository.CharacterRepository
import org.koin.dsl.module

val dataModule = module {
    single<CharacterMemoryCache> { CharacterMemoryCacheImpl() }
    single<CharacterRepository> { CharacterRepositoryImpl(get()) }
}
