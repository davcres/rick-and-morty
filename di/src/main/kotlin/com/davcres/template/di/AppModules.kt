package com.davcres.template.di

import com.davcres.template.data.di.dataModule
import com.davcres.template.domain.di.domainModule
import com.davcres.template.feature.characters.di.charactersFeatureModule
import org.koin.core.module.Module

val appModules: List<Module> = listOf(
    domainModule,
    dataModule,
    charactersFeatureModule,
)
