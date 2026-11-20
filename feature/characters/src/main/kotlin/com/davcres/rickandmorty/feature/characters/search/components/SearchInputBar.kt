package com.davcres.rickandmorty.feature.characters.search.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.davcres.rickandmorty.core.ui.TextField
import com.davcres.rickandmorty.core.ui.extensions.modifiers.bounceClick
import com.davcres.rickandmorty.feature.characters.R

private val SEARCH_FIELD_CORNER_RADIUS = 16.dp

@Composable
fun SearchInputBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier.bounceClick(),
        placeholder = stringResource(R.string.search_placeholder),
        contentDescription = stringResource(R.string.search_content_description),
        hasClearIcon = true,
        cornerRadius = SEARCH_FIELD_CORNER_RADIUS,
        leadingIcon = Icons.Default.Search,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
    )
}
