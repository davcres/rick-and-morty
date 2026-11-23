package com.davcres.rickandmorty.data.repository

import com.davcres.rickandmorty.core.common.DispatcherProvider
import com.davcres.rickandmorty.data.cache.CharacterMemoryCache
import com.davcres.rickandmorty.data.local.datasource.CharacterLocalDataSource
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
    private val localDataSource: CharacterLocalDataSource,
    private val memoryCache: CharacterMemoryCache,
    private val dispatchers: DispatcherProvider,
) : CharacterRepository {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun searchCharacters(query: String, page: Int): Result<PagedCharacters> =
        withContext(dispatchers.io) {
            val memoryCached = memoryCache.get(query, page)
            if (memoryCached != null) {
                return@withContext Result.Success(memoryCached)
            }

            val localCached = localDataSource.getPagedCharacters(query, page)
            if (localCached != null) {
                memoryCache.put(query, page, localCached)
                return@withContext Result.Success(localCached)
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
                localDataSource.savePagedCharacters(query, page, pagedCharacters)
                memoryCache.put(query, page, pagedCharacters)
                Result.Success(pagedCharacters)
            } catch (e: Exception) {
                val offlineData = localDataSource.getPagedCharacters(query, page, ignoreTtl = true)
                if (offlineData != null) {
                    memoryCache.put(query, page, offlineData)
                    Result.Success(offlineData)
                } else {
                    Result.Error(e.toAppError())
                }
            }
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getCharacterDetail(id: Int): Result<Character> =
        withContext(dispatchers.io) {
            val memoryCached = memoryCache.getCharacter(id)
            if (memoryCached != null) {
                return@withContext Result.Success(memoryCached)
            }

            val localCached = localDataSource.getCharacter(id)
            if (localCached != null) {
                memoryCache.putCharacter(localCached)
                return@withContext Result.Success(localCached)
            }

            try {
                val character = remoteDataSource.getCharacter(id).toDomain()
                localDataSource.saveCharacter(character)
                memoryCache.putCharacter(character)
                Result.Success(character)
            } catch (e: Exception) {
                Result.Error(e.toAppError())
            }
        }
}
