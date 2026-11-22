package com.davcres.rickandmorty.data.local.datasource

import com.davcres.rickandmorty.data.cache.TimeProvider
import com.davcres.rickandmorty.data.local.dao.CharacterDao
import com.davcres.rickandmorty.data.local.dao.SearchDao
import com.davcres.rickandmorty.data.local.database.TransactionRunner
import com.davcres.rickandmorty.data.local.mapper.toDomain
import com.davcres.rickandmorty.data.local.mapper.toEntity
import com.davcres.rickandmorty.data.local.model.SearchPageCharacterCrossRef
import com.davcres.rickandmorty.data.local.model.SearchPageEntity
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.PagedCharacters

private const val DEFAULT_TTL_HOURS = 24L
private const val MILLIS_PER_HOUR = 3_600_000L
const val DEFAULT_L2_CACHE_TTL_MILLIS = DEFAULT_TTL_HOURS * MILLIS_PER_HOUR

interface CharacterLocalDataSource {
    suspend fun getPagedCharacters(
        query: String,
        page: Int,
        ignoreTtl: Boolean = false,
    ): PagedCharacters?

    suspend fun savePagedCharacters(
        query: String,
        page: Int,
        pagedCharacters: PagedCharacters,
    )

    suspend fun getCharacter(id: Int): Character?
    suspend fun saveCharacter(character: Character)
    suspend fun clearAll()
}

class CharacterLocalDataSourceImpl(
    private val characterDao: CharacterDao,
    private val searchDao: SearchDao,
    private val transactionRunner: TransactionRunner,
    private val timeProvider: TimeProvider,
    private val ttlMillis: Long = DEFAULT_L2_CACHE_TTL_MILLIS,
) : CharacterLocalDataSource {

    private fun normalizeQuery(query: String): String = query.lowercase().trim()

    override suspend fun getPagedCharacters(
        query: String,
        page: Int,
        ignoreTtl: Boolean,
    ): PagedCharacters? {
        val normalizedQuery = normalizeQuery(query)
        val searchPage = searchDao.getSearchPage(normalizedQuery, page)
        val isValid = searchPage != null && (ignoreTtl || (timeProvider.currentTimeMillis() - searchPage.updatedAt <= ttlMillis))

        if (!isValid) return null

        val characters = searchDao.getCharactersForPage(normalizedQuery, page).map { it.toDomain() }
        return PagedCharacters(
            characters = characters,
            page = searchPage!!.page,
            totalPages = searchPage.totalPages,
            hasNextPage = searchPage.hasNextPage,
        )
    }

    override suspend fun savePagedCharacters(
        query: String,
        page: Int,
        pagedCharacters: PagedCharacters,
    ) {
        val normalizedQuery = normalizeQuery(query)
        val now = timeProvider.currentTimeMillis()

        transactionRunner {
            characterDao.insertCharacters(pagedCharacters.characters.map { it.toEntity() })

            searchDao.insertSearchPage(
                SearchPageEntity(
                    query = normalizedQuery,
                    page = page,
                    totalPages = pagedCharacters.totalPages,
                    hasNextPage = pagedCharacters.hasNextPage,
                    updatedAt = now,
                ),
            )

            val crossRefs = pagedCharacters.characters.mapIndexed { index, character ->
                SearchPageCharacterCrossRef(
                    query = normalizedQuery,
                    page = page,
                    characterId = character.id,
                    position = index,
                )
            }
            searchDao.insertCrossRefs(crossRefs)
        }
    }

    override suspend fun getCharacter(id: Int): Character? =
        characterDao.getCharacterById(id)?.toDomain()

    override suspend fun saveCharacter(character: Character) {
        characterDao.insertCharacter(character.toEntity())
    }

    override suspend fun clearAll() {
        transactionRunner {
            searchDao.clearAllSearchPages()
            characterDao.clearAll()
        }
    }
}
