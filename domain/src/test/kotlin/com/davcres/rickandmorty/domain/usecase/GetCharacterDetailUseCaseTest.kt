package com.davcres.rickandmorty.domain.usecase

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

class GetCharacterDetailUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: GetCharacterDetailUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GetCharacterDetailUseCase(repository)
    }

    @Test
    fun `invoke should return character detail when repository succeeds`() = runTest {
        val characterId = 1
        val expectedCharacter = Character(
            id = characterId,
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
        coEvery { repository.getCharacterDetail(characterId) } returns Result.Success(expectedCharacter)

        val result = useCase(characterId)

        assertTrue(result is Result.Success)
        assertEquals(expectedCharacter, (result as Result.Success).data)
        coVerify(exactly = 1) { repository.getCharacterDetail(characterId) }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val characterId = 999
        val exception = RuntimeException("Character not found")
        coEvery { repository.getCharacterDetail(characterId) } returns Result.Error(exception)

        val result = useCase(characterId)

        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
        coVerify(exactly = 1) { repository.getCharacterDetail(characterId) }
    }
}
