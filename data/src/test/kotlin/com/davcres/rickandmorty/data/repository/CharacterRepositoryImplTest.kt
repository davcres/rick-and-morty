package com.davcres.rickandmorty.data.repository

import com.davcres.rickandmorty.data.cache.CharacterMemoryCache
import com.davcres.rickandmorty.data.remote.datasource.CharacterRemoteDataSource
import com.davcres.rickandmorty.data.remote.model.CharacterDto
import com.davcres.rickandmorty.data.remote.model.CharacterResponseDto
import com.davcres.rickandmorty.data.remote.model.LocationDto
import com.davcres.rickandmorty.data.remote.model.OriginDto
import com.davcres.rickandmorty.data.remote.model.PageInfoDto
import com.davcres.rickandmorty.data.util.FakeDispatcherProvider
import com.davcres.rickandmorty.domain.model.AppError
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.PagedCharacters
import com.davcres.rickandmorty.domain.model.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.SocketTimeoutException

class CharacterRepositoryImplTest {

    private lateinit var remoteDataSource: CharacterRemoteDataSource
    private lateinit var memoryCache: CharacterMemoryCache
    private lateinit var dispatchers: FakeDispatcherProvider
    private lateinit var repository: CharacterRepositoryImpl

    private val sampleCharacterDto = CharacterDto(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = OriginDto(name = "Earth (C-137)"),
        location = LocationDto(name = "Citadel of Ricks"),
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episode = listOf("https://rickandmortyapi.com/api/episode/1"),
    )

    private val sampleCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = "Earth (C-137)",
        location = "Citadel of Ricks",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episodeCount = 1,
    )

    private val samplePagedCharacters = PagedCharacters(
        characters = listOf(sampleCharacter),
        page = 1,
        totalPages = 2,
        hasNextPage = true,
    )

    @BeforeEach
    fun setUp() {
        remoteDataSource = mockk()
        memoryCache = mockk(relaxed = true)
        dispatchers = FakeDispatcherProvider()
        repository = CharacterRepositoryImpl(
            remoteDataSource = remoteDataSource,
            memoryCache = memoryCache,
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `searchCharacters should return cached data immediately when cache hit`() = runTest {
        val query = "Rick"
        val page = 1
        every { memoryCache.get(query, page) } returns samplePagedCharacters

        val result = repository.searchCharacters(query, page)

        assertTrue(result is Result.Success)
        assertEquals(samplePagedCharacters, (result as Result.Success).data)
        verify(exactly = 1) { memoryCache.get(query, page) }
        coVerify(exactly = 0) { remoteDataSource.searchCharacters(any(), any()) }
    }

    @Test
    fun `searchCharacters should call remote and cache results on cache miss`() = runTest {
        val query = "Rick"
        val page = 1
        val remoteResponse = CharacterResponseDto(
            info = PageInfoDto(pages = 2, next = "https://rickandmortyapi.com/api/character/?page=2"),
            results = listOf(sampleCharacterDto),
        )
        every { memoryCache.get(query, page) } returns null
        coEvery { remoteDataSource.searchCharacters(query, page) } returns remoteResponse

        val result = repository.searchCharacters(query, page)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(1, data.characters.size)
        assertEquals(sampleCharacter.id, data.characters[0].id)
        assertEquals(sampleCharacter.name, data.characters[0].name)
        assertEquals(page, data.page)
        assertEquals(2, data.totalPages)
        assertTrue(data.hasNextPage)
        verify(exactly = 1) { memoryCache.get(query, page) }
        coVerify(exactly = 1) { remoteDataSource.searchCharacters(query, page) }
        verify(exactly = 1) { memoryCache.put(query, page, any()) }
    }

    @Test
    fun `searchCharacters should return hasNextPage false when next is null`() = runTest {
        val query = "Rick"
        val page = 2
        val remoteResponse = CharacterResponseDto(
            info = PageInfoDto(pages = 2, next = null),
            results = listOf(sampleCharacterDto),
        )
        every { memoryCache.get(query, page) } returns null
        coEvery { remoteDataSource.searchCharacters(query, page) } returns remoteResponse

        val result = repository.searchCharacters(query, page)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertFalse(data.hasNextPage)
    }

    @Test
    fun `searchCharacters should return error when remoteDataSource throws exception`() = runTest {
        val query = "Rick"
        val page = 1
        val exception = SocketTimeoutException("Network timeout")
        every { memoryCache.get(query, page) } returns null
        coEvery { remoteDataSource.searchCharacters(query, page) } throws exception

        val result = repository.searchCharacters(query, page)

        assertTrue(result is Result.Error)
        assertEquals(AppError.Timeout, (result as Result.Error).error)
        verify(exactly = 0) { memoryCache.put(any(), any(), any()) }
    }

    @Test
    fun `getCharacterDetail should return character on remote success`() = runTest {
        val characterId = 1
        coEvery { remoteDataSource.getCharacter(characterId) } returns sampleCharacterDto

        val result = repository.getCharacterDetail(characterId)

        assertTrue(result is Result.Success)
        assertEquals(sampleCharacter.id, (result as Result.Success).data.id)
        assertEquals(sampleCharacter.name, (result as Result.Success).data.name)
        coVerify(exactly = 1) { remoteDataSource.getCharacter(characterId) }
    }

    @Test
    fun `getCharacterDetail should return error when remoteDataSource throws exception`() = runTest {
        val characterId = 1
        val exception = RuntimeException("Server error")
        coEvery { remoteDataSource.getCharacter(characterId) } throws exception

        val result = repository.getCharacterDetail(characterId)

        assertTrue(result is Result.Error)
        assertEquals(AppError.Unknown(exception), (result as Result.Error).error)
        coVerify(exactly = 1) { remoteDataSource.getCharacter(characterId) }
    }
}
