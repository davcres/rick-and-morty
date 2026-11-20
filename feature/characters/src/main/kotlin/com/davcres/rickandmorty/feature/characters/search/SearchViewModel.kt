package com.davcres.rickandmorty.feature.characters.search

import com.davcres.rickandmorty.core.viewmodels.BaseViewModel
import com.davcres.rickandmorty.domain.usecase.SearchCharactersUseCase

class SearchViewModel(
    @Suppress("UnusedPrivateProperty")
    private val searchCharactersUseCase: SearchCharactersUseCase,
) : BaseViewModel<SearchUiState, SearchUiIntent, SearchUiEffect>(SearchUiState()) {

    override fun handleIntent(intent: SearchUiIntent) {
        when (intent) {
            is SearchUiIntent.QueryChanged -> onQueryChanged(intent.query)
            is SearchUiIntent.CharacterClicked -> sendEffect(SearchUiEffect.NavigateToDetail(intent.characterId))
            is SearchUiIntent.ClearSearch -> onClearSearch()
        }
    }

    private fun onQueryChanged(query: String) {
        updateState { copy(query = query) }
    }

    private fun onClearSearch() {
        updateState { copy(query = "", characters = emptyList(), error = null) }
    }
}
