package com.davcres.rickandmorty.feature.characters.search

import com.davcres.rickandmorty.core.ui.UiText
import com.davcres.rickandmorty.domain.model.Character

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val characters: List<Character> = emptyList(),
    val currentPage: Int = 1,
    val canPaginate: Boolean = false,
    val error: UiText? = null,
    val paginationError: UiText? = null,
) {
    val isInitial: Boolean get() = query.isBlank() && !isLoading && characters.isEmpty() && error == null
    val isEmpty: Boolean get() = query.isNotBlank() && !isLoading && characters.isEmpty() && error == null
}

sealed interface SearchUiIntent {
    data class QueryChanged(val query: String) : SearchUiIntent
    data class CharacterClicked(val characterId: Int) : SearchUiIntent
    data object ClearSearch : SearchUiIntent
    data object Retry : SearchUiIntent
    data object LoadMore : SearchUiIntent
}

sealed interface SearchUiEffect {
    data class NavigateToDetail(val characterId: Int) : SearchUiEffect
}
