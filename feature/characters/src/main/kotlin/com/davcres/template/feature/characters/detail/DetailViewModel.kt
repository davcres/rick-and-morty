package com.davcres.template.feature.characters.detail

import com.davcres.template.core.viewmodels.BaseViewModel
import com.davcres.template.domain.usecase.GetCharacterDetailUseCase

class DetailViewModel(
    private val characterId: Int,
    @Suppress("UnusedPrivateProperty")
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
) : BaseViewModel<DetailUiState, DetailUiIntent, DetailUiEffect>(DetailUiState()) {

    init {
        handleIntent(DetailUiIntent.LoadCharacter(characterId))
    }

    override fun handleIntent(intent: DetailUiIntent) {
        when (intent) {
            is DetailUiIntent.BackClicked -> sendEffect(DetailUiEffect.NavigateBack)
            is DetailUiIntent.LoadCharacter -> loadCharacter(intent.characterId)
        }
    }

    private fun loadCharacter(@Suppress("UnusedParameter") id: Int) {
        updateState { copy(isLoading = true) }
    }
}
