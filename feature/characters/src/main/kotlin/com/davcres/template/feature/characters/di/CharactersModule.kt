package com.davcres.template.feature.characters.di

import com.davcres.template.feature.characters.detail.DetailViewModel
import com.davcres.template.feature.characters.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val charactersFeatureModule = module {
    viewModelOf(::SearchViewModel)
    viewModel { params ->
        DetailViewModel(
            characterId = params.get(),
            getCharacterDetailUseCase = get(),
        )
    }
}
