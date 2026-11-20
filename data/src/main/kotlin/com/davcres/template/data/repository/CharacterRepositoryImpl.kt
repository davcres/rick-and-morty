package com.davcres.template.data.repository

import com.davcres.template.data.cache.CharacterMemoryCache
import com.davcres.template.domain.model.Character
import com.davcres.template.domain.model.Result
import com.davcres.template.domain.repository.CharacterRepository

class CharacterRepositoryImpl(
    private val memoryCache: CharacterMemoryCache,
) : CharacterRepository {

    override suspend fun searchCharacters(query: String): Result<List<Character>> {
        val cached = memoryCache.get(query)
        if (cached != null) {
            return Result.Success(cached)
        }
        return Result.Success(emptyList())
    }

    override suspend fun getCharacterDetail(id: Int): Result<Character> =
        Result.Error(NoSuchElementException("Character not found"))
}
