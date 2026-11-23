package com.davcres.rickandmorty.feature.characters.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.davcres.rickandmorty.core.navigation.NavDestination
import com.davcres.rickandmorty.feature.characters.detail.DetailScreen
import com.davcres.rickandmorty.feature.characters.search.SearchScreen

private const val TRANSITION_DURATION_MILLIS = 350

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CharactersNavigation() {
    val backStack = rememberNavBackStack(NavDestination.Search)

    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
        ) {
            NavDisplay(
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                transitionSpec = {
                    ContentTransform(
                        fadeIn(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
                        fadeOut(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
                    )
                },
                popTransitionSpec = {
                    ContentTransform(
                        fadeIn(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
                        fadeOut(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
                    )
                },
                predictivePopTransitionSpec = {
                    ContentTransform(
                        fadeIn(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
                        fadeOut(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
                    )
                },
                entryProvider = entryProvider {
                    entry<NavDestination.Search> {
                        CompositionLocalProvider( // Needed for shared element transitions
                            LocalAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
                        ) {
                            SearchScreen(
                                onNavigateToDetail = { characterId ->
                                    backStack.add(NavDestination.Detail(characterId))
                                },
                            )
                        }
                    }
                    entry<NavDestination.Detail> { key ->
                        CompositionLocalProvider(
                            LocalAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
                        ) {
                            DetailScreen(
                                characterId = key.characterId,
                                onNavigateBack = {
                                    if (backStack.size > 1) {
                                        backStack.removeLastOrNull()
                                    }
                                },
                            )
                        }
                    }
                },
            )
        }
    }
}
