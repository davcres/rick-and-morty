package com.davcres.rickandmorty.data.repository

import com.davcres.rickandmorty.core.common.DispatcherProvider
import com.davcres.rickandmorty.data.cache.CharacterMemoryCache
import com.davcres.rickandmorty.data.remote.datasource.CharacterRemoteDataSource
import com.davcres.rickandmorty.data.remote.mapper.toAppError
import com.davcres.rickandmorty.data.remote.mapper.toDomain
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.repository.CharacterRepository
import kotlinx.coroutines.withContext

class CharacterRepositoryImpl(
    private val remoteDataSource: CharacterRemoteDataSource,
    private val memoryCache: CharacterMemoryCache,
    private val dispatchers: DispatcherProvider,
) : CharacterRepository {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun searchCharacters(query: String): Result<List<Character>> =
        withContext(dispatchers.io) {
            val cached = memoryCache.get(query)
            if (cached != null) {
                return@withContext Result.Success(cached)
            }
            try {
                val remoteCharacters = remoteDataSource.searchCharacters(query).map { it.toDomain() }
                memoryCache.put(query, remoteCharacters)
                Result.Success(remoteCharacters)
            } catch (e: Exception) {
                Result.Error(e.toAppError())
            }
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getCharacterDetail(id: Int): Result<Character> =
        withContext(dispatchers.io) {
            try {
                val character = remoteDataSource.getCharacter(id).toDomain()
                Result.Success(character)
            } catch (e: Exception) {
                Result.Error(e.toAppError())
            }
        }
}
