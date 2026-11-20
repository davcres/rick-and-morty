package com.davcres.template.data.repository

import com.davcres.template.core.common.DispatcherProvider
import com.davcres.template.data.cache.CharacterMemoryCache
import com.davcres.template.domain.model.Character
import com.davcres.template.domain.model.Result
import com.davcres.template.domain.repository.CharacterRepository
import kotlinx.coroutines.withContext

class CharacterRepositoryImpl(
    private val memoryCache: CharacterMemoryCache,
    private val dispatchers: DispatcherProvider,
) : CharacterRepository {

    override suspend fun searchCharacters(query: String): Result<List<Character>> =
        withContext(dispatchers.io) {
            val cached = memoryCache.get(query)
            if (cached != null) {
                return@withContext Result.Success(cached)
            }
            Result.Success(emptyList())
        }

    override suspend fun getCharacterDetail(id: Int): Result<Character> =
        withContext(dispatchers.io) {
            Result.Error(NoSuchElementException("Character not found"))
        }
}
