package com.davcres.rickandmorty.data.repository

import com.davcres.rickandmorty.core.common.DispatcherProvider
import com.davcres.rickandmorty.data.cache.CharacterMemoryCache
import com.davcres.rickandmorty.data.remote.datasource.CharacterRemoteDataSource
import com.davcres.rickandmorty.data.remote.mapper.toAppError
import com.davcres.rickandmorty.data.remote.mapper.toDomain
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.PagedCharacters
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.repository.CharacterRepository
import kotlinx.coroutines.withContext

class CharacterRepositoryImpl(
    private val remoteDataSource: CharacterRemoteDataSource,
    private val memoryCache: CharacterMemoryCache,
    private val dispatchers: DispatcherProvider,
) : CharacterRepository {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun searchCharacters(query: String, page: Int): Result<PagedCharacters> =
        withContext(dispatchers.io) {
            val cached = memoryCache.get(query, page)
            if (cached != null) {
                return@withContext Result.Success(cached)
            }
            try {
                val response = remoteDataSource.searchCharacters(query, page)
                val characters = response.results.map { it.toDomain() }
                val totalPages = response.info?.pages ?: 0
                val hasNextPage = response.info?.next != null
                val pagedCharacters = PagedCharacters(
                    characters = characters,
                    page = page,
                    totalPages = totalPages,
                    hasNextPage = hasNextPage,
                )
                memoryCache.put(query, page, pagedCharacters)
                Result.Success(pagedCharacters)
            } catch (e: Exception) {
                Result.Error(e.toAppError())
            }
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getCharacterDetail(id: Int): Result<Character> =
        withContext(dispatchers.io) {
            val cached = memoryCache.getCharacter(id)
            if (cached != null) {
                return@withContext Result.Success(cached)
            }
            try {
                val character = remoteDataSource.getCharacter(id).toDomain()
                memoryCache.putCharacter(character)
                Result.Success(character)
            } catch (e: Exception) {
                Result.Error(e.toAppError())
            }
        }
}
