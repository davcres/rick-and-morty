package com.davcres.rickandmorty.feature.characters.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davcres.rickandmorty.core.ui.extensions.modifiers.clearFocusOnTap
import com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.feature.characters.search.components.SearchCharacterList
import com.davcres.rickandmorty.feature.characters.search.components.SearchEmptyView
import com.davcres.rickandmorty.feature.characters.search.components.SearchErrorView
import com.davcres.rickandmorty.feature.characters.search.components.SearchInitialView
import com.davcres.rickandmorty.feature.characters.search.components.SearchInputBar
import com.davcres.rickandmorty.feature.characters.search.components.SearchLoadingView
import org.koin.androidx.compose.koinViewModel

private val SCREEN_PADDING = 16.dp
private val SPACING_SMALL = 8.dp

@Composable
fun SearchScreen(
    onNavigateToDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SearchUiEffect.NavigateToDetail -> onNavigateToDetail(effect.characterId)
            }
        }
    }

    SearchContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@Composable
fun SearchContent(
    state: SearchUiState,
    onIntent: (SearchUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .clearFocusOnTap(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clearFocusOnTap(),
        ) {
            SearchInputBar(
                query = state.query,
                onQueryChanged = { onIntent(SearchUiIntent.QueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SCREEN_PADDING, vertical = SPACING_SMALL),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                when {
                    state.isLoading && state.characters.isEmpty() -> {
                        SearchLoadingView()
                    }
                    state.error != null && state.characters.isEmpty() -> {
                        SearchErrorView(
                            error = state.error,
                            onRetry = { onIntent(SearchUiIntent.Retry) },
                        )
                    }
                    state.isInitial -> {
                        SearchInitialView()
                    }
                    state.isEmpty -> {
                        SearchEmptyView(query = state.query)
                    }
                    else -> {
                        SearchCharacterList(
                            characters = state.characters,
                            onCharacterClick = { onIntent(SearchUiIntent.CharacterClicked(it)) },
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchContentInitialPreview() {
    RickAndMortyTheme {
        SearchContent(
            state = SearchUiState(),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchContentSuccessPreview() {
    RickAndMortyTheme {
        SearchContent(
            state = SearchUiState(
                query = "Rick",
                characters = listOf(
                    Character(
                        id = 1,
                        name = "Rick Sanchez",
                        status = "Alive",
                        species = "Human",
                        type = "",
                        gender = "Male",
                        origin = "Earth (C-137)",
                        location = "Citadel of Ricks",
                        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                        episodeCount = 51,
                    ),
                    Character(
                        id = 2,
                        name = "Morty Smith",
                        status = "Alive",
                        species = "Human",
                        type = "",
                        gender = "Male",
                        origin = "Earth (C-137)",
                        location = "Citadel of Ricks",
                        imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
                        episodeCount = 51,
                    ),
                ),
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchContentErrorPreview() {
    RickAndMortyTheme {
        SearchContent(
            state = SearchUiState(
                query = "Rick",
                error = "Network connection failed",
            ),
            onIntent = {},
        )
    }
}
