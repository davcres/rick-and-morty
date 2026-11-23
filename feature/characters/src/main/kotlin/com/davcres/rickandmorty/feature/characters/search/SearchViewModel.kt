package com.davcres.rickandmorty.feature.characters.search

import androidx.lifecycle.viewModelScope
import com.davcres.rickandmorty.core.common.DispatcherProvider
import com.davcres.rickandmorty.core.viewmodels.BaseViewModel
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.usecase.SearchCharactersUseCase
import com.davcres.rickandmorty.feature.characters.mapper.toUiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DEBOUNCE_DELAY_MILLIS = 300L

class SearchViewModel(
    private val searchCharactersUseCase: SearchCharactersUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : BaseViewModel<SearchUiState, SearchUiIntent, SearchUiEffect>(SearchUiState()) {

    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null

    override fun handleIntent(intent: SearchUiIntent) {
        when (intent) {
            is SearchUiIntent.QueryChanged -> onQueryChanged(intent.query)
            is SearchUiIntent.CharacterClicked -> sendEffect(SearchUiEffect.NavigateToDetail(intent.characterId))
            is SearchUiIntent.ClearSearch -> onClearSearch()
            is SearchUiIntent.Retry -> onRetry()
            is SearchUiIntent.LoadMore -> onLoadMore()
        }
    }

    private fun onQueryChanged(query: String) {
        updateState {
            copy(
                query = query,
                error = null,
                paginationError = null,
                currentPage = 1,
                canPaginate = false,
            )
        }
        searchJob?.cancel()
        loadMoreJob?.cancel()

        if (query.isBlank()) {
            updateState {
                copy(
                    isLoading = false,
                    isLoadingMore = false,
                    characters = emptyList(),
                    error = null,
                    paginationError = null,
                )
            }
            return
        }

        searchJob = viewModelScope.launch(dispatcherProvider.io) {
            delay(DEBOUNCE_DELAY_MILLIS)
            executeSearch(query.trim(), page = 1)
        }
    }

    private fun onRetry() {
        val query = currentState.query
        if (query.isNotBlank()) {
            if (currentState.paginationError != null) {
                onLoadMore()
            } else {
                searchJob?.cancel()
                loadMoreJob?.cancel()
                searchJob = viewModelScope.launch(dispatcherProvider.io) {
                    executeSearch(query.trim(), page = 1)
                }
            }
        }
    }

    private fun onLoadMore() {
        if (!currentState.canPaginate || currentState.isLoading || currentState.isLoadingMore) {
            return
        }
        val nextPage = currentState.currentPage + 1
        val query = currentState.query.trim()

        updateState { copy(isLoadingMore = true, paginationError = null) }
        loadMoreJob = viewModelScope.launch(dispatcherProvider.io) {
            when (val result = searchCharactersUseCase(query, nextPage)) {
                is Result.Success -> {
                    updateState {
                        copy(
                            isLoadingMore = false,
                            characters = currentState.characters + result.data.characters,
                            currentPage = result.data.page,
                            canPaginate = result.data.hasNextPage,
                            paginationError = null,
                        )
                    }
                }
                is Result.Error -> {
                    updateState {
                        copy(
                            isLoadingMore = false,
                            paginationError = result.error.toUiText(),
                        )
                    }
                }
            }
        }
    }

    private suspend fun executeSearch(query: String, page: Int) {
        updateState { copy(isLoading = true, error = null, paginationError = null) }
        when (val result = searchCharactersUseCase(query, page)) {
            is Result.Success -> {
                updateState {
                    copy(
                        isLoading = false,
                        characters = result.data.characters,
                        currentPage = result.data.page,
                        canPaginate = result.data.hasNextPage,
                        error = null,
                        paginationError = null,
                    )
                }
            }
            is Result.Error -> {
                updateState {
                    copy(
                        isLoading = false,
                        characters = emptyList(),
                        currentPage = 1,
                        canPaginate = false,
                        error = result.error.toUiText(),
                        paginationError = null,
                    )
                }
            }
        }
    }

    private fun onClearSearch() {
        searchJob?.cancel()
        loadMoreJob?.cancel()
        updateState {
            copy(
                query = "",
                isLoading = false,
                isLoadingMore = false,
                characters = emptyList(),
                currentPage = 1,
                canPaginate = false,
                error = null,
                paginationError = null,
            )
        }
    }
}
