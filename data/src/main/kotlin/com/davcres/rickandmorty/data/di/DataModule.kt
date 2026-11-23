package com.davcres.rickandmorty.data.di

import androidx.room.Room
import androidx.room.withTransaction
import com.davcres.rickandmorty.data.BuildConfig
import com.davcres.rickandmorty.data.cache.CacheConfiguration
import com.davcres.rickandmorty.data.cache.CharacterMemoryCache
import com.davcres.rickandmorty.data.cache.CharacterMemoryCacheImpl
import com.davcres.rickandmorty.data.cache.DEFAULT_CACHE_TTL_MILLIS
import com.davcres.rickandmorty.data.cache.DefaultTimeProvider
import com.davcres.rickandmorty.data.cache.TimeProvider
import com.davcres.rickandmorty.data.local.database.RickAndMortyDatabase
import com.davcres.rickandmorty.data.local.database.TransactionRunner
import com.davcres.rickandmorty.data.local.datasource.CharacterLocalDataSource
import com.davcres.rickandmorty.data.local.datasource.CharacterLocalDataSourceImpl
import com.davcres.rickandmorty.data.local.datasource.DEFAULT_L2_CACHE_TTL_MILLIS
import com.davcres.rickandmorty.data.remote.api.RickAndMortyApi
import com.davcres.rickandmorty.data.remote.datasource.CharacterRemoteDataSource
import com.davcres.rickandmorty.data.remote.datasource.CharacterRemoteDataSourceImpl
import com.davcres.rickandmorty.data.repository.CharacterRepositoryImpl
import com.davcres.rickandmorty.domain.repository.CharacterRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://rickandmortyapi.com/api/"
private const val TIMEOUT_SECONDS = 30L
private const val DATABASE_NAME = "rick_and_morty.db"

val dataModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .callTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    single<RickAndMortyApi> {
        val json: Json = get()
        val okHttpClient: OkHttpClient = get()
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(RickAndMortyApi::class.java)
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            RickAndMortyDatabase::class.java,
            DATABASE_NAME,
        ).build()
    }

    single { get<RickAndMortyDatabase>().characterDao() }
    single { get<RickAndMortyDatabase>().searchDao() }

    single<TransactionRunner> {
        object : TransactionRunner {
            private val db: RickAndMortyDatabase = get()
            override suspend operator fun <T> invoke(block: suspend () -> T): T =
                db.withTransaction(block)
        }
    }

    single<CharacterLocalDataSource> {
        CharacterLocalDataSourceImpl(
            characterDao = get(),
            searchDao = get(),
            transactionRunner = get(),
            timeProvider = get(),
            ttlMillis = getOrNull<CacheConfiguration>()?.l2TtlMillis
                ?: DEFAULT_L2_CACHE_TTL_MILLIS,
        )
    }

    single<CharacterRemoteDataSource> { CharacterRemoteDataSourceImpl(get()) }
    single<TimeProvider> { DefaultTimeProvider() }
    single<CharacterMemoryCache> {
        CharacterMemoryCacheImpl(
            timeProvider = get(),
            ttlMillis = getOrNull<CacheConfiguration>()?.l1TtlMillis
                ?: DEFAULT_CACHE_TTL_MILLIS,
        )
    }
    single<CharacterRepository> {
        CharacterRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            memoryCache = get(),
            dispatchers = get(),
        )
    }
}
