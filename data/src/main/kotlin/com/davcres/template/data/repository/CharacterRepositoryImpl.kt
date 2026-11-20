package com.davcres.template.data.repository

import com.davcres.template.core.common.DispatcherProvider
import com.davcres.template.data.cache.CharacterMemoryCache
import com.davcres.template.data.remote.datasource.CharacterRemoteDataSource
import com.davcres.template.data.remote.mapper.toDomain
import com.davcres.template.domain.model.Character
import com.davcres.template.domain.model.Result
import com.davcres.template.domain.repository.CharacterRepository
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
                Result.Error(e)
            }
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getCharacterDetail(id: Int): Result<Character> =
        withContext(dispatchers.io) {
            try {
                val character = remoteDataSource.getCharacter(id).toDomain()
                Result.Success(character)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}
