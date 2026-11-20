package com.davcres.rickandmorty.feature.characters.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.davcres.rickandmorty.core.navigation.NavDestination
import com.davcres.rickandmorty.feature.characters.detail.DetailScreen
import com.davcres.rickandmorty.feature.characters.search.SearchScreen

@Composable
fun CharactersNavigation() {
    val backStack = rememberNavBackStack(NavDestination.Search)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            ContentTransform(
                slideInHorizontally(initialOffsetX = { it }),
                slideOutHorizontally(),
            )
        },
        popTransitionSpec = {
            ContentTransform(
                slideInHorizontally(),
                slideOutHorizontally(targetOffsetX = { it }),
            )
        },
        entryProvider = entryProvider {
            entry<NavDestination.Search> {
                SearchScreen(
                    onNavigateToDetail = { characterId ->
                        backStack.add(NavDestination.Detail(characterId))
                    },
                )
            }
            entry<NavDestination.Detail> { key ->
                DetailScreen(
                    characterId = key.characterId,
                    onNavigateBack = {
                        if (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        }
                    },
                )
            }
        },
    )
}
