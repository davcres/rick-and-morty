package com.davcres.template.feature.characters.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.davcres.template.core.navigation.NavDestination
import com.davcres.template.feature.characters.detail.DetailScreen
import com.davcres.template.feature.characters.search.SearchScreen

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
                    onCharacterClick = { characterId ->
                        backStack.add(NavDestination.Detail(characterId))
                    },
                )
            }
            entry<NavDestination.Detail> { key ->
                DetailScreen(
                    characterId = key.characterId,
                    onBackClick = {
                        if (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        }
                    },
                )
            }
        },
    )
}
