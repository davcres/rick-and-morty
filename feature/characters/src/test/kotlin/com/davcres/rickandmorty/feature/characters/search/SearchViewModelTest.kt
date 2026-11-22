package com.davcres.rickandmorty.feature.characters.search

import app.cash.turbine.test
import com.davcres.rickandmorty.core.ui.UiText
import com.davcres.rickandmorty.domain.model.AppError
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.domain.model.PagedCharacters
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

    private val sampleCharacter1 = Character(
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

    private val sampleCharacter2 = Character(
        id = 2,
        name = "Morty Smith",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = "Earth",
        location = "Citadel",
        imageUrl = "url2",
        episodeCount = 10,
    )

    private val samplePagedCharacters = PagedCharacters(
        characters = listOf(sampleCharacter1),
        page = 1,
        totalPages = 2,
        hasNextPage = true,
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
        assertFalse(state.isLoadingMore)
        assertFalse(state.isEmpty)
        assertEquals("", state.query)
        assertEquals(1, state.currentPage)
        assertFalse(state.canPaginate)
        assertTrue(state.characters.isEmpty())
        assertNull(state.error)
        assertNull(state.paginationError)
    }

    @Test
    fun `QueryChanged with blank query should reset state without calling usecase`() = runTest(testDispatcher) {
        viewModel.handleIntent(SearchUiIntent.QueryChanged("   "))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("   ", state.query)
        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertTrue(state.characters.isEmpty())
        assertNull(state.error)
        assertNull(state.paginationError)
        coVerify(exactly = 0) { searchCharactersUseCase(any(), any()) }
    }

    @Test
    fun `QueryChanged should debounce and update state with characters on success`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick", 1) } returns Result.Success(samplePagedCharacters)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))

        // Before debounce delay (300ms)
        advanceTimeBy(150)
        coVerify(exactly = 0) { searchCharactersUseCase(any(), any()) }

        // After debounce delay
        advanceTimeBy(160)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Rick", state.query)
        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertEquals(listOf(sampleCharacter1), state.characters)
        assertEquals(1, state.currentPage)
        assertTrue(state.canPaginate)
        assertNull(state.error)
        assertNull(state.paginationError)
        assertFalse(state.isEmpty)
        coVerify(exactly = 1) { searchCharactersUseCase("Rick", 1) }
    }

    @Test
    fun `QueryChanged cancels previous query on rapid typing`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick", 1) } returns Result.Success(samplePagedCharacters)

        // User rapidly typing "R", "Ri", "Rick"
        viewModel.handleIntent(SearchUiIntent.QueryChanged("R"))
        advanceTimeBy(100)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Ri"))
        advanceTimeBy(100)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        // Only the final query "Rick" should be executed
        coVerify(exactly = 0) { searchCharactersUseCase("R", any()) }
        coVerify(exactly = 0) { searchCharactersUseCase("Ri", any()) }
        coVerify(exactly = 1) { searchCharactersUseCase("Rick", 1) }

        val state = viewModel.uiState.value
        assertEquals("Rick", state.query)
        assertEquals(listOf(sampleCharacter1), state.characters)
    }

    @Test
    fun `QueryChanged should update error state when usecase fails`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick", 1) } returns Result.Error(AppError.Network)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Rick", state.query)
        assertFalse(state.isLoading)
        assertTrue(state.characters.isEmpty())
        assertEquals(UiText.StringResource(R.string.error_network_message), state.error)
        coVerify(exactly = 1) { searchCharactersUseCase("Rick", 1) }
    }

    @Test
    fun `QueryChanged should show empty state when no characters found`() = runTest(testDispatcher) {
        val emptyPaged = PagedCharacters(characters = emptyList(), page = 1, totalPages = 0, hasNextPage = false)
        coEvery { searchCharactersUseCase("UnknownQuery", 1) } returns Result.Success(emptyPaged)

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
    fun `LoadMore should append next page characters and increment currentPage`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick", 1) } returns Result.Success(samplePagedCharacters)
        val pageTwo = PagedCharacters(
            characters = listOf(sampleCharacter2),
            page = 2,
            totalPages = 2,
            hasNextPage = false,
        )
        coEvery { searchCharactersUseCase("Rick", 2) } returns Result.Success(pageTwo)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.LoadMore)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.characters.size)
        assertEquals(listOf(sampleCharacter1, sampleCharacter2), state.characters)
        assertEquals(2, state.currentPage)
        assertFalse(state.canPaginate)
        assertFalse(state.isLoadingMore)
        assertNull(state.paginationError)
        coVerify(exactly = 1) { searchCharactersUseCase("Rick", 2) }
    }

    @Test
    fun `LoadMore when canPaginate is false should not trigger useCase`() = runTest(testDispatcher) {
        val lastPage = PagedCharacters(
            characters = listOf(sampleCharacter1),
            page = 1,
            totalPages = 1,
            hasNextPage = false,
        )
        coEvery { searchCharactersUseCase("Rick", 1) } returns Result.Success(lastPage)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.LoadMore)
        advanceUntilIdle()

        coVerify(exactly = 0) { searchCharactersUseCase("Rick", 2) }
    }

    @Test
    fun `LoadMore error should set paginationError without clearing existing characters`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick", 1) } returns Result.Success(samplePagedCharacters)
        coEvery { searchCharactersUseCase("Rick", 2) } returns Result.Error(AppError.Network)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.LoadMore)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(sampleCharacter1), state.characters)
        assertFalse(state.isLoadingMore)
        assertEquals(UiText.StringResource(R.string.error_network_message), state.paginationError)
    }

    @Test
    fun `Retry should re-execute search when characters empty`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick", 1) } returns Result.Success(samplePagedCharacters)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.Retry)
        advanceUntilIdle()

        coVerify(exactly = 2) { searchCharactersUseCase("Rick", 1) }
        val state = viewModel.uiState.value
        assertEquals(listOf(sampleCharacter1), state.characters)
    }

    @Test
    fun `Retry when paginationError exists should retry LoadMore`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick", 1) } returns Result.Success(samplePagedCharacters)
        coEvery { searchCharactersUseCase("Rick", 2) } returns Result.Error(AppError.Network)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.LoadMore)
        advanceUntilIdle()

        val pageTwo = PagedCharacters(
            characters = listOf(sampleCharacter2),
            page = 2,
            totalPages = 2,
            hasNextPage = false,
        )
        coEvery { searchCharactersUseCase("Rick", 2) } returns Result.Success(pageTwo)

        viewModel.handleIntent(SearchUiIntent.Retry)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(sampleCharacter1, sampleCharacter2), state.characters)
        assertNull(state.paginationError)
        coVerify(exactly = 2) { searchCharactersUseCase("Rick", 2) }
    }

    @Test
    fun `ClearSearch should reset query and characters`() = runTest(testDispatcher) {
        coEvery { searchCharactersUseCase("Rick", 1) } returns Result.Success(samplePagedCharacters)

        viewModel.handleIntent(SearchUiIntent.QueryChanged("Rick"))
        advanceTimeBy(310)
        advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.ClearSearch)

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.characters.isEmpty())
        assertNull(state.error)
        assertNull(state.paginationError)
        assertEquals(1, state.currentPage)
        assertFalse(state.canPaginate)
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
