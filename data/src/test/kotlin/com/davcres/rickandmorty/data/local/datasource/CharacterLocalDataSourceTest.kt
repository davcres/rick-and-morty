package com.davcres.rickandmorty.data.local.datasource

import com.davcres.rickandmorty.data.cache.TimeProvider
import com.davcres.rickandmorty.data.local.dao.CharacterDao
import com.davcres.rickandmorty.data.local.dao.SearchDao
import com.davcres.rickandmorty.data.local.database.TransactionRunner
import com.davcres.rickandmorty.data.local.model.CharacterEntity
import com.davcres.rickandmorty.data.local.model.SearchPageEntity
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.PagedCharacters
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CharacterLocalDataSourceTest {

    private lateinit var characterDao: CharacterDao
    private lateinit var searchDao: SearchDao
    private lateinit var transactionRunner: TransactionRunner
    private lateinit var timeProvider: TimeProvider
    private lateinit var localDataSource: CharacterLocalDataSourceImpl

    private val testTtlMillis = 60_000L

    private val sampleCharacterEntity = CharacterEntity(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "Scientist",
        gender = "Male",
        origin = "Earth",
        location = "Citadel",
        imageUrl = "image_url",
        episodeCount = 51,
    )

    private val sampleDomainCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "Scientist",
        gender = "Male",
        origin = "Earth",
        location = "Citadel",
        imageUrl = "image_url",
        episodeCount = 51,
    )

    @BeforeEach
    fun setUp() {
        characterDao = mockk(relaxed = true)
        searchDao = mockk(relaxed = true)
        timeProvider = mockk(relaxed = true)

        transactionRunner = object : TransactionRunner {
            override suspend operator fun <T> invoke(block: suspend () -> T): T = block()
        }

        localDataSource = CharacterLocalDataSourceImpl(
            characterDao = characterDao,
            searchDao = searchDao,
            transactionRunner = transactionRunner,
            timeProvider = timeProvider,
            ttlMillis = testTtlMillis,
        )
    }

    @Test
    fun `getPagedCharacters should return null when search page does not exist`() = runTest {
        coEvery { searchDao.getSearchPage("rick", 1) } returns null

        val result = localDataSource.getPagedCharacters("rick", 1)

        assertNull(result)
    }

    @Test
    fun `getPagedCharacters should return null when search page is expired and ignoreTtl is false`() = runTest {
        val searchPage = SearchPageEntity(
            query = "rick",
            page = 1,
            totalPages = 3,
            hasNextPage = true,
            updatedAt = 1_000L,
        )
        coEvery { searchDao.getSearchPage("rick", 1) } returns searchPage
        every { timeProvider.currentTimeMillis() } returns 1_000L + testTtlMillis + 1L

        val result = localDataSource.getPagedCharacters("rick", 1, ignoreTtl = false)

        assertNull(result)
    }

    @Test
    fun `getPagedCharacters should return characters when search page is expired and ignoreTtl is true`() = runTest {
        val searchPage = SearchPageEntity(
            query = "rick",
            page = 1,
            totalPages = 3,
            hasNextPage = true,
            updatedAt = 1_000L,
        )
        coEvery { searchDao.getSearchPage("rick", 1) } returns searchPage
        coEvery { searchDao.getCharactersForPage("rick", 1) } returns listOf(sampleCharacterEntity)
        every { timeProvider.currentTimeMillis() } returns 1_000L + testTtlMillis + 100L

        val result = localDataSource.getPagedCharacters("rick", 1, ignoreTtl = true)

        assertNotNull(result)
        assertEquals(1, result?.characters?.size)
        assertEquals(sampleDomainCharacter, result?.characters?.first())
        assertEquals(3, result?.totalPages)
    }

    @Test
    fun `getPagedCharacters should return characters within TTL`() = runTest {
        val searchPage = SearchPageEntity(
            query = "rick",
            page = 1,
            totalPages = 3,
            hasNextPage = true,
            updatedAt = 1_000L,
        )
        coEvery { searchDao.getSearchPage("rick", 1) } returns searchPage
        coEvery { searchDao.getCharactersForPage("rick", 1) } returns listOf(sampleCharacterEntity)
        every { timeProvider.currentTimeMillis() } returns 2_000L

        val result = localDataSource.getPagedCharacters("   Rick  ", 1)

        assertNotNull(result)
        assertEquals(1, result?.characters?.size)
        assertEquals(sampleDomainCharacter, result?.characters?.first())
        assertEquals(1, result?.page)
        assertEquals(3, result?.totalPages)
        assertEquals(true, result?.hasNextPage)
    }

    @Test
    fun `savePagedCharacters should insert characters, page, and cross references in a transaction`() = runTest {
        every { timeProvider.currentTimeMillis() } returns 5_000L
        val pagedCharacters = PagedCharacters(
            characters = listOf(sampleDomainCharacter),
            page = 1,
            totalPages = 2,
            hasNextPage = true,
        )

        localDataSource.savePagedCharacters("Rick", 1, pagedCharacters)

        coVerify(exactly = 1) { characterDao.insertCharacters(listOf(sampleCharacterEntity)) }
        coVerify(exactly = 1) {
            searchDao.insertSearchPage(
                SearchPageEntity(
                    query = "rick",
                    page = 1,
                    totalPages = 2,
                    hasNextPage = true,
                    updatedAt = 5_000L,
                ),
            )
        }
        coVerify(exactly = 1) { searchDao.insertCrossRefs(any()) }
    }

    @Test
    fun `getCharacter should return domain character when entity exists in database`() = runTest {
        coEvery { characterDao.getCharacterById(1) } returns sampleCharacterEntity

        val result = localDataSource.getCharacter(1)

        assertEquals(sampleDomainCharacter, result)
        coVerify(exactly = 1) { characterDao.getCharacterById(1) }
    }

    @Test
    fun `getCharacter should return null when character does not exist`() = runTest {
        coEvery { characterDao.getCharacterById(99) } returns null

        val result = localDataSource.getCharacter(99)

        assertNull(result)
    }

    @Test
    fun `saveCharacter should insert character entity into database`() = runTest {
        localDataSource.saveCharacter(sampleDomainCharacter)

        coVerify(exactly = 1) { characterDao.insertCharacter(sampleCharacterEntity) }
    }

    @Test
    fun `clearAll should delete all search pages and characters`() = runTest {
        localDataSource.clearAll()

        coVerify(exactly = 1) { searchDao.clearAllSearchPages() }
        coVerify(exactly = 1) { characterDao.clearAll() }
    }
}
