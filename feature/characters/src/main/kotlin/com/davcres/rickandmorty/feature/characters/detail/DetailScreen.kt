package com.davcres.rickandmorty.feature.characters.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davcres.rickandmorty.core.ui.Button
import com.davcres.rickandmorty.core.ui.auxiliary.ButtonStyle
import com.davcres.rickandmorty.core.ui.theme.RickAndMortyTheme
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.feature.characters.R
import com.davcres.rickandmorty.feature.characters.detail.components.DetailErrorView
import com.davcres.rickandmorty.feature.characters.detail.components.DetailHeaderView
import com.davcres.rickandmorty.feature.characters.detail.components.DetailInfoCard
import com.davcres.rickandmorty.feature.characters.detail.components.DetailLoadingView
import com.davcres.rickandmorty.feature.characters.detail.components.DetailTopBar
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val SCREEN_PADDING = 16.dp
private val SECTION_SPACING = 20.dp
private val BUTTON_PADDING_HORIZONTAL = 24.dp
private val BUTTON_PADDING_VERTICAL = 10.dp

@Composable
fun DetailScreen(
    characterId: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(characterId) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is DetailUiEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    DetailContent(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@Composable
fun DetailContent(
    state: DetailUiState,
    onIntent: (DetailUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            DetailTopBar(
                onBackClicked = { onIntent(DetailUiIntent.BackClicked) },
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                when {
                    state.isLoading -> {
                        DetailLoadingView()
                    }
                    state.error != null && state.character == null -> {
                        DetailErrorView(
                            error = state.error,
                            onRetry = { onIntent(DetailUiIntent.Retry) },
                        )
                    }
                    state.character != null -> {
                        DetailSuccessContent(
                            character = state.character,
                            onBack = { onIntent(DetailUiIntent.BackClicked) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSuccessContent(
    character: Character,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SECTION_SPACING),
    ) {
        DetailHeaderView(character = character)

        DetailInfoCard(character = character)

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            text = stringResource(R.string.action_back),
            onClick = onBack,
            buttonStyle = ButtonStyle.SECONDARY,
            buttonModifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = BUTTON_PADDING_HORIZONTAL,
                vertical = BUTTON_PADDING_VERTICAL,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailContentLoadingPreview() {
    RickAndMortyTheme {
        DetailContent(
            state = DetailUiState(isLoading = true),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailContentSuccessPreview() {
    RickAndMortyTheme {
        DetailContent(
            state = DetailUiState(
                character = Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    species = "Human",
                    type = "Scientist",
                    gender = "Male",
                    origin = "Earth (C-137)",
                    location = "Citadel of Ricks",
                    imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                    episodeCount = 51,
                )
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailContentErrorPreview() {
    RickAndMortyTheme {
        DetailContent(
            state = DetailUiState(error = "Network connection timed out"),
            onIntent = {},
        )
    }
}
