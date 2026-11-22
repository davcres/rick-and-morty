package com.davcres.rickandmorty.data.cache

import com.davcres.rickandmorty.data.local.datasource.DEFAULT_L2_CACHE_TTL_MILLIS

data class CacheConfiguration(
    val l1TtlMillis: Long = DEFAULT_CACHE_TTL_MILLIS,
    val l2TtlMillis: Long = DEFAULT_L2_CACHE_TTL_MILLIS,
)
