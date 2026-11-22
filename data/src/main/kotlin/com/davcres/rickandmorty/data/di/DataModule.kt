package com.davcres.rickandmorty.data.di

import com.davcres.rickandmorty.data.BuildConfig
import com.davcres.rickandmorty.data.cache.CharacterMemoryCache
import com.davcres.rickandmorty.data.cache.CharacterMemoryCacheImpl
import com.davcres.rickandmorty.data.cache.DefaultTimeProvider
import com.davcres.rickandmorty.data.cache.TimeProvider
import com.davcres.rickandmorty.data.remote.api.RickAndMortyApi
import com.davcres.rickandmorty.data.remote.datasource.CharacterRemoteDataSource
import com.davcres.rickandmorty.data.remote.datasource.CharacterRemoteDataSourceImpl
import com.davcres.rickandmorty.data.repository.CharacterRepositoryImpl
import com.davcres.rickandmorty.domain.repository.CharacterRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://rickandmortyapi.com/api/"
private const val TIMEOUT_SECONDS = 30L

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

    single<CharacterRemoteDataSource> { CharacterRemoteDataSourceImpl(get()) }
    single<TimeProvider> { DefaultTimeProvider() }
    single<CharacterMemoryCache> { CharacterMemoryCacheImpl(get()) }
    single<CharacterRepository> { CharacterRepositoryImpl(get(), get(), get()) }
}
