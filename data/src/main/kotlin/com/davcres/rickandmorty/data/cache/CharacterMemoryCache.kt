package com.davcres.rickandmorty.data.cache

import com.davcres.rickandmorty.domain.model.Character

private const val DEFAULT_TTL_MINUTES = 5L
private const val MILLIS_PER_MINUTE = 60_000L
const val DEFAULT_CACHE_TTL_MILLIS = DEFAULT_TTL_MINUTES * MILLIS_PER_MINUTE

data class CachedCharacters(
    val characters: List<Character>,
    val timestamp: Long,
)

interface TimeProvider {
    fun currentTimeMillis(): Long
}

class DefaultTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}

interface CharacterMemoryCache {
    fun get(query: String): List<Character>?
    fun put(query: String, characters: List<Character>)
    fun clear()
}

class CharacterMemoryCacheImpl(
    private val timeProvider: TimeProvider,
    private val ttlMillis: Long = DEFAULT_CACHE_TTL_MILLIS,
) : CharacterMemoryCache {

    private val cache = mutableMapOf<String, CachedCharacters>()

    @Synchronized
    override fun get(query: String): List<Character>? {
        val cached = cache[query.lowercase().trim()] ?: return null
        val now = timeProvider.currentTimeMillis()
        return if (now - cached.timestamp <= ttlMillis) {
            cached.characters
        } else {
            cache.remove(query.lowercase().trim())
            null
        }
    }

    @Synchronized
    override fun put(query: String, characters: List<Character>) {
        cache[query.lowercase().trim()] = CachedCharacters(
            characters = characters,
            timestamp = timeProvider.currentTimeMillis(),
        )
    }

    @Synchronized
    override fun clear() {
        cache.clear()
    }
}
