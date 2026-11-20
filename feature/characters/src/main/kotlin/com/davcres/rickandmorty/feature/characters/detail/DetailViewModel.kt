package com.davcres.rickandmorty.feature.characters.detail

import androidx.lifecycle.viewModelScope
import com.davcres.rickandmorty.core.common.DispatcherProvider
import com.davcres.rickandmorty.core.viewmodels.BaseViewModel
import com.davcres.rickandmorty.domain.model.Result
import com.davcres.rickandmorty.domain.usecase.GetCharacterDetailUseCase
import kotlinx.coroutines.launch

class DetailViewModel(
    private val characterId: Int,
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : BaseViewModel<DetailUiState, DetailUiIntent, DetailUiEffect>(DetailUiState()) {

    init {
        handleIntent(DetailUiIntent.LoadCharacter(characterId))
    }

    override fun handleIntent(intent: DetailUiIntent) {
        when (intent) {
            is DetailUiIntent.BackClicked -> sendEffect(DetailUiEffect.NavigateBack)
            is DetailUiIntent.LoadCharacter -> loadCharacter(intent.characterId)
            is DetailUiIntent.Retry -> loadCharacter(characterId)
        }
    }

    private fun loadCharacter(id: Int) {
        updateState { copy(isLoading = true, error = null) }
        viewModelScope.launch(dispatcherProvider.io) {
            when (val result = getCharacterDetailUseCase(id)) {
                is Result.Success -> {
                    updateState {
                        copy(
                            isLoading = false,
                            character = result.data,
                            error = null,
                        )
                    }
                }
                is Result.Error -> {
                    updateState {
                        copy(
                            isLoading = false,
                            error = result.exception.message ?: "Failed to load character details",
                        )
                    }
                }
            }
        }
    }
}
