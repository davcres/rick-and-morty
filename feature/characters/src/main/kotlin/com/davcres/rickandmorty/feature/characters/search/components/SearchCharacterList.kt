package com.davcres.rickandmorty.feature.characters.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.domain.model.Character

private val CARD_SPACING = 12.dp
private val SCREEN_PADDING = 16.dp
private val SPACING_SMALL = 8.dp

@Composable
fun SearchCharacterList(
    characters: List<Character>,
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

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = SCREEN_PADDING, vertical = SPACING_SMALL),
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
    }
}
