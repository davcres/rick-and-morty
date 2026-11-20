package com.davcres.template.domain.di

import com.davcres.template.domain.usecase.GetCharacterDetailUseCase
import com.davcres.template.domain.usecase.SearchCharactersUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { SearchCharactersUseCase(get()) }
    factory { GetCharacterDetailUseCase(get()) }
}
