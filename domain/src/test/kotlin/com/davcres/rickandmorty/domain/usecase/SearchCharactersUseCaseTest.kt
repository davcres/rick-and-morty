package com.davcres.rickandmorty.domain.usecase

import com.davcres.rickandmorty.domain.model.AppError
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchCharactersUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: SearchCharactersUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = SearchCharactersUseCase(repository)
    }

    @Test
    fun `invoke should return characters when repository succeeds`() = runTest {
        val query = "Rick"
        val expectedCharacters = listOf(
            Character(
                id = 1,
                name = "Rick Sanchez",
                status = "Alive",
                species = "Human",
                type = "",
                gender = "Male",
                origin = "Earth (C-137)",
                location = "Citadel of Ricks",
                imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                episodeCount = 51,
            )
        )
        coEvery { repository.searchCharacters(query) } returns Result.Success(expectedCharacters)

        val result = useCase(query)

        assertTrue(result is Result.Success)
        assertEquals(expectedCharacters, (result as Result.Success).data)
        coVerify(exactly = 1) { repository.searchCharacters(query) }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val query = "Unknown"
        val appError = AppError.Network
        coEvery { repository.searchCharacters(query) } returns Result.Error(appError)

        val result = useCase(query)

        assertTrue(result is Result.Error)
        assertEquals(appError, (result as Result.Error).error)
        coVerify(exactly = 1) { repository.searchCharacters(query) }
    }
}
