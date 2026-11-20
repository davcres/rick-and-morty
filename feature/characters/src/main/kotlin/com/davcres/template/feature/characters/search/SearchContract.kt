package com.davcres.template.feature.characters.search

import com.davcres.template.domain.model.Character

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val characters: List<Character> = emptyList(),
    val error: String? = null,
)

sealed interface SearchUiIntent {
    data class QueryChanged(val query: String) : SearchUiIntent
    data class CharacterClicked(val characterId: Int) : SearchUiIntent
    data object ClearSearch : SearchUiIntent
}

sealed interface SearchUiEffect {
    data class NavigateToDetail(val characterId: Int) : SearchUiEffect
}
