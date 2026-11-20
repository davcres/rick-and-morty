package com.davcres.rickandmorty.feature.characters.detail

import app.cash.turbine.test
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.usecase.GetCharacterDetailUseCase
import com.davcres.rickandmorty.feature.characters.util.FakeDispatcherProvider
import com.davcres.rickandmorty.feature.characters.util.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension(testDispatcher)

    private lateinit var getCharacterDetailUseCase: GetCharacterDetailUseCase
    private lateinit var dispatchers: FakeDispatcherProvider

    private val sampleCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "Scientist",
        gender = "Male",
        origin = "Earth (C-137)",
        location = "Citadel of Ricks",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        episodeCount = 51,
    )

    @BeforeEach
    fun setUp() {
        getCharacterDetailUseCase = mockk()
        dispatchers = FakeDispatcherProvider(testDispatcher)
    }

    private fun createViewModel(characterId: Int = 1): DetailViewModel = DetailViewModel(
        characterId = characterId,
        getCharacterDetailUseCase = getCharacterDetailUseCase,
        dispatcherProvider = dispatchers,
    )

    @Test
    fun `init loads character successfully`() = runTest(testDispatcher) {
        coEvery { getCharacterDetailUseCase(1) } returns Result.Success(sampleCharacter)

        val viewModel = createViewModel(characterId = 1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(sampleCharacter, state.character)
        coVerify(exactly = 1) { getCharacterDetailUseCase(1) }
    }

    @Test
    fun `init handles error when use case fails`() = runTest(testDispatcher) {
        val errorMessage = "Character not found"
        coEvery { getCharacterDetailUseCase(1) } returns Result.Error(Exception(errorMessage))

        val viewModel = createViewModel(characterId = 1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.character)
        assertEquals(errorMessage, state.error)
        coVerify(exactly = 1) { getCharacterDetailUseCase(1) }
    }

    @Test
    fun `Retry intent triggers reloading character detail`() = runTest(testDispatcher) {
        coEvery { getCharacterDetailUseCase(1) } returns
            Result.Error(Exception("Network error")) andThen
            Result.Success(sampleCharacter)

        val viewModel = createViewModel(characterId = 1)
        advanceUntilIdle()
        assertEquals("Network error", viewModel.uiState.value.error)

        viewModel.handleIntent(DetailUiIntent.Retry)
        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertNull(finalState.error)
        assertEquals(sampleCharacter, finalState.character)
        coVerify(exactly = 2) { getCharacterDetailUseCase(1) }
    }

    @Test
    fun `BackClicked intent emits NavigateBack effect`() = runTest(testDispatcher) {
        coEvery { getCharacterDetailUseCase(1) } returns Result.Success(sampleCharacter)

        val viewModel = createViewModel(characterId = 1)

        viewModel.uiEffect.test {
            viewModel.handleIntent(DetailUiIntent.BackClicked)
            assertEquals(DetailUiEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
