package com.davcres.rickandmorty.di

import com.davcres.rickandmorty.BuildConfig
import com.davcres.rickandmorty.data.cache.CacheConfiguration
import org.koin.dsl.module

private const val MILLIS_PER_MINUTE = 60_000L
private const val MILLIS_PER_HOUR = 3_600_000L

val appConfigModule = module {
    single {
        CacheConfiguration(
            l1TtlMillis = BuildConfig.CACHE_L1_TTL_MINUTES * MILLIS_PER_MINUTE,
            l2TtlMillis = BuildConfig.CACHE_L2_TTL_HOURS * MILLIS_PER_HOUR,
        )
    }
}
