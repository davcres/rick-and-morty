package com.davcres.template.feature.characters.search

import com.davcres.template.core.viewmodels.BaseViewModel
import com.davcres.template.domain.usecase.SearchCharactersUseCase

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
