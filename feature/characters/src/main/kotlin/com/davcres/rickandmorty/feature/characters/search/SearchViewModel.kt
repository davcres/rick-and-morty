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

    override fun handleIntent(intent: SearchUiIntent) {
        when (intent) {
            is SearchUiIntent.QueryChanged -> onQueryChanged(intent.query)
            is SearchUiIntent.CharacterClicked -> sendEffect(SearchUiEffect.NavigateToDetail(intent.characterId))
            is SearchUiIntent.ClearSearch -> onClearSearch()
            is SearchUiIntent.Retry -> onRetry()
        }
    }

    private fun onQueryChanged(query: String) {
        updateState { copy(query = query, error = null) }
        searchJob?.cancel()

        if (query.isBlank()) {
            updateState { copy(isLoading = false, characters = emptyList(), error = null) }
            return
        }

        searchJob = viewModelScope.launch(dispatcherProvider.io) {
            delay(DEBOUNCE_DELAY_MILLIS)
            executeSearch(query.trim())
        }
    }

    private fun onRetry() {
        val query = currentState.query
        if (query.isNotBlank()) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch(dispatcherProvider.io) {
                executeSearch(query.trim())
            }
        }
    }

    private suspend fun executeSearch(query: String) {
        updateState { copy(isLoading = true, error = null) }
        when (val result = searchCharactersUseCase(query)) {
            is Result.Success -> {
                updateState {
                    copy(
                        isLoading = false,
                        characters = result.data,
                        error = null,
                    )
                }
            }
            is Result.Error -> {
                updateState {
                    copy(
                        isLoading = false,
                        characters = emptyList(),
                        error = result.error.toUiText(),
                    )
                }
            }
        }
    }

    private fun onClearSearch() {
        searchJob?.cancel()
        updateState { copy(query = "", isLoading = false, characters = emptyList(), error = null) }
    }
}
