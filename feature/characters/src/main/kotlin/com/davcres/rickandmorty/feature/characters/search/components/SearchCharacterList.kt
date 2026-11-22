package com.davcres.rickandmorty.feature.characters.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.ui.Button
import com.davcres.rickandmorty.core.ui.ButtonStyle
import com.davcres.rickandmorty.core.ui.Text
import com.davcres.rickandmorty.core.ui.UiText
import com.davcres.rickandmorty.domain.model.Character
import com.davcres.rickandmorty.feature.characters.R

private val CARD_SPACING = 12.dp
private val SCREEN_PADDING = 16.dp
private val SPACING_SMALL = 8.dp
private val SPACING_MEDIUM = 16.dp
private val PROGRESS_SIZE = 32.dp
private val PROGRESS_STROKE = 3.dp
private val FOOTER_MIN_HEIGHT = 110.dp
private const val PAGINATION_BUFFER_ITEMS = 3

@Composable
fun SearchCharacterList(
    characters: List<Character>,
    canPaginate: Boolean,
    isLoadingMore: Boolean,
    paginationError: UiText?,
    onLoadMore: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItemIndex >= totalItems - PAGINATION_BUFFER_ITEMS
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && canPaginate && !isLoadingMore && paginationError == null) {
            onLoadMore()
        }
    }

    LaunchedEffect(paginationError) {
        if (paginationError != null) {
            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems > 0) {
                listState.animateScrollToItem(totalItems - 1)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = SCREEN_PADDING,
            top = SPACING_SMALL,
            end = SCREEN_PADDING,
            bottom = SPACING_MEDIUM,
        ),
        verticalArrangement = Arrangement.spacedBy(CARD_SPACING),
    ) {
        items(
            items = characters,
            key = { it.id },
        ) { character ->
            CharacterCard(
                character = character,
                onClick = { onCharacterClick(character.id) },
            )
        }

        if (isLoadingMore || paginationError != null) {
            item(key = "pagination_footer") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = FOOTER_MIN_HEIGHT)
                        .padding(vertical = SPACING_MEDIUM),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoadingMore) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(PROGRESS_SIZE),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = PROGRESS_STROKE,
                        )
                    } else if (paginationError != null) {
                        PaginationErrorItem(
                            error = paginationError,
                            onRetry = onLoadMore,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaginationErrorItem(
    error: UiText,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = error.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(SPACING_SMALL))
        Button(
            text = stringResource(R.string.pagination_retry),
            onClick = onRetry,
            buttonStyle = ButtonStyle.SECONDARY,
        )
    }
}
