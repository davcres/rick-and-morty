package com.davcres.template.data.di

import com.davcres.template.data.cache.CharacterMemoryCache
import com.davcres.template.data.cache.CharacterMemoryCacheImpl
import com.davcres.template.data.cache.DefaultTimeProvider
import com.davcres.template.data.cache.TimeProvider
import com.davcres.template.data.remote.api.RickAndMortyApi
import com.davcres.template.data.remote.datasource.CharacterRemoteDataSource
import com.davcres.template.data.remote.datasource.CharacterRemoteDataSourceImpl
import com.davcres.template.data.repository.CharacterRepositoryImpl
import com.davcres.template.domain.repository.CharacterRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "https://rickandmortyapi.com/api/"

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
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
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
