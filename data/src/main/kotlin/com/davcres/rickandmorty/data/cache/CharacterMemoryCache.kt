package com.davcres.rickandmorty.data.cache

import com.davcres.rickandmorty.domain.model.PagedCharacters

private const val DEFAULT_TTL_MINUTES = 5L
private const val MILLIS_PER_MINUTE = 60_000L
const val DEFAULT_CACHE_TTL_MILLIS = DEFAULT_TTL_MINUTES * MILLIS_PER_MINUTE

data class CachedPagedCharacters(
    val pagedCharacters: PagedCharacters,
    val timestamp: Long,
)

interface TimeProvider {
    fun currentTimeMillis(): Long
}

class DefaultTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}

interface CharacterMemoryCache {
    fun get(query: String, page: Int): PagedCharacters?
    fun put(query: String, page: Int, pagedCharacters: PagedCharacters)
    fun clear()
}

class CharacterMemoryCacheImpl(
    private val timeProvider: TimeProvider,
    private val ttlMillis: Long = DEFAULT_CACHE_TTL_MILLIS,
) : CharacterMemoryCache {

    private val cache = mutableMapOf<String, CachedPagedCharacters>()

    private fun buildKey(query: String, page: Int): String =
        "${query.lowercase().trim()}#$page"

    @Synchronized
    override fun get(query: String, page: Int): PagedCharacters? {
        val key = buildKey(query, page)
        val cached = cache[key] ?: return null
        val now = timeProvider.currentTimeMillis()
        return if (now - cached.timestamp <= ttlMillis) {
            cached.pagedCharacters
        } else {
            cache.remove(key)
            null
        }
    }

    @Synchronized
    override fun put(query: String, page: Int, pagedCharacters: PagedCharacters) {
        val key = buildKey(query, page)
        cache[key] = CachedPagedCharacters(
            pagedCharacters = pagedCharacters,
            timestamp = timeProvider.currentTimeMillis(),
        )
    }

    @Synchronized
    override fun clear() {
        cache.clear()
    }
}
