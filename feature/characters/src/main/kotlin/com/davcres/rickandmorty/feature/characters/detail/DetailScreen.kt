package com.davcres.rickandmorty.feature.characters.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davcres.rickandmorty.core.ui.Button
import com.davcres.rickandmorty.core.ui.Text
import com.davcres.rickandmorty.core.ui.auxiliary.ButtonStyle
import com.davcres.rickandmorty.feature.characters.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val SPACING_MEDIUM = 16.dp
private val BUTTON_PADDING_HORIZONTAL = 20.dp
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
        characterId = characterId,
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@Composable
fun DetailContent(
    characterId: Int,
    state: DetailUiState,
    onIntent: (DetailUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.padding(SPACING_MEDIUM),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.detail_title, characterId),
                    style = MaterialTheme.typography.titleLarge,
                )
                if (state.isLoading) {
                    Spacer(modifier = Modifier.height(SPACING_MEDIUM))
                    Text(
                        text = stringResource(R.string.detail_loading),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.height(SPACING_MEDIUM))
                Button(
                    text = stringResource(R.string.action_back),
                    onClick = { onIntent(DetailUiIntent.BackClicked) },
                    buttonStyle = ButtonStyle.SECONDARY,
                    contentPadding = PaddingValues(
                        horizontal = BUTTON_PADDING_HORIZONTAL,
                        vertical = BUTTON_PADDING_VERTICAL,
                    ),
                )
            }
        }
    }
}
