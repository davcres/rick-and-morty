package com.davcres.rickandmorty.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface NavDestination : NavKey {
    @Serializable
    data object Search : NavDestination

    @Serializable
    data class Detail(val characterId: Int) : NavDestination
}
