package com.davcres.rickandmorty.di

import com.davcres.rickandmorty.core.di.coreModule
import com.davcres.rickandmorty.data.di.dataModule
import com.davcres.rickandmorty.domain.usecase.GetCharacterDetailUseCase
import com.davcres.rickandmorty.domain.usecase.SearchCharactersUseCase
import com.davcres.rickandmorty.feature.characters.di.charactersFeatureModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::SearchCharactersUseCase)
    factoryOf(::GetCharacterDetailUseCase)
}

val appModules: List<Module> = listOf(
    coreModule,
    domainModule,
    dataModule,
    charactersFeatureModule,
)
