package com.davcres.template.di

import com.davcres.template.core.di.coreModule
import com.davcres.template.data.di.dataModule
import com.davcres.template.domain.usecase.GetCharacterDetailUseCase
import com.davcres.template.domain.usecase.SearchCharactersUseCase
import com.davcres.template.feature.characters.di.charactersFeatureModule
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
