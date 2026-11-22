package com.davcres.rickandmorty.data.cache

import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.PagedCharacters

private const val DEFAULT_TTL_MINUTES = 2L
private const val MILLIS_PER_MINUTE = 60_000L
const val DEFAULT_CACHE_TTL_MILLIS = DEFAULT_TTL_MINUTES * MILLIS_PER_MINUTE

data class CachedPagedCharacters(
    val pagedCharacters: PagedCharacters,
    val timestamp: Long,
)

data class CachedCharacter(
    val character: Character,
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
    fun getCharacter(id: Int): Character?
    fun putCharacter(character: Character)
    fun putCharacters(characters: List<Character>)
    fun clear()
}

class CharacterMemoryCacheImpl(
    private val timeProvider: TimeProvider,
    private val ttlMillis: Long = DEFAULT_CACHE_TTL_MILLIS,
) : CharacterMemoryCache {

    private val pagedCache = mutableMapOf<String, CachedPagedCharacters>()
    private val characterCache = mutableMapOf<Int, CachedCharacter>()

    private fun buildKey(query: String, page: Int): String =
        "${query.lowercase().trim()}#$page"

    @Synchronized
    override fun get(query: String, page: Int): PagedCharacters? {
        val key = buildKey(query, page)
        val cached = pagedCache[key] ?: return null
        val now = timeProvider.currentTimeMillis()
        return if (now - cached.timestamp <= ttlMillis) {
            cached.pagedCharacters
        } else {
            pagedCache.remove(key)
            null
        }
    }

    @Synchronized
    override fun put(query: String, page: Int, pagedCharacters: PagedCharacters) {
        val key = buildKey(query, page)
        pagedCache[key] = CachedPagedCharacters(
            pagedCharacters = pagedCharacters,
            timestamp = timeProvider.currentTimeMillis(),
        )
        putCharacters(pagedCharacters.characters)
    }

    @Synchronized
    override fun getCharacter(id: Int): Character? {
        val cached = characterCache[id] ?: return null
        val now = timeProvider.currentTimeMillis()
        return if (now - cached.timestamp <= ttlMillis) {
            cached.character
        } else {
            characterCache.remove(id)
            null
        }
    }

    @Synchronized
    override fun putCharacter(character: Character) {
        putCharacters(listOf(character))
    }

    @Synchronized
    override fun putCharacters(characters: List<Character>) {
        val now = timeProvider.currentTimeMillis()
        characters.forEach { character ->
            characterCache[character.id] = CachedCharacter(
                character = character,
                timestamp = now,
            )
        }
    }

    @Synchronized
    override fun clear() {
        pagedCache.clear()
        characterCache.clear()
    }
}
