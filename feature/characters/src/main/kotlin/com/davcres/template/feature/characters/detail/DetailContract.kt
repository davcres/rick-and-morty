package com.davcres.template.feature.characters.detail

import com.davcres.template.domain.model.Character

data class DetailUiState(
    val isLoading: Boolean = false,
    val character: Character? = null,
    val error: String? = null,
)

sealed interface DetailUiIntent {
    data object BackClicked : DetailUiIntent
    data class LoadCharacter(val characterId: Int) : DetailUiIntent
}

sealed interface DetailUiEffect {
    data object NavigateBack : DetailUiEffect
}
