package com.davcres.rickandmorty.feature.characters.search

import app.cash.turbine.test
import com.davcres.rickandmorty.core.ui.UiText
import com.davcres.rickandmorty.domain.model.AppError
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.usecase.SearchCharactersUseCase
import com.davcres.rickandmorty.feature.characters.R
import com.davcres.rickandmorty.feature.characters.util.FakeDispatcherProvider
import com.davcres.rickandmorty.feature.characters.util.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension(testDispatcher)

    private lateinit var searchCharactersUseCase: SearchCharactersUseCase
    private lateinit var dispatchers: FakeDispatcherProvider
    private lateinit var viewModel: SearchViewModel

    private val sampleCharacters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = "Earth",
            location = "Citadel",
            imageUrl = "url",
            episodeCount = 10,
        )
    )

    @BeforeEach
    fun setUp() {
        searchCharactersUseCase = mockk()
        dispatchers = FakeDispatcherProvider(testDispatcher)
        viewModel = SearchViewModel(
            searchCharactersUseCase = searchCharactersUseCase,
            dispatcherProvider = dispatchers,
        )
    }

    @Test
    fun `initial state should be initial and empty`() {
        val state = viewModel.uiState.value

        assertTrue(state.isInitial)
        assertFalse(state.isLoading)
        assertFalse(state.isEmpty)
        assertEquals("", state.query)
        assertTrue(state.characters.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `QueryChanged with blank query should reset state without calling usecase`() = runTest(testDispatcher) {
        viewModel.handleIntent(SearchUiIntent.QueryChanged("   "))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("   ", state.query)
        assertFalse(state.isLoading)
        assertTrue(state.characters.isEmpty())
        assertNull(state.error)
        coVerify(exactly = 0) { searchCharactersUseCase(any()) }
    }

    @Test
    fun `QueryChanged should debounce and update state with characters on success`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick") } returns Result.Success(sampleCharacters)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))

        // Before debounce delay (300ms)
        advanceTimeBy(150)
        coVerify(exactly = 0) { searchCharactersUseCase(any()) }

        // After debounce delay
        advanceTimeBy(160)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Rick", state.query)
        assertFalse(state.isLoading)
        assertEquals(sampleCharacters, state.characters)
        assertNull(state.error)
        assertFalse(state.isEmpty)
        coVerify(exactly = 1) { searchCharactersUseCase("Rick") }
    }

    @Test
    fun `QueryChanged cancels previous query on rapid typing`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick") } returns Result.Success(sampleCharacters)

        // User rapidly typing "R", "Ri", "Rick"
        viewModel.handleIntent(SearchUiIntent.QueryChanged("R"))
        advanceTimeBy(100)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Ri"))
        advanceTimeBy(100)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        // Only the final query "Rick" should be executed
        coVerify(exactly = 0) { searchCharactersUseCase("R") }
        coVerify(exactly = 0) { searchCharactersUseCase("Ri") }
        coVerify(exactly = 1) { searchCharactersUseCase("Rick") }

        val state = viewModel.uiState.value
        assertEquals("Rick", state.query)
        assertEquals(sampleCharacters, state.characters)
    }

    @Test
    fun `QueryChanged should update error state when usecase fails`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick") } returns Result.Error(AppError.Network)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Rick", state.query)
        assertFalse(state.isLoading)
        assertTrue(state.characters.isEmpty())
        assertEquals(UiText.StringResource(R.string.error_network_message), state.error)
        coVerify(exactly = 1) { searchCharactersUseCase("Rick") }
    }

    @Test
    fun `QueryChanged should show empty state when no characters found`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("UnknownQuery") } returns Result.Success(emptyList())

        viewModel.handleIntent(SearchUiIntent.QueryChanged("UnknownQuery"))
        advanceTimeBy(310)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("UnknownQuery", state.query)
        assertFalse(state.isLoading)
        assertTrue(state.characters.isEmpty())
        assertTrue(state.isEmpty)
        assertNull(state.error)
    }

    @Test
    fun `Retry should re-execute search without debounce delay`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick") } returns Result.Success(sampleCharacters)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.Retry)
        advanceUntilIdle()

        coVerify(exactly = 2) { searchCharactersUseCase("Rick") }
        val state = viewModel.uiState.value
        assertEquals(sampleCharacters, state.characters)
    }

    @Test
    fun `ClearSearch should reset query and characters`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick") } returns Result.Success(sampleCharacters)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.ClearSearch)

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.characters.isEmpty())
        assertNull(state.error)
        assertTrue(state.isInitial)
    }

    @Test
    fun `CharacterClicked should emit NavigateToDetail effect`() = runTest(testDispatcher) {
        val characterId = 42

        viewModel.uiEffect.test {
            viewModel.handleIntent(SearchUiIntent.CharacterClicked(characterId))

            val effect = awaitItem()
            assertTrue(effect is SearchUiEffect.NavigateToDetail)
            assertEquals(characterId, (effect as SearchUiEffect.NavigateToDetail).characterId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
